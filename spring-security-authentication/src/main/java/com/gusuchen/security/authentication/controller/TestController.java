package com.gusuchen.security.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试 {@link org.springframework.stereotype.Controller}
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-20 10:19
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping
    public String hello() {
        return "hello spring security";
    }
}
