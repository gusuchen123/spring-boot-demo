package com.gusuchen.cache.redis;

import com.gusuchen.cache.redis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * redis缓存测试类
 * 测试使用 RedisTemplate 操作 Redis 中的数据：
 * opsForValue：对应 String（字符串
 * opsForZSet：对应 ZSet（有序集合）
 * opsForHash：对应 Hash（哈希）
 * opsForList：对应 List（列表）
 * opsForSet：对应 Set（集合）
 * opsForGeo：对应 GEO（地理位置）
 *
 * @author gusuchen
 * @since 2019-09-02
 */
@Slf4j
public class RedisTest extends SpringBootDemoCacheRedisApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 测试 redis_template 操作
     */
    @Test
    public void get() {
        // 测试线程安全，程序结束查看redis中count的值是否为1000
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        IntStream.range(0, 1000)
                .forEach(i -> executorService.execute(
                        () -> stringRedisTemplate.opsForValue().increment("count", 1)
                        )
                );
        stringRedisTemplate.opsForValue().set("k1", "v1");
        String k1 = stringRedisTemplate.opsForValue().get("k1");
        log.debug("【k1】 = {}", k1);

        String key = "gusuchen:user:1";
        redisTemplate.opsForValue().set(key, new User().setId(1L).setName("user1"));

        User user = (User) redisTemplate.opsForValue().get(key);
        log.debug("【user】 = {}", user);
    }

}
