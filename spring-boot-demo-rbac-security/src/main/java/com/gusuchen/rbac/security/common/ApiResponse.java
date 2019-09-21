package com.gusuchen.rbac.security.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 通用的API接口封装
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 20:49
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回内容
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 构造一个自定义的API返回
     *
     * @param code    状态码
     * @param message 返回内容
     * @param data    返回数据
     * @param <T>     数据类型
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> of(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    /**
     * 构造一个有状态且带数据的API返回
     *
     * @param status 状态 {@link IStatus}
     * @param <T>    数据类型
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofStatus(IStatus status, T data) {
        return of(status.getCode(), status.getMessage(), data);
    }

    /**
     * 构造一个有状态的API返回
     *
     * @param status 状态 {@link Status}
     * @param <T>    数据类型
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofStatus(Status status) {
        return ofStatus(status, null);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param <T> 数据类型
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofSuccess() {
        return ofStatus(Status.SUCCESS, null);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofSuccess(T data) {
        return ofStatus(Status.SUCCESS, data);
    }

    /**
     * 构造一个成功且自定义消息的API返回
     *
     * @param message 返回内容
     * @param <T>     数据类型
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofMessage(String message) {
        return of(Status.SUCCESS.getCode(), message, null);
    }

    /**
     * 构造一个异常的API返回
     *
     * @param e   异常
     * @param <E> {@link BaseException} 的子类
     * @return {@link ApiResponse}
     */
    public static <E extends BaseException> ApiResponse<Object> ofException(E e) {
        return of(e.getCode(), e.getMessage(), e.getData());
    }


}
