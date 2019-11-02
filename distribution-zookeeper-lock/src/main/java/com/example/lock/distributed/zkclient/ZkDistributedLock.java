package com.example.lock.distributed.zkclient;

import cn.hutool.core.util.StrUtil;
import com.example.lock.distributed.DistributedLock;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * 自定义zookeeper实现分布式锁;
 * 实现原理：节点不可重名 + watch机制
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-17 19:46
 */
@Slf4j
public class ZkDistributedLock implements DistributedLock {

    /**
     * 自定义锁的目录
     */
    private final String lockPath;
    /**
     * zookeeper客户端
     */
    private ZkClient client;

    public ZkDistributedLock(String zkAddress, String lockPath) {
        Preconditions.checkArgument(StrUtil.isNotBlank(lockPath), "【ZkDistributedLock】lockPath不允许为空");
        this.lockPath = lockPath;

        client = new ZkClient(zkAddress);
        client.setZkSerializer(new CustomZkSerializer());
    }

    @Override
    public void lock() {
        if (!tryLock()) {
            // 没有获得锁,阻塞自身的线程
            waitForLock();
            // 从等待中唤醒，再次尝试获得锁
            lock();
        }
    }

    /**
     * 加锁:尝试创建临时节点
     *
     * @return 是否成功创建临时有序节点
     */
    @Override
    public boolean tryLock() {
        log.error("【ZkDistributedLock#tryLock()】线程名称:{}, 尝试获取分布式锁：{}", Thread.currentThread().getName(), lockPath);
        try {
            client.createEphemeral(lockPath);
        } catch (ZkNodeExistsException ex) {
            return false;
        }
        return true;
    }

    /**
     * 解锁:删除临时节点
     */
    @Override
    public void unlock() {
        log.error("【ZkDistributedLock#unlock()】线程名称:{}, 释放分布式锁:{}", Thread.currentThread().getName(), lockPath);
        client.delete(lockPath);
    }

    private void waitForLock() {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {
                log.error("【ZkDistributedLock】lockPath:{}, 当前节点数据发生了变化, data:{}", dataPath, data);
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                /*
                 唤醒所有的阻塞线程
                 */
                countDownLatch.countDown();
                log.error("【ZkDistributedLock#waitForLock()】线程名称:{}, 当前节点被删除:lockPath:{}", Thread.currentThread().getName(), dataPath);
            }
        };
        client.subscribeDataChanges(lockPath, listener);

        // 阻塞自身线程
        if (this.client.exists(lockPath)) {
            log.error("【ZkDistributedLock#waitForLock()】线程名称:{}, lockPath:{}还存在, 分布式锁没抢到，进入阻塞状态", Thread.currentThread().getName(), this.lockPath);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.error("【ZkDistributedLock#waitForLock()】线程名称:{}, lockPath:{}已删除, 当前线程被唤醒", Thread.currentThread().getName(), this.lockPath);
        }

        // 通知所有阻塞线程，阻塞线程被唤醒后, 取消 watcher
        client.unsubscribeDataChanges(lockPath, listener);
    }

}
