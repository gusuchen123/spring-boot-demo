package com.example.lock.distributed;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * <p>抽象类RedisLock实现{@link Lock}，然后对一些方法提供默认实现，子类只需实现{@link Lock#lock()}和{@link Lock#unlock()}</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 18:23
 * <p>
 * 开启定时刷新
 */


@Slf4j
public abstract class RedisLock implements Lock {

    protected Jedis jedis;

    protected String lockKey;

    protected volatile boolean isOpenExpirationRenewal = true;

    public RedisLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey;
    }

    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return false;
    }

    @Override
    public void unlock() {

    }

    protected void scheduleExpirationRenewal() {
        Thread renewalThread = new Thread(new ExpirationRenewal());
        renewalThread.start();
    }

    public void sleepBySecond(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ExpirationRenewal implements Runnable {

        @Override
        public void run() {
            while (isOpenExpirationRenewal) {
                log.error("线程号: {}, 执行延迟失效时间中", Thread.currentThread().getId());
                String checkAndExpireScript = "if redis_template.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis_template.call('expire',KEYS[1],ARGV[2]) " +
                        "else " +
                        "return 0 end";
                jedis.eval(checkAndExpireScript, 1, lockKey, String.valueOf(Thread.currentThread().getId()), "30");

                sleepBySecond(10);
            }
        }
    }
}
