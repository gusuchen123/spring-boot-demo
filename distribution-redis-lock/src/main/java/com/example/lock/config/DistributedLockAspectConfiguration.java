package com.example.lock.config;

import cn.hutool.core.util.StrUtil;
import com.example.lock.annotation.RedisLock;
import com.example.lock.distributed.DistributeLock;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * <p>全局分布式锁切面</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-14 16:57
 */
@Slf4j
@Aspect
@Configuration
@ConditionalOnClass(DistributeLock.class)
@AutoConfigureAfter(value = DistributedLockAutoConfiguration.class)
public class DistributedLockAspectConfiguration {

    private final DistributeLock distributeLock;

    @Autowired
    public DistributedLockAspectConfiguration(DistributeLock distributeLock) {
        this.distributeLock = distributeLock;
    }

    @Pointcut(value = "execution(public * *(..)) && @annotation(com.example.lock.annotation.RedisLock)")
    private void lockPoint() {

    }

    @Around(value = "lockPoint()")
    public Object around(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);

        String lockKey = redisLock.lockKey();
        Preconditions.checkArgument(StrUtil.isNotBlank(lockKey), "【非法的参数】lockKey不能为空");

        int retryTimes = redisLock.action().equals(RedisLock.LockFailAction.GIVEUP) ? 0 : redisLock.retryTimes();

        boolean lock = distributeLock.lock(lockKey, redisLock.keepMills(), retryTimes, redisLock.sleepMills());
        if (!lock) {
            log.error("get lock failed, lockKey: {}", lockKey);
            // todo: 不应该返回 null, 后面做处理；
            return null;
        }

        log.error("get lock success, 线程ID:{}, lockKey:{}", Thread.currentThread().getId(), lockKey);
        // 获取锁，执行方法，释放锁
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            log.error("execute locked method happened an exception", throwable);
            throw new RuntimeException("服务器后端异常");
        } finally {
            boolean releaseResult = distributeLock.releaseLock(lockKey);
            log.error("release lock, lockKey:{}, releaseResult:{}", lockKey, (releaseResult ? " success" : " failed"));
        }
    }
}
