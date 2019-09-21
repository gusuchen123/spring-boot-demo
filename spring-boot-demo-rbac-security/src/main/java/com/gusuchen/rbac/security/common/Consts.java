package com.gusuchen.rbac.security.common;

/**
 * <p>
 * 常量池
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 16:09
 */
public interface Consts {
    /**
     * 启用
     */
    Integer ENABLE = 1;
    /**
     * 禁用
     */
    Integer DISABLE = 0;
    /**
     * 页面
     */
    Integer PAGE = 1;
    /**
     * 按钮
     */
    Integer BUTTON = 2;
    /**
     * JWT 在 Redis 中保存的前缀
     */
    String REDIS_JWT_KEY_PREFIX = "security:jwt:";
    /**
     * 星号
     */
    String SYMBOL_STAR = "*";
    /**
     * 邮箱符号
     */
    String SYMBOL_EMAIL = "@";
    /**
     * 默认当前页码
     */
    Integer DEFAULT_CURRENt_PAGE = 1;
    /**
     * 默认每页条数
     */
    Integer DEFAULT_PAGE_SIZE = 10;
    /**
     * 匿名用户 用户名
     */
    String ANONYMOUS_NAME = "匿名用户";
}
