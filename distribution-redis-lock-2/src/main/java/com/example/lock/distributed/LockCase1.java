package com.example.lock.distributed;

import com.example.lock.constants.LockConstants;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * <p>{@link RedisLock#lock()}方法中，redis客户端执行如下命令：SET lockKey value NX</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 18:40
 */
@Slf4j
public class LockCase1 extends RedisLock {

    public LockCase1(Jedis jedis, String lockKey) {
        super(jedis, lockKey);
    }

    @Override
    public void lock() {
        while (true) {
            String result = jedis.set(lockKey, "value", "NX");
            if (LockConstants.OK.equals(result)) {
                log.error("线程id: {} 加锁成功", Thread.currentThread().getId());
                break;
            }
        }
    }

    @Override
    public void unlock() {
        jedis.del(lockKey);
    }
}
