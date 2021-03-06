package com.gusuchen.cache.ehcache.service;

import com.gusuchen.cache.ehcache.SpringBootDemoCacheEhcacheApplicationTests;
import com.gusuchen.cache.ehcache.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ehcache 缓存测试
 *
 * @author gusuchen
 * @since 2019-09-02
 */
@Slf4j
public class UserServiceTest extends SpringBootDemoCacheEhcacheApplicationTests {

    @Autowired
    private UserService userService;

    /**
     * 获取两次，查看日志验证缓存
     */
    @Test
    public void getTwice() {
        // 模拟查询用户id为1的用户
        User user1 = userService.get(1L);
        log.debug("【user1】 = {}", user1);

        // 再次查询
        User user2 = userService.get(1L);
        log.debug("【user2】 = {}", user2);
        // 查看日志，只打印一次日志，证明缓存生效
    }

    /**
     * 先存,再查询，查看日志验证缓存
     */
    @Test
    public void getAfterSave() {
        userService.saveOrUpdate(new User().setId(4L).setName("user4"));

        // 查看日志，只打印保存用户的日志，查询是未触发查询日志，因此缓存生效
        User user = userService.get(4L);
        log.debug("【user】 = {}", user);
    }

    /**
     * 测试删除，查看ehcache是否存在缓存数据
     */
    @Test
    public void deleteUser() {
        // 查询一次，使ehcache中存在缓存数据
        userService.get(1L);
        // 删除，查看ehcache是否存在缓存数据
        userService.delete(1L);

    }
}
