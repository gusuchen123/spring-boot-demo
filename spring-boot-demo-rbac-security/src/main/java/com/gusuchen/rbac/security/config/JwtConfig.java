package com.gusuchen.rbac.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * JWT 配置
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 15:44
 */
@Data
@ConfigurationProperties(prefix = "jwt.config")
public class JwtConfig {
    /**
     * jwt 加密 key, 默认值是：xkcoding
     */
    private String key = "xkcoding";
    /**
     * jwt 过期时间, 单位是ms, 默认值: 600000L {@code 10分钟}
     */
    private Long ttlMillis = 600000L;
    /**
     * 开启 记住我 之后 jwt 过期时间，单位是ms, 默认值：7*24*60*60*60 {@code 7天}
     */
    private Long remember = 604800000L;
}
