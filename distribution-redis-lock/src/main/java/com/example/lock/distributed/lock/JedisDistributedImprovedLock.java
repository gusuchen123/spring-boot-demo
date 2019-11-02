package com.example.lock.distributed.lock;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-22 20:55
 */
@Slf4j
public class JedisDistributedImprovedLock {
    private static final String OK = "OK";

    /**
     * NX -- Only set the key if it does not already exist.
     */
    private static final String NOT_EXIST = "NX";
    /**
     * XX -- Only set the key if it already exist.
     */
    private static final String EXIST = "XX";

    /**
     * 过期时间，单位 秒
     */
    private static final String SECONDS = "EX";
    /**
     * 过期时间，单位 毫秒
     */
    private static final String MILLISECONDS = "PX";

    /**
     * jedis客户端
     */
    private Jedis jedis;
    /**
     * 是否开启守护线程，刷新lockKey的过期时间
     */
    private volatile boolean isOpenExpirationRenewal = true;

    private String lockKey;
    private String lockValue;

    public JedisDistributedImprovedLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey;
        this.lockValue = UUID.randomUUID().toString();
    }

    public void lock(long expireTime) {
        while (true) {
            String result = jedis.set(lockKey, lockValue, NOT_EXIST, SECONDS, expireTime);
            if (OK.equals(result)) {

                // 开启定时刷新时间
                isOpenExpirationRenewal = true;
                scheduleExpirationRenewal();
                break;
            }
        }

        // 休眠是10秒
        sleepBySecond(10);
    }

    private void sleepBySecond(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void unlock() {
        // 使用lua脚本进行原子删除操作
        String checkAndDelScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        jedis.eval(checkAndDelScript, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
        isOpenExpirationRenewal = false;
    }

    /**
     * 开启定时刷新
     */
    private void scheduleExpirationRenewal() {
        Thread renewalThread = new Thread(new ExpirationRenewal());
        renewalThread.start();
    }

    /**
     * 刷新lockKey的过期时间
     */
    private class ExpirationRenewal implements Runnable {

        @Override
        public void run() {
            while (isOpenExpirationRenewal) {
                log.error("【JedisDistributedLock.ExpirationRenewal】线程【{}】执行延迟失效时间中....", Thread.currentThread().getName());

                String checkAndExpireScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('expire', KEYS[1], ARGV[2]) " +
                        "else " +
                        "return 0 end";
                jedis.eval(checkAndExpireScript, 1, lockKey, lockValue);
            }
        }
    }
}
