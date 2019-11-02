package com.example.lock.distributed.zookeeper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.lock.distributed.DistributedLock;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-18 17:08
 */
@Slf4j
public class NativeZkDistributedImproveLock implements Watcher, DistributedLock {
    /**
     * 超时时间: 30分钟
     */
    private static final int SESSION_TIMEOUT = 30 * 60 * 1000;
    /**
     * 锁的根节点
     */
    private final String rootLockNode;
    /**
     * 竞争资源，用来生成子节点名称
     */
    private final String lockName;
    /**
     * {@link ZooKeeper}客户端
     */
    private ZooKeeper zk = null;
    /**
     * 当前锁
     */
    private String currentLock;
    /**
     * 等待的锁（前一个锁）
     */
    private String beforeLock;
    /**
     * 计数器（用来在加锁失败时阻塞加锁线程）
     */
    private CountDownLatch countDownLatch;

    /**
     * 1.构造器中创建ZK连接，创建锁的根节点
     *
     * @param zkAddress    Zookeeper连接地址
     * @param rootLockNode 锁的根节点
     * @param lockName     竞争资源，用来生成子节点名称
     */
    public NativeZkDistributedImproveLock(String zkAddress, String rootLockNode, String lockName) {
        Preconditions.checkArgument(StrUtil.isNotBlank(zkAddress), "【NativeZkDistributedImproveLock】zkAddress不能为空");
        Preconditions.checkArgument(StrUtil.isNotBlank(rootLockNode), "【NativeZkDistributedImproveLock】rootLockNode不能为空");
        Preconditions.checkArgument(StrUtil.isNotBlank(lockName), "【NativeZkDistributedImproveLock】lockName不能为空");
        this.rootLockNode = rootLockNode;
        this.lockName = lockName;
        try {
            // 创建 ZK 客户端连接
            zk = new ZooKeeper(zkAddress, SESSION_TIMEOUT, this);

            // 检测锁的根节点是否存在，不存在则创建
            Stat stat = zk.exists(rootLockNode, false);
            if (ObjectUtil.isNull(stat)) {
                zk.create(rootLockNode, StrUtil.EMPTY.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2.加锁方法，先尝试加锁，不能加锁则等待上一个锁的释放
     */
    @Override
    public void lock() {
        if (this.tryLock()) {
            log.error("【NativeZkDistributedImproveLock#lock()】线程【{}】加锁（{}）成功", Thread.currentThread().getName(), this.currentLock);
        } else {
            waitOtherLock(this.beforeLock, SESSION_TIMEOUT);
        }
    }


    /**
     * 3.释放锁
     */
    @Override
    public void unlock() throws InterruptedException {
        try {
            Stat stat = zk.exists(this.currentLock, false);
            if (ObjectUtil.isNotNull(stat)) {
                log.error("【NativeZkDistributedImproveLock#unlock()】线程【{}】释放锁（{}）", Thread.currentThread().getName(), this.currentLock);
                zk.delete(this.currentLock, -1);
                this.currentLock = null;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {
            zk.close();
        }
    }

    /**
     * 4.监听器回调
     *
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        boolean isCountDown = ObjectUtil.isNotNull(countDownLatch) && Event.EventType.NodeDeleted == event.getType();
        if (isCountDown) {
            // 计数器减一，恢复线程操作
            this.countDownLatch.countDown();
        }
    }

    @Override
    public boolean tryLock() {
        // 分隔符
        final String delimiter = "_lock_";
        Preconditions.checkArgument(!this.lockName.contains(delimiter), "【NativeZkDistributedImproveLock#tryLock()】lockName can't contains '_lock_' ");

        try {
            // 创建锁节点（临时有序节点）
            String lockPath = this.rootLockNode + "/" + this.lockName + delimiter;
            this.currentLock = zk.create(lockPath, StrUtil.EMPTY.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            log.error("【NativeZkDistributedImproveLock#tryLock()】线程【{}】创建锁节点（{}）成功，开始竞争。。。", Thread.currentThread().getName(), this.currentLock);

            // 获取所有子节点
            List<String> nodes = zk.getChildren(this.rootLockNode, false);

            // 获取所有竞争lockName的锁, 并且进行自然排序
            List<String> lockNodes = nodes.stream()
                    .filter((node) -> node.split(delimiter)[0].equals(this.lockName)).sorted()
                    .collect(Collectors.toList());

            // 取当前最小节点和当前锁节点比对加锁, 如果 当前锁节点 == 最小节点，则说明 当前锁节点 加锁成功；
            String currentLockPath = this.rootLockNode + "/" + lockNodes.get(0);
            if (StrUtil.equals(this.currentLock, currentLockPath)) {
                return true;
            }

            // 如果 当前锁节点 != 最小节点，则说明 当前锁节点 加锁失败，设置前一个节点为等待锁节点；
            String currentLockNode = this.currentLock.substring(this.currentLock.lastIndexOf("/") + 1);
            int preNodeIndex = Collections.binarySearch(lockNodes, currentLockNode) - 1;
            this.beforeLock = lockNodes.get(preNodeIndex);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 不能获得锁，则阻塞当前进程
     *
     * @param beforeLock     等待的锁
     * @param sessionTimeout 超时时间
     * @return 是否重新获得锁
     */
    private boolean waitOtherLock(String beforeLock, int sessionTimeout) {
        boolean isLock = false;
        try {
            // 监听等待锁节点
            String beforeLockNode = this.rootLockNode + "/" + beforeLock;
            Stat stat = zk.exists(beforeLockNode, true);
            if (ObjectUtil.isNotNull(stat)) {
                log.error("【NativeZkDistributedImproveLock#waitOtherLock()】线程【{}】锁（{}）加锁失败，等待锁（{}）释放...",
                        Thread.currentThread().getName(), this.currentLock, beforeLockNode);

                // 设置计数器，使用计数器阻塞线程
                this.countDownLatch = new CountDownLatch(1);
                isLock = this.countDownLatch.await(sessionTimeout, TimeUnit.MILLISECONDS);
                this.countDownLatch = null;
                if (isLock) {
                    log.error("【NativeZkDistributedImproveLock#waitOtherLock()】线程【{}】锁（{}）加锁成功，锁（{}）已经释放...",
                            Thread.currentThread().getName(), this.currentLock, beforeLockNode);
                } else {
                    log.error("【NativeZkDistributedImproveLock#waitOtherLock()】线程【{}】锁（{}）加锁失败，等待锁（{}）释放...",
                            Thread.currentThread().getName(), this.currentLock, beforeLockNode);
                }

            } else {
                isLock = true;
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return isLock;
    }

}
