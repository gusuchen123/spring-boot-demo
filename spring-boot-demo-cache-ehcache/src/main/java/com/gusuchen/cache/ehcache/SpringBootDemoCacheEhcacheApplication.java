package com.gusuchen.cache.ehcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * spring boot 整合 ehcache, 使用 ehcache 缓存数据 {@link SpringApplication}
 *
 * @author gusuchen
 * @since 2019-09-02
 */
@EnableCaching
@SpringBootApplication
public class SpringBootDemoCacheEhcacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoCacheEhcacheApplication.class, args);
    }

}
