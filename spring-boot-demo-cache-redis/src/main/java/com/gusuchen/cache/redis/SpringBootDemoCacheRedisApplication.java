package com.gusuchen.cache.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * spring-boot 整合 redis_template，操作redis中的数据，并使用redis缓存数据
 *
 * @author gusuchen
 * @since 2019-09-02
 */
@SpringBootApplication
public class SpringBootDemoCacheRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoCacheRedisApplication.class, args);
    }

}
