package com.example.lock.distributed;

import com.example.lock.constants.LockConstants;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * <p>
 * 版本3：设置value
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 18:55
 */
@Slf4j
public class LockCase3 extends RedisLock {

    public LockCase3(Jedis jedis, String lockKey) {
        super(jedis, lockKey);
    }

    @Override
    public void lock() {
        while (true) {
            /*
             * 设置value为当前线程特有的值
             */
            String result = jedis.set(lockKey, String.valueOf(Thread.currentThread().getId()), "NX", "PX", 30);
            if (LockConstants.OK.equals(result)) {
                log.error("线程id: {} 加锁成功", Thread.currentThread().getId());
                break;
            }
        }
    }

    @Override
    public void unlock() {
        /*
         * 此处不具备原子性,可以分为三个步骤
         * 1.获取锁对应的value值
         * 2.检查是否与requestId相等
         * 3.如果相等则删除锁（解锁）
         */
        String lockValue = jedis.get(lockKey);
        if (String.valueOf(Thread.currentThread().getId()).equals(lockValue)) {
            jedis.del(lockKey);
        }
    }
}
