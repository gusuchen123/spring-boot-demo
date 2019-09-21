package com.gusuchen.rbac.security.vo;

import lombok.Data;

/**
 * <p>
 * 在线用户 VO
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 11:30
 */
@Data
public class OnlineUser {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 生日
     */
    private Long birthday;
    /**
     * 性别，男-1，女-2
     */
    private Integer sex;

}
