package com.example.lock.distributed;

import com.example.lock.constants.LockConstants;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * <p>
 * 版本4：具有原子性的释放锁
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 19:05
 */
@Slf4j
public class LockCase4 extends RedisLock {

    public LockCase4(Jedis jedis, String lockKey) {
        super(jedis, lockKey);
    }

    @Override
    public void lock() {
        while (true) {
            String result = jedis.set(lockKey, String.valueOf(Thread.currentThread().getId()), "NX", "PX", 30);
            if (LockConstants.OK.equals(result)) {
                log.error("线程id: {} 加锁成功", Thread.currentThread().getId());
                break;
            }
        }
    }

    @Override
    public void unlock() {
        // 使用lua脚本进行原子删除操作
        String checkAndDelScript = "if redis_template.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis_template.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        jedis.eval(checkAndDelScript);
    }
}
