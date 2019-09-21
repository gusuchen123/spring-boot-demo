package com.gusuchen.rbac.security.config;

import com.gusuchen.rbac.security.common.Status;
import com.gusuchen.rbac.security.util.ResponseUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>
 * Security 结果处理配置
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-18 16:23
 */
@Configuration
public class SecurityHandlerConfig {

    /**
     * Handles an access denied failure.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> ResponseUtil.renderJson(response, Status.ACCESS_DENIED, null);
    }
}
