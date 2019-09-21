package com.gusuchen.rbac.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 角色
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 16:49
 */
@Data
@Entity
@Table(name = "sec_role")
public class Role {
    /**
     * 主键
     */
    @Id
    private Long id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 角色描述
     */
    private String description;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Long updateTime;
}
