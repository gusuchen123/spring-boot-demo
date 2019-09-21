package com.gusuchen.rbac.security.controller;

import cn.hutool.core.collection.CollUtil;
import com.gusuchen.rbac.security.common.ApiResponse;
import com.gusuchen.rbac.security.common.PageResult;
import com.gusuchen.rbac.security.common.Status;
import com.gusuchen.rbac.security.exception.SecurityException;
import com.gusuchen.rbac.security.payload.PageCondition;
import com.gusuchen.rbac.security.service.MonitorService;
import com.gusuchen.rbac.security.util.PageUtil;
import com.gusuchen.rbac.security.util.SecurityUtil;
import com.gusuchen.rbac.security.vo.OnlineUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-18 15:15
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/monitor")
public class MonitorController {
    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    /**
     * 在线用户列表
     *
     * @param pageCondition 分页参数
     */
    @GetMapping("/online/user")
    public ApiResponse<PageResult> onlineUser(PageCondition pageCondition) {
        PageUtil.checkPageCondition(pageCondition, PageCondition.class);
        PageResult<OnlineUser> pageResult = monitorService.onlineUser(pageCondition);
        return ApiResponse.ofSuccess(pageResult);
    }

    /**
     * 批量踢出在线用户
     *
     * @param names 用户名列表
     */
    @DeleteMapping("/online/user/kick-out")
    public ApiResponse kickOutOnlineUser(@RequestBody List<String> names) {
        if (CollUtil.isEmpty(names)) {
            throw new SecurityException(Status.PARAM_NOT_NULL);
        }
        if (names.contains(SecurityUtil.getCurrentUsername())) {
            throw new SecurityException(Status.KICKOUT_SELF);
        }
        monitorService.kickOut(names);
        return ApiResponse.ofSuccess();
    }
}

