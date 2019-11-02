package com.example.lock.config;

import com.example.lock.distributed.DistributeLock;
import com.example.lock.distributed.lock.RedisTemplateDistributedLock;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

/**
 * <p>装配分布式锁的bean</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-14 13:45
 */
@Configuration
@AutoConfigureAfter(RedisTemplateConfiguration.class)
public class DistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnBean(value = RedisTemplate.class)
    public DistributeLock redisDistributedLock(RedisTemplate<String, Serializable> redisTemplate) {
        return new RedisTemplateDistributedLock(redisTemplate);
    }
}
