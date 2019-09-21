package com.gusuchen.rbac.security.vo;

import cn.hutool.core.util.StrUtil;
import com.gusuchen.rbac.security.model.Permission;
import com.gusuchen.rbac.security.model.Role;
import com.gusuchen.rbac.security.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-18 20:12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipalFactory {

    public static UserPrincipal create(User user, List<Role> roles, List<Permission> permissions) {
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<GrantedAuthority> authorities = permissions.stream()
                .filter(permission -> StrUtil.isNotBlank(permission.getPermission()))
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        return new UserPrincipal()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setNickname(user.getNickname())
                .setPhone(user.getPhone())
                .setEmail(user.getEmail())
                .setCreateTime(user.getCreateTime())
                .setUpdateTime(user.getUpdateTime())
                .setRoles(roleNames)
                .setAuthorities(authorities)
                ;
    }
}
