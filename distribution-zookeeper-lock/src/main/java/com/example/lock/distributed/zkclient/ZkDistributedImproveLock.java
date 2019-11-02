package com.example.lock.distributed.zkclient;

import cn.hutool.core.util.StrUtil;
import com.example.lock.distributed.DistributedLock;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * <p>
 * 基于 {@link ZkDistributedLock} 优化
 * 实现原理：取号 + 最小号取lock + watch
 * 利用临时有序节点来实现分布式锁
 * 获取锁：取排队号（创建自己的临时有序节点），然后判断自己是否是最小号，如果是，则获得锁；不是，则注册前一个节点的watcher,阻塞等待；
 * 释放锁：删除自己创建的临时有序节点
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-17 20:15
 */
@Slf4j
public class ZkDistributedImproveLock implements DistributedLock {
    /**
     * 自定义锁的目录
     */
    private final String lockPath;

    private ZkClient client;

    private ThreadLocal<String> currentPath = new ThreadLocal<>();
    private ThreadLocal<String> beforePath = new ThreadLocal<>();

    /**
     * 锁重入计数器
     */
    private ThreadLocal<Integer> reenterCount = ThreadLocal.withInitial(() -> 0);

    public ZkDistributedImproveLock(String zkAddress, String lockPath) {
        Preconditions.checkArgument(StrUtil.isNotBlank(lockPath), "");
        this.lockPath = lockPath;

        client = new ZkClient(zkAddress, 30 * 1000, 10 * 1000);
        client.setZkSerializer(new CustomZkSerializer());

        if (!this.client.exists(lockPath)) {
            try {
                this.client.createPersistent(lockPath, true);
            } catch (ZkNodeExistsException ex) {
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void lock() {
        if (!tryLock()) {
            // 阻塞等待
            waitForLock();
            // 再次尝试加锁
            lock();
        }
    }

    @Override
    public void unlock() {
        log.error("【ZkDistributedImproveLock#unlock()】线程名称:{}, 释放分布式锁:{}", Thread.currentThread().getName(), this.currentPath.get());
        if (reenterCount.get() > 1) {
            // 锁重入次数减去1，释放锁；
            reenterCount.set(reenterCount.get() - 1);
            return;
        }

        // 删除节点
        if (StrUtil.isNotBlank(this.currentPath.get())) {
            this.client.delete(this.currentPath.get());
            this.currentPath.set(StrUtil.EMPTY);
            this.reenterCount.set(0);
        }
        currentPath.remove();
    }

    @Override
    public boolean tryLock() {
        log.error("【ZkDistributedImproveLock#tryLock()】线程名称:{}, 尝试获取分布式锁....", Thread.currentThread().getName());
        boolean isCurrentPathExists = StrUtil.isNotBlank(this.currentPath.get()) && this.client.exists(this.currentPath.get());
        final String delimiter = "/";
        if (!isCurrentPathExists) {
            /*
             * 创建了一个临时顺序节点
             * 用银行取号来表示这个行为吧，相当于每个实例程序先去取号，然后排队等着叫号的场景
             */
            String node = this.client.createEphemeralSequential(lockPath + delimiter, UUID.randomUUID().toString());

            // 记录第一个节点编号
            currentPath.set(node);
            reenterCount.set(0);

            log.error("【ZkDistributedImproveLock#tryLock()】线程名称:{}, 分布锁：{}", Thread.currentThread().getName(), this.currentPath.get());
        }

        // 获取所有的号, 并且进行排序
        List<String> children = this.client.getChildren(lockPath)
                .stream()
                .sorted()
                .collect(Collectors.toList());

        /*
         判断当前节点是否是最小的，和第一个节点编号对比；
         如果当前节点的编号最小，则获得锁，锁重入计数器加1；
         否则 找到当前节点（currentPath）的前一个节点, 设置 beforePath，
         */
        if (currentPath.get().equals(lockPath + delimiter + children.get(0))) {
            // 锁重入计数
            reenterCount.set(reenterCount.get() + 1);
            log.error("【ZkDistributedImproveLock#tryLock()】线程名称:{}, 获取分布式锁:{}", Thread.currentThread().getName(), this.currentPath.get());
            return true;
        } else {
            // 节点编号最小的获得锁
            String currentPathNode = currentPath.get();
            int curIndex = children.indexOf(currentPathNode.substring(lockPath.length() + 1));
            String node = lockPath + delimiter + children.get(curIndex - 1);
            beforePath.set(node);
        }
        return false;
    }

    private void waitForLock() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // 注册watcher
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {
                log.error("【ZkDistributedImproveLock#waitForLock()】线程名称:{}, lockPath:{}, 当前节点数据发生了变化, data:{}", Thread.currentThread().getId(), dataPath, data);
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                log.error("【ZkDistributedImproveLock#waitForLock()】线程名称:{}, 当前节点被删除:lockPath:{}", Thread.currentThread().getName(), dataPath);
                // 唤醒阻塞线程
                countDownLatch.countDown();
                beforePath.remove();
            }
        };
        // beforePath注册监听
        this.client.subscribeDataChanges(this.beforePath.get(), listener);

        // 阻塞自身线程
        if (this.client.exists(this.beforePath.get())) {
            log.error("【ZkDistributedImproveLock#waitForLock()】线程名称:{}, beforePath:{}还存在, 分布式锁没抢到，进入阻塞状态", Thread.currentThread().getName(), this.beforePath.get());
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.error("【ZkDistributedImproveLock#waitForLock()】线程名称:{}, beforePath:{}已删除, 当前线程被唤醒", Thread.currentThread().getName(), this.beforePath.get());
        }

        // 阻塞线程被唤醒后,取消 watcher
        client.unsubscribeDataChanges(this.beforePath.get(), listener);
    }
}
