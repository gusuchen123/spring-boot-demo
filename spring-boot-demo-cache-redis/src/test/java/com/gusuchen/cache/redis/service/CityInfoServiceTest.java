package com.gusuchen.cache.redis.service;

import cn.hutool.json.JSONUtil;
import com.gusuchen.cache.redis.SpringBootDemoCacheRedisApplicationTests;
import com.gusuchen.cache.redis.entity.CityInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-29 18:25
 */
@Slf4j
public class CityInfoServiceTest extends SpringBootDemoCacheRedisApplicationTests {

    @Autowired
    private ICityInfoService cityInfoService;

    /**
     * <h2>测试多次获取, 可以直接从缓存(Redis)中获取数据, 而不用查询数据库</h2>
     */
    @Test
    public void testGetCityInfoById() {

        System.out.println(JSONUtil.toJsonStr(cityInfoService.getCityInfoById(1L)));
    }

    /**
     * <h2>测试更新缓存</h2>
     */
    @Test
    public void testUpdateCityInfo() {

        System.out.println(JSONUtil.toJsonStr(cityInfoService.updateCityInfo(
                new CityInfo(1L, "合肥", 11717L, 3153L)
        )));
    }

    /**
     * <h2>测试删除缓存</h2>
     */
    @Test
    public void testDeleteCityInfoById() {
        cityInfoService.deleteCityInfoById(1L);
    }
}
