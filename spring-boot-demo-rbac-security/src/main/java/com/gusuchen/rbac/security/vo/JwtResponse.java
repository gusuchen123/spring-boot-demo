package com.gusuchen.rbac.security.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * JWT响应返回
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 11:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    /**
     * token 字段
     */
    private String token;

    /**
     * token 类型
     */
    private String tokenType = "Bearer";

    public JwtResponse(String token) {
        this.token = token;
    }
}
