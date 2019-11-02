package com.gusuchen.rbac.security.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.gusuchen.rbac.security.common.Consts;
import com.gusuchen.rbac.security.common.PageResult;
import com.gusuchen.rbac.security.model.User;
import com.gusuchen.rbac.security.payload.PageCondition;
import com.gusuchen.rbac.security.repository.UserDAO;
import com.gusuchen.rbac.security.util.RedisUtil;
import com.gusuchen.rbac.security.util.SecurityUtil;
import com.gusuchen.rbac.security.vo.OnlineUser;
import com.gusuchen.rbac.security.vo.OnlineUserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 监控 Service
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-18 15:24
 */
@Slf4j
@Service
public class MonitorService {

    private final RedisUtil redisUtil;

    private final UserDAO userDAO;

    public MonitorService(RedisUtil redisUtil, UserDAO userDAO) {
        this.redisUtil = redisUtil;
        this.userDAO = userDAO;
    }

    /**
     * 在线用户分页列表
     *
     * @param pageCondition 分页参数
     * @return 在线用户分页列表
     */
    public PageResult<OnlineUser> onlineUser(PageCondition pageCondition) {
        PageResult<String> keys = redisUtil.findKeysForPage(Consts.REDIS_JWT_KEY_PREFIX + Consts.SYMBOL_STAR,
                pageCondition.getCurrentPage(), pageCondition.getPageSize());
        List<String> rows = keys.getRows();
        Long total = keys.getTotal();

        // 根据 redis_template 中键获取用户名列表
        List<String> usernameList = rows.stream()
                .map(s -> StrUtil.subAfter(s, Consts.REDIS_JWT_KEY_PREFIX, true))
                .collect(Collectors.toList());
        // 根据用户名查询用户信息
        List<User> userList = userDAO.findByUsernameIn(usernameList);

        // 封装在线用户信息
        List<OnlineUser> onlineUserList = Lists.newArrayList();
        userList.forEach(user -> onlineUserList.add(OnlineUserFactory.create(user)));

        return new PageResult<>(onlineUserList, total);
    }

    /**
     * 踢出在线用户
     *
     * @param names 用户名列表
     */
    public void kickOut(List<String> names) {
        // 清除 Redis 中的 JWT 信息
        List<String> redisKeys = names.parallelStream()
                .map(s -> Consts.REDIS_JWT_KEY_PREFIX + s)
                .collect(Collectors.toList());
        redisUtil.batchDelete(redisKeys);

        // 获取当前用户名
        String currentUsername = SecurityUtil.getCurrentUsername();
        names.parallelStream()
                .forEach(name -> {
                    // TODO: 通知被踢出的用户已被当前登录用户踢出，
                    //  后期考虑使用 websocket 实现，具体伪代码实现如下。
                    //  String message = "您已被用户【" + currentUsername + "】手动下线！";
                    log.debug("用户【{}】被用户【{}】手动下线！", name, currentUsername);
                });
    }
}
