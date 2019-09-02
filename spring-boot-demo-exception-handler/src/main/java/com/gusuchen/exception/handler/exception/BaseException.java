package com.gusuchen.exception.handler.exception;

import com.gusuchen.exception.handler.constant.Status;
import lombok.*;

/**
 * 异常基类
 * @author gusuchen
 * @since 2019-09-01
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    private Integer code;
    private String message;

    public BaseException(Status status) {
        super(status.getMessage());
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
