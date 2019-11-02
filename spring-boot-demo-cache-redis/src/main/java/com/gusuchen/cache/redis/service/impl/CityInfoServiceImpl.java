package com.gusuchen.cache.redis.service.impl;

import com.gusuchen.cache.redis.entity.CityInfo;
import com.gusuchen.cache.redis.repository.CityInfoRepository;
import com.gusuchen.cache.redis.service.ICityInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 实现 {@link ICityInfoService}
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-29 18:17
 */
@Slf4j
@Service
public class CityInfoServiceImpl implements ICityInfoService {

    private final CityInfoRepository cityInfoRepository;

    @Autowired
    public CityInfoServiceImpl(CityInfoRepository cityInfoRepository) {
        this.cityInfoRepository = cityInfoRepository;
    }


    @Override
    @Cacheable(cacheNames = "city_info", key = "#id")
    public CityInfo getCityInfoById(Long id) {
        log.error("【ICityInfoService#getCityInfoById(Long)】id:{}", id);
        return cityInfoRepository.findById(id).get();
    }

    @Override
    public CityInfo updateCityInfo(CityInfo newObj) {
        log.error("【ICityInfoService#updateCityInfo(CityInfo)】CityInfo:{}", newObj.toString());
        return cityInfoRepository.save(newObj);
    }

    @Override
    public void deleteCityInfoById(Long id) {
        log.error("【ICityInfoService#deleteCityInfoById(Long)】id:{}", id);
        cityInfoRepository.deleteById(id);
    }
}
