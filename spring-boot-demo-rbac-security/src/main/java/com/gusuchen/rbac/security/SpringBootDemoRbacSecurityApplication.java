package com.gusuchen.rbac.security;

import com.gusuchen.rbac.security.config.CustomConfig;
import com.gusuchen.rbac.security.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * spring boot 集成 spring-security 完成基于rbac权限模型的权限管理，支持自定义过滤请求，动态权限认证，使用JWT安全认证，支持在线人数统计，手动踢出用户等操作；
 *
 * @author gusuchen
 */
@SpringBootApplication
@EnableConfigurationProperties(value = {JwtConfig.class, CustomConfig.class})
public class SpringBootDemoRbacSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoRbacSecurityApplication.class, args);
    }

}
