package com.gusuchen.exception.handler.exception;

import com.gusuchen.exception.handler.constant.Status;
import lombok.Getter;

/**
 * 页面异常
 * @author gusuchen
 * @since 2019-09-01
 */
@Getter
public class PageException extends BaseException {

    public PageException(Status status) {
        super(status);
    }

    public PageException(Integer code, String message) {
        super(code, message);
    }
}
