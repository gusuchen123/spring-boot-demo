package com.gusuchen.exception.handler.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态吗封装
 *
 * @author gu_su
 * @since 2019-09-01
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum Status {
    /**
     * 操作成功
     */
    OK(200, "操作成功"),
    /**
     * 未知异常
     */
    UNKNOWN_ERROR(500, "服务出错啦"),
    ;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 内容
     */
    private String message;
}
