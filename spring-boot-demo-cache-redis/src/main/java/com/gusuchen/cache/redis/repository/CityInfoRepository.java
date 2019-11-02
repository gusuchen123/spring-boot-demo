package com.gusuchen.cache.redis.repository;

import com.gusuchen.cache.redis.entity.CityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-29 18:14
 */
@Repository
public interface CityInfoRepository extends JpaRepository<CityInfo, Long> {
}
