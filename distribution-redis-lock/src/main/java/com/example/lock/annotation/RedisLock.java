package com.example.lock.annotation;

import java.lang.annotation.*;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-14 13:03
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisLock {
    /**
     * 锁的资源，redis的key
     *
     * @return lockKey
     */
    String lockKey() default "";

    /**
     * 持有锁的时间，单位是毫秒
     *
     * @return 持有锁的时间
     */
    long keepMills() default 300000;

    /**
     * 当获取锁失败时候的动作
     *
     * @return {@link LockFailAction}
     */
    LockFailAction action() default LockFailAction.CONTINUE;

    /**
     * 重试时间间隔，单位毫秒，设置了 {@link LockFailAction#GIVEUP}则忽略此项
     *
     * @return 重试时间间隔，单位毫秒
     */
    long sleepMills() default 200;

    /**
     * 重试次数
     *
     * @return 重试次数
     */
    int retryTimes() default 5;

    enum LockFailAction {
        /**
         * 放弃
         */
        GIVEUP,
        /**
         * 继续
         */
        CONTINUE,
        ;
    }
}
