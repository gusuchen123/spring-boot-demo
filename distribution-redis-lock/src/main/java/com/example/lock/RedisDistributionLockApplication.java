package com.example.lock;

import com.example.lock.common.CacheKeyGenerator;
import com.example.lock.common.LockKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * 基于redis实现分布式锁
 * 多个进程对同一条数据进行修改时，并且要求这个修改是原子性的；
 * 有两个限定：1.多个进程之间的竞争，意味着JDK自带的锁失效了
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-07 18:44
 */
@SpringBootApplication
public class RedisDistributionLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDistributionLockApplication.class, args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }

}
