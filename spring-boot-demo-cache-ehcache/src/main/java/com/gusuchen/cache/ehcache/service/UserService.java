package com.gusuchen.cache.ehcache.service;

import com.gusuchen.cache.ehcache.entity.User;

/**
 * {@link UserService}
 *
 * @author gusuchen
 * @since 2019-09-02
 */
public interface UserService {

    /**
     * 保存或者修改用户
     *
     * @param user {@link User}
     * @return {@link User}
     */
    User saveOrUpdate(User user);

    /**
     * 获取用户
     *
     * @param id {@link User#getId()}
     * @return {@link User}
     */
    User get(Long id);

    /**
     * 删除用户
     *
     * @param id {@link User#getId()}
     */
    void delete(Long id);
}
