package com.gusuchen.rbac.security.repository;

import com.gusuchen.rbac.security.model.RolePermission;
import com.gusuchen.rbac.security.model.unionkey.RolePermissionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <p>
 * 角色-权限 DAO
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 14:30
 */
public interface RolePermissionDAO extends JpaRepository<RolePermission, RolePermissionKey>, JpaSpecificationExecutor<RolePermission> {
}
