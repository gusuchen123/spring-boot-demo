package com.example.lock.distributed;

import com.example.lock.distributed.zkclient.ZkDistributedImproveLock;
import com.example.lock.distributed.zookeeper.NativeZkDistributedImproveLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * {@link NativeZkDistributedImproveLock} 测试
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-20 15:02
 */
@Slf4j
public class ZkDistributedLockTest {

    public static void main(String[] args) {
        // 并发的线程数
        final int nThreads = 5;
        ExecutorService executorService = new ThreadPoolExecutor(
                nThreads,
                nThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        for (int i = 0; i < nThreads; i++) {
            executorService.execute(new ZkDistributedImprovedLockRunnable());
        }

        executorService.shutdown();
    }

    /**
     * 模拟业务逻辑, 业务逻辑只执行时间 1000ms
     */
    private static void doSomething() {
        log.error("【ZkDistributedLockTest#doSomething()】线程【{}】正在运行...", Thread.currentThread().getName());
        try {
            Thread.sleep(6 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link NativeZkDistributedImproveLock} 测试
     */
    private static class NativeZkDistributedImproveLockRunnable implements Runnable {
        final String zkAddress = "192.168.2.20:2181";
        final String rootLockNode = "/locks";
        final String lockName = "NativeZkDistributedImproveLock";

        @Override
        public void run() {
            NativeZkDistributedImproveLock lock = new NativeZkDistributedImproveLock(zkAddress, rootLockNode, lockName);
            try {
                // 1.加锁
                lock.lock();

                // 2.执行业务逻辑
                doSomething();

                // 3.释放锁
                lock.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class CuratorDistributedLockRunnable implements Runnable {
        final String zkAddress = "192.168.2.20:2181";
        final String rootLockNode = "/locks";
        final int baseSleepTimeMs = 1000;
        final int maxRetries = 3;

        @Override
        public void run() {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
            CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
            client.start();
            client.checkExists();

            InterProcessMutex mutex = new InterProcessMutex(client, rootLockNode);

            try {
                // 1.加锁
                mutex.acquire(3, TimeUnit.SECONDS);

                // 2.执行业务逻辑
                doSomething();

                // 3.释放锁
                mutex.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ZkDistributedImprovedLockRunnable implements Runnable {

        final String zkAddress = "192.168.2.20:2181";
        final String lockPath = "/locks/zk_distributed_lock";

        @Override
        public void run() {
            ZkDistributedImproveLock lock = new ZkDistributedImproveLock(zkAddress, lockPath);

            // 1.加锁
            lock.lock();

            // 2.执行业务逻辑
            doSomething();

            // 3.释放锁
            lock.unlock();
        }
    }
}
