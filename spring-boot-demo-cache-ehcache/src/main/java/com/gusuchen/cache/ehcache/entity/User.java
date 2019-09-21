package com.gusuchen.cache.ehcache.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户实体类
 *
 * @author gusuchen
 * @since 2019-09-03
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名称
     */
    private String name;
}
