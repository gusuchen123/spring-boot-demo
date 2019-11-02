package com.example.lock.distributed;

/**
 * <p>DistributedLock.java 顶级接口</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 19:48
 */
public interface DistributeLock {

    long TIMEOUT_MILLIS = 30000;

    int RETRY_TIMES = Integer.MAX_VALUE;

    long SLEEP_MILLIS = 500;

    /**
     * 加锁，设置重试次数
     *
     * @param lockKey lockKey
     * @return 是否成功获得锁
     */
    boolean lock(String lockKey);

    /**
     * 加锁，设置重试次数
     *
     * @param lockKey    lockKey
     * @param retryTimes 重试次数
     * @return 是否成功获得锁
     */
    boolean lock(String lockKey, int retryTimes);

    /**
     * 加锁，设置重试次数
     *
     * @param lockKey     lockKey
     * @param retryTimes  重试次数
     * @param sleepMillis 线程休息时间
     * @return 是否成功获得锁
     */
    boolean lock(String lockKey, int retryTimes, long sleepMillis);

    /**
     * 加锁，设置重试次数
     *
     * @param lockKey lockKey
     * @param expire  过期时间
     * @return 是否成功获得锁
     */
    boolean lock(String lockKey, long expire);

    /**
     * 加锁，设置重试次数
     *
     * @param lockKey    lockKey
     * @param expire     过期时间
     * @param retryTimes 重试次数
     * @return 是否成功获得锁
     */
    boolean lock(String lockKey, long expire, int retryTimes);

    /**
     * 加锁，设置重试次数
     *
     * @param lockKey     lockKey
     * @param expire      过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 线程休息时间
     * @return 是否成功获得锁
     */
    boolean lock(String lockKey, long expire, int retryTimes, long sleepMillis);

    /**
     * 释放锁
     *
     * @param lockKey lockKey
     * @return 是否成功
     */
    boolean releaseLock(String lockKey);
}
