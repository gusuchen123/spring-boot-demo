package com.gusuchen.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试 {@link org.springframework.web.servlet.mvc.Controller}
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-19 14:17
 */
@RestController
@RequestMapping(value = "/hello")
public class TestController {

    @GetMapping
    public String hello() {
        return "hello spring security";
    }
}
