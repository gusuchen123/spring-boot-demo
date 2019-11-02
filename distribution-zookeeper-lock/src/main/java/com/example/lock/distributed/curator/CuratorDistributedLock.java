package com.example.lock.distributed.curator;

import com.example.lock.distributed.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * {@link InterProcessMutex} 分布式可重入排它锁
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-21 10:54
 */
@Slf4j
public class CuratorDistributedLock implements DistributedLock {

    private CuratorFramework client;
    private InterProcessMutex mutex;

    public CuratorDistributedLock(String zkAddress, int baseSleepTimeMs, int sessionTimeoutMs, int connectionTimeoutMs, int maxRetries, String rootLockNode) {
        // 1.设置重试策略，创建zk客户端
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        client = CuratorFrameworkFactory.newClient(zkAddress, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
        client.start();

        // 2.创建分布式可重入排他锁，监听客户端为client，锁的根节点为 rootLockNode
        mutex = new InterProcessMutex(client, rootLockNode);
    }

    @Override
    public void lock() {
        try {
            // 3.尝试获得锁
            mutex.acquire(3, TimeUnit.SECONDS);
            log.error("【CuratorDistributedLock#lock()】线程【{}】加锁成功...., children:{}", Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlock() {
        try {
            // 4.释放锁
            mutex.release();
            log.error("【CuratorDistributedLock#lock()】线程【{}】释放锁....", Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 5.关闭客户端
            client.close();
        }
    }

    @Override
    public boolean tryLock() {
        return false;
    }
}
