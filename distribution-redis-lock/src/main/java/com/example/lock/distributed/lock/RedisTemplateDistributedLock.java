package com.example.lock.distributed.lock;

import com.example.lock.distributed.AbstractDistributedLock;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 基于 {@link RedisTemplate}实现redis分布式锁
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 19:47
 */
@Slf4j
public class RedisTemplateDistributedLock extends AbstractDistributedLock {


    private static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis_template.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis_template.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    private RedisTemplate<String, Serializable> redisTemplate;
    private ThreadLocal<String> lockFlag = new ThreadLocal<>();

    public RedisTemplateDistributedLock(RedisTemplate<String, Serializable> redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        boolean result = setAndExpire(key, expire);
        // 如果获取锁失败，按照传入的重试次数进行重试
        while ((!result) && retryTimes > 0) {
            try {
                log.debug("lock failed, retrying..." + retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                return false;
            }
            result = setAndExpire(key, expire);
            retryTimes--;
        }
        return result;
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
        try {
            String result = redisTemplate.execute((RedisCallback<String>) redisConnection -> {
                JedisCommands commands = (JedisCommands) redisConnection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                lockFlag.set(uuid);
                return commands.set(lockKey, uuid, "NX", "PX", expireTime);
            });
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            log.error("set  and expire redis_template happened an exception", e);
        }
        return false;
    }

    /**
     * 保证锁不被误删除以及原子性释放锁;
     * Redis 从2.6.0开始通过内置的 Lua 解释器，可以使用 EVAL 命令对 Lua 脚本进行求值
     * <p>
     * if redis_template.call("get",KEYS[1]) == ARGV[1] then
     * return redis_template.call("del",KEYS[1])
     * else
     * return 0
     * end
     *
     * @param lockKey lockKey
     * @return 是否成功
     */
    @Override
    public boolean releaseLock(String lockKey) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = Lists.newArrayList(lockKey);
            List<String> args = Lists.newArrayList(lockFlag.get());

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            Long result = redisTemplate.execute((RedisCallback<Long>) redisConnection -> {
                Object nativeConnection = redisConnection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                if (nativeConnection instanceof JedisCluster) { // 集群模式
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                } else if (nativeConnection instanceof Jedis) { // 单机模式
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            });
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("release lock happened an exception", e);
        } finally {
            lockFlag.remove();
        }
        return false;
    }
}

