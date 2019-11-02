package com.example.lock.distributed.lock;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;

/**
 * <p>
 * 基于 {@link redis.clients.jedis.Jedis} 实现redis分布式锁
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-22
 */
@Slf4j
public class JedisDistributedLock {

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

    private String lockKey;
    private String lockValue;

    public JedisDistributedLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey;
        this.lockValue = UUID.randomUUID().toString();
    }

    public boolean lock(long expireTime, int retryTimes, long sleepMillis) {
        boolean result = setAndExpire(lockKey, expireTime);
        while (!result && retryTimes > 0) {
            try {
                log.error("【JedisDistributedLock#lock()】线程【{}】获得锁【{}】失败，retrying....{}", Thread.currentThread().getName(), lockKey, retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = setAndExpire(lockKey, expireTime);
            retryTimes--;
        }
        return result;
    }


    public boolean unlock() {
        // 使用lua脚本进行原子删除操作
        String checkAndDelScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        try {
            jedis.eval(checkAndDelScript, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 保证设置锁（setnx）和 设置过期时间（expire）两个操作的原子性
     * jedis命令:SET lockKey lockValue NX EX expireTime, 保证设置过期时间和设置锁具有原子性
     * NX:表示只有当锁定资源不存在的时候才能 SET 成功。利用 Redis 的原子性，保证了只有第一个请求的线程才能获得锁，而之后的所有线程在锁定资源被释放之前都不能获得锁。
     * PX: expire 表示锁定的资源的自动过期时间，单位是毫秒。具体过期时间根据实际场景而定
     *
     * @param lockKey    lockKey
     * @param expireTime 过期时间
     * @return 是否成功设置lockKey和expireTime
     */
    private boolean setAndExpire(String lockKey, long expireTime) {
        String result = jedis.set(lockKey, lockValue, NOT_EXIST, SECONDS, expireTime);
        return OK.equals(result);
    }
}
