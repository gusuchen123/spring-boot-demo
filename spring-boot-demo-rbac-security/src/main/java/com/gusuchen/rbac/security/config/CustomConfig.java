package com.gusuchen.rbac.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 自定义配置
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 14:56
 */
@Component
@ConfigurationProperties(prefix = "custom.config")
@Data
public class CustomConfig {
    /**
     * 不需要拦截的地址
     */
    private IgnoreConfig ignores;
}
