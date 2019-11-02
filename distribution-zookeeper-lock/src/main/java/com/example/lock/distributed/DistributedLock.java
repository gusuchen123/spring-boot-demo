package com.example.lock.distributed;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-18 16:40
 */
public interface DistributedLock {
    /**
     * 加锁
     */
    void lock();

    /**
     * 释放锁
     */
    void unlock() throws InterruptedException;

    /**
     * 尝试获得锁
     *
     * @return 是否成功获得锁
     */
    boolean tryLock();
}
