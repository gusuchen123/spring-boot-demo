package com.gusuchen.rbac.security.common;

/**
 * <p>
 * REST API 错误码接口
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 16:49
 */
public interface IStatus {
    /**
     * 状态码
     *
     * @return 状态码
     */
    Integer getCode();

    /**
     * 返回信息
     *
     * @return 返回信息
     */
    String getMessage();
}
