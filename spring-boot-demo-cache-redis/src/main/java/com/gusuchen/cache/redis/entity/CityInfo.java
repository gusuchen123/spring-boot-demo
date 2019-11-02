package com.gusuchen.cache.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-29 18:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "city_info")
public class CityInfo {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 城市名称
     */
    @Basic
    @Column(name = "city", nullable = false)
    private String city;

    /**
     * 经度
     */
    @Basic
    @Column(name = "longitude", nullable = false)
    private Long longitude;

    /**
     * 维度
     */
    @Basic
    @Column(name = "latitude", nullable = false)
    private Long latitude;
}
