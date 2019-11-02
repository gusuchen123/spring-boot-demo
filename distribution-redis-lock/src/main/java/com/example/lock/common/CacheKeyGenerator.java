package com.example.lock.common;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * <p>key生成器</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 15:15
 */
public interface CacheKeyGenerator {
    /**
     * 获取AOP参数，生成指定缓存key
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return 缓存key
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
