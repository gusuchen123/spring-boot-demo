package com.gusuchen.redis.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *     基于redis的消息队列的实现
 * </p>
 *
 * @author gusuchen
 * @date created in 2019-10-31
 */
@SpringBootApplication
public class RedisMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisMqApplication.class, args);
    }
}
