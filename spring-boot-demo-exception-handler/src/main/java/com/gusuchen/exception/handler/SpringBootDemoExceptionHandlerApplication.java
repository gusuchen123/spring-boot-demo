package com.gusuchen.exception.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 统一异常处理引导类 {@link SpringApplication}
 *
 * @author gusuchen
 * @since 2019-09-02
 */
@SpringBootApplication
public class SpringBootDemoExceptionHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoExceptionHandlerApplication.class, args);
    }

}
