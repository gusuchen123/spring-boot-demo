package com.gusuchen.exception.handler;

import com.gusuchen.exception.handler.common.ApiResponse;
import com.gusuchen.exception.handler.constant.Status;
import com.gusuchen.exception.handler.exception.JsonException;
import com.gusuchen.exception.handler.exception.PageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 测试 {@link org.springframework.stereotype.Controller}
 * @author gusuchen
 * @since 2019-09-01
 */
@Controller
public class TestController {

    @GetMapping(value = "/json-error")
    @ResponseBody
    public ApiResponse jsonException() {
        throw new JsonException(Status.UNKNOWN_ERROR);
    }

    @GetMapping(value = "/page-error")
    public ModelAndView pageException() {
        throw new PageException(Status.UNKNOWN_ERROR);
    }
}
