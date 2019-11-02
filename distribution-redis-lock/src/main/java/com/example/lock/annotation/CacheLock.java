package com.example.lock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>redis锁注解</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-07 18:44
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

    /**
     * <p>redis_template 锁key的前缀</p>
     *
     * @return redis_template 锁key的前缀
     */
    String prefix() default "";

    /**
     * <p>过期时间,默认为5秒</p>
     *
     * @return 轮询锁的时间
     */
    int expire() default 5;

    /**
     * <p>超时时间单位</p>
     *
     * @return 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * <p>>Key的分隔符（默认 :）</p>
     *
     * @return
     */
    String delimiter() default ":";
}
