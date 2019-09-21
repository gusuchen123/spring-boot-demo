package com.gusuchen.rbac.security.model.unionkey;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * <p>
 * 角色-权限联合主键
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 17:00
 */
@Data
@Embeddable
public class RolePermissionKey implements Serializable {
    /**
     * 角色id
     */
    @Column(name = "role_id")
    private Long roleId;
    /**
     * 权限id
     */
    @Column(name = "permission_id")
    private Long permissionId;
}
