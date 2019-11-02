package com.example.lock.distributed.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * <p>
 * 使用 curator 操作
 * {@link InterProcessMutex}          分布式可重入排它锁
 * {@link InterProcessSemaphoreMutex} 分布式排它锁
 * {@link InterProcessReadWriteLock}  分布式读写锁
 * {@link InterProcessMultiLock}      将多个锁作为单个实体管理的容器
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-19 17:51
 */
@Slf4j
public class CuratorOperation {

    public static void main(String[] args) throws Exception {
        // 1.创建Zookeeper客户端
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.2.20:2181", 300000, 300000, retryPolicy);
        client.start();

        // 2.创建分布式锁，锁的根节点路径为：/curator/lock
        InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock");

        // 3.获得锁
        mutex.acquire();

        // 4.执行业务流程
        log.error("【CuratorOperation】获得锁，执行业务流程");

        // 5.释放锁
        mutex.release();

        // 6.关闭客户端
        client.close();
    }
}
