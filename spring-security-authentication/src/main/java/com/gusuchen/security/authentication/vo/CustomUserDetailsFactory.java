package com.gusuchen.security.authentication.vo;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * <p>
 * {@link CustomUserDetails} 工厂类
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-20 10:09
 */
public class CustomUserDetailsFactory {

    public static CustomUserDetails create(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        return new CustomUserDetails(username, password, authorities);
    }
}
