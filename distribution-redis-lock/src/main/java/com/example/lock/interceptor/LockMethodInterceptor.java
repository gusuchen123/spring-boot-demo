package com.example.lock.interceptor;

import cn.hutool.core.util.StrUtil;
import com.example.lock.annotation.CacheLock;
import com.example.lock.common.CacheKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Optional;

/**
 * <p>
 * 全局切面 + 锁的注解
 * 存在的问题：
 * 1.过期时间如何保证大于业务执行时间；
 * 2.如何保证锁不被误删除；
 * 3.当持有锁的客户端宕机时，如何保证其它客户端可以正常的获得锁；
 * 4.分布式锁的重入该如何实现；
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 15:33
 */
@Aspect
@Configuration
public class LockMethodInterceptor {
    private final StringRedisTemplate lockRedisTemplate;
    private final CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    public LockMethodInterceptor(StringRedisTemplate lockRedisTemplate, CacheKeyGenerator cacheKeyGenerator) {
        this.lockRedisTemplate = lockRedisTemplate;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @Around(value = "execution(public * *(..)) && @annotation(com.example.lock.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock cacheLock = method.getAnnotation(CacheLock.class);

        if (StrUtil.isEmpty(cacheLock.prefix())) {
            throw new RuntimeException("lock key can not be null...");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(pjp);
        try {
            // 计算过期时间
            String expireTime = String.valueOf(Instant.now().toEpochMilli() + cacheLock.expire() * 1000);

            // 使用setnx尝试获得锁
            final boolean success = Optional.ofNullable(lockRedisTemplate.opsForValue().setIfAbsent(lockKey, expireTime)).orElse(false);
            if (success) {
                // 设置过期时间
                lockRedisTemplate.expire(lockKey, cacheLock.expire(), cacheLock.timeUnit());
            } else {
                //按理来说 我们应该抛出一个自定义的 CacheLockException 异常;
                throw new RuntimeException("请勿重复要求");
            }
            try {
                return pjp.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new RuntimeException("系统异常");
            }
        } finally {
            //释放锁
            lockRedisTemplate.delete(lockKey);
        }
    }
}
