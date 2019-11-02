package com.example.lock.common;

import cn.hutool.core.util.StrUtil;
import com.example.lock.annotation.CacheLock;
import com.example.lock.annotation.CacheParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * <p>通过接口注入的方式去写不同的生成规则</p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 15:17
 */
public class LockKeyGenerator implements CacheKeyGenerator {

    /**
     * 获取AOP参数，生成指定缓存key
     *
     * @param pjp {@link ProceedingJoinPoint}
     * @return 缓存key
     */
    @Override
    public String getLockKey(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lockAnnotation = method.getAnnotation(CacheLock.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();

        //默认解析方法里面带 CacheParam 注解的属性,如果没有尝试着解析实体对象中的
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = parameters.length; i < n; i++) {
            final CacheParam annotation = parameters[i].getAnnotation(CacheParam.class);
            if (annotation == null) {
                continue;
            }
            builder.append(lockAnnotation.delimiter()).append(args[i]);
        }

        // 解析实体类里面带 CacheParam 注解的属性
        if (StrUtil.isEmpty(builder.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0, n = parameterAnnotations.length; i < n; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final CacheParam annotation = field.getAnnotation(CacheParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(lockAnnotation.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return lockAnnotation.prefix() + builder.toString();
    }
}
