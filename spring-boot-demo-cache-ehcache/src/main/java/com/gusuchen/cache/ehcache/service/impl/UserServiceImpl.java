package com.gusuchen.cache.ehcache.service.impl;

import com.google.common.collect.Maps;
import com.gusuchen.cache.ehcache.entity.User;
import com.gusuchen.cache.ehcache.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * {@link UserService}
 *
 * @author gusuchen
 * @since 2019-09-03
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    /**
     * 模拟数据库
     */
    private static final Map<Long, User> DATABASES = Maps.newConcurrentMap();

    static {
        DATABASES.put(1L, new User().setId(1L).setName("user1"));
        DATABASES.put(2L, new User().setId(2L).setName("user2"));
        DATABASES.put(3L, new User().setId(3L).setName("user3"));
    }

    @Override
    @CachePut(value = "user", key = "#user.id")
    public User saveOrUpdate(User user) {
        DATABASES.put(user.getId(), user);
        log.info("【保存用户】,user:{}", user);
        return user;
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public User get(Long id) {
        log.info("【获取用户】:{}", id);
        return DATABASES.get(id);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public void delete(Long id) {
        DATABASES.remove(id);
        log.info("【删除用户】:{}", id);
    }
}
