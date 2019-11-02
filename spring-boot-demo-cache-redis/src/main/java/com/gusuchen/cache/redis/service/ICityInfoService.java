package com.gusuchen.cache.redis.service;

import com.gusuchen.cache.redis.entity.CityInfo;

/**
 * <p>
 * 城市信息服务接口
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-29 :
 */
public interface ICityInfoService {

    /**
     * <h2>根据 id 获取城市信息</h2>
     *
     * @param id 记录 id
     * @return {@link CityInfo}
     */
    CityInfo getCityInfoById(Long id);

    /**
     * <h2>更新城市信息</h2>
     *
     * @param newObj 新的城市信息
     * @return {@link CityInfo}
     */
    CityInfo updateCityInfo(CityInfo newObj);

    /**
     * <h2>根据 id 删除城市信息</h2>
     *
     * @param id 记录 id
     */
    void deleteCityInfoById(Long id);
}
