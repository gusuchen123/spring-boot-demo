package com.gusuchen.rbac.security.service.impl;

import com.gusuchen.rbac.security.model.Permission;
import com.gusuchen.rbac.security.model.Role;
import com.gusuchen.rbac.security.model.User;
import com.gusuchen.rbac.security.repository.PermissionDAO;
import com.gusuchen.rbac.security.repository.RoleDAO;
import com.gusuchen.rbac.security.repository.UserDAO;
import com.gusuchen.rbac.security.vo.UserPrincipalFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 自定义UserDetails查询
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-18 15:24
 */
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserDAO userDAO;

    private final RoleDAO roleDAO;

    private final PermissionDAO permissionDAO;

    public CustomUserDetailsServiceImpl(UserDAO userDAO, RoleDAO roleDAO, PermissionDAO permissionDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.permissionDAO = permissionDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmailOrPhone) throws UsernameNotFoundException {
        User user = userDAO.findByUsernameOrEmailOrPhone(usernameOrEmailOrPhone, usernameOrEmailOrPhone, usernameOrEmailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户信息 : " + usernameOrEmailOrPhone));

        List<Role> roles = roleDAO.selectByUserId(user.getId());
        List<Long> roleIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        List<Permission> permissions = permissionDAO.selectByRoleIdList(roleIds);
        return UserPrincipalFactory.create(user, roles, permissions);
    }
}
