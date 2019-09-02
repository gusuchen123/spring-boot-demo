package com.gusuchen.exception.handler.exception;

import com.gusuchen.exception.handler.constant.Status;
import lombok.Getter;

/**
 * json异常
 *
 * @author gusuchen
 * @since 2019-09-01
 */
@Getter
public class JsonException extends BaseException {

    public JsonException(Status status) {
        super(status);
    }

    public JsonException(Integer code, String message) {
        super(code, message);
    }
}
