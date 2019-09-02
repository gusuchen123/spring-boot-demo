package com.gusuchen.exception.handler.common;

import com.gusuchen.exception.handler.constant.Status;
import com.gusuchen.exception.handler.exception.BaseException;
import lombok.*;

/**
 * <p>
 * 通用的 API 接口封装
 * </p>
 *
 * @author gusuchen
 * @since 2019-09-01
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
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
     * @param status 状态 {@link Status}
     * @param data   返回数据
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofStatus(Status status, T data) {
        return of(status.getCode(), status.getMessage(), data);
    }

    /**
     * 构造一个成功且带数据的API返回
     *
     * @param data 返回数据
     * @return {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ofSuccess(T data) {
        return ofStatus(Status.OK, data);
    }

    /**
     * 构造一个成功且自定义消息的API返回
     *
     * @param message 返回内容
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofMessage(String message) {
        return of(Status.OK.getCode(), message, null);
    }

    /**
     * 构造一个有状态的API返回
     *
     * @param status 状态 {@link Status}
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> ofStatus(Status status) {
        return ofStatus(status, null);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param e    异常
     * @param data 返回数据
     * @param <E>  {@link BaseException} 的子类
     * @return {@link ApiResponse}
     */
    public static <E extends BaseException> ApiResponse<Object> ofException(E e, Object data) {
        return of(e.getCode(), e.getMessage(), data);
    }

    /**
     * 构造一个异常且带数据的API返回
     *
     * @param e   异常
     * @param <E> {@link BaseException} 的子类
     * @return ApiResponse
     */
    public static <E extends BaseException> ApiResponse<Object> ofException(E e) {
        return ofException(e, null);
    }

}
