package com.example.lock.distributed;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.time.LocalTime;

import static com.example.lock.constants.LockConstants.OK;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 19:11
 */
@Slf4j
public class LockCase5 extends RedisLock {

    public LockCase5(Jedis jedis, String lockKey) {
        super(jedis, lockKey);
    }

    @Override
    public void lock() {
        while (true) {
            String result = jedis.set(lockKey, String.valueOf(Thread.currentThread().getId()), "NX", "PX", 30);
            if (OK.equals(result)) {
                System.out.println("线程id:" + Thread.currentThread().getId() + "加锁成功!时间:" + LocalTime.now());

                //开启定时刷新过期时间
                isOpenExpirationRenewal = true;
                scheduleExpirationRenewal();
                break;
            }
            System.out.println("线程id:" + Thread.currentThread().getId() + "获取锁失败，休眠10秒!时间:" + LocalTime.now());
            //休眠10秒
            sleepBySecond(10);
        }
    }

    @Override
    public void unlock() {
        System.out.println("线程id:" + Thread.currentThread().getId() + "解锁!时间:" + LocalTime.now());

        String checkAndDelScript = "if redis_template.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis_template.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        jedis.eval(checkAndDelScript, 1, lockKey, String.valueOf(Thread.currentThread().getId()));
        isOpenExpirationRenewal = false;
    }
}
