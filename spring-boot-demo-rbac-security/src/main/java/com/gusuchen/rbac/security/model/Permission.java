package com.gusuchen.rbac.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 权限
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 16:44
 */
@Data
@Entity
@Table(name = "sec_permission")
public class Permission {
    /**
     * 主键
     */
    @Id
    private Long id;
    /**
     * 权限名
     */
    private String name;
    /**
     * 类型为页面时候，代表前端路径，类型为按钮的时候，代表后端接口路径
     */
    private String url;
    /**
     * 权限类型，页面-1，按钮-2
     */
    private Integer type;
    /**
     * 权限表达式
     */
    private String permission;
    /**
     * 后端接口访问方式
     */
    private String method;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 父级id
     */
    @Column(name = "parent_id")
    private Long parentId;
}
