package com.gusuchen.rbac.security.model;

import com.gusuchen.rbac.security.model.unionkey.UserRoleKey;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 用户-角色关联
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 17:00
 */
@Data
@Entity
@Table(name = "sec_user_role")
public class UserRole {
    /**
     * 主键
     */
    @EmbeddedId
    private UserRoleKey id;
}
