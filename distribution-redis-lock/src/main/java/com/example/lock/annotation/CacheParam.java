package com.example.lock.annotation;

import java.lang.annotation.*;

/**
 * <p>锁的参数</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 15:09
 */
@Target(value = {ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {

    /**
     * 字段名称
     *
     * @return 字段名称
     */
    String name() default "";
}
