package com.gusuchen.rbac.security.repository;

import com.gusuchen.rbac.security.model.UserRole;
import com.gusuchen.rbac.security.model.unionkey.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户-角色 DAO
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 14:31
 */
@Repository
public interface UserRoleDAO extends JpaRepository<UserRole, UserRoleKey>, JpaSpecificationExecutor<UserRole> {
}
