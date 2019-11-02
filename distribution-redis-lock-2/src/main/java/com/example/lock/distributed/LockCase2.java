package com.example.lock.distributed;

import com.example.lock.constants.LockConstants;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * <p>
 * 版本2：设置过期时间
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 18:48
 */
@Slf4j
public class LockCase2 extends RedisLock {

    public LockCase2(Jedis jedis, String lockKey) {
        super(jedis, lockKey);
    }

    @Override
    public void lock() {
        while (true) {
            String result = jedis.set(lockKey, "value", "NX", "PX", 30);
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
