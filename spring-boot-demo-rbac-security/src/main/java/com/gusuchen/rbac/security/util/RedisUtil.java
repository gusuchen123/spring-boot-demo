package com.gusuchen.rbac.security.util;

import com.google.common.collect.Lists;
import com.gusuchen.rbac.security.common.PageResult;
import com.gusuchen.rbac.security.common.Status;
import com.gusuchen.rbac.security.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 15:26
 */
@Slf4j
@Component
public class RedisUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 分页获取指定格式key，使用 scan 命令代替 keys 命令，在大数据量的情况下可以提高查询效率
     *
     * @param patternKey  key格式
     * @param currentPage 当前页码
     * @param pageSize    每页条数
     * @return 分页获取指定格式key
     */
    public PageResult<String> findKeysForPage(String patternKey, int currentPage, int pageSize) {
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(patternKey)
                .build();
        RedisConnectionFactory redisConnectionFactory = Optional.ofNullable(stringRedisTemplate.getConnectionFactory())
                .orElseThrow(() -> new SecurityException(Status.ERROR));
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        Cursor<byte[]> cursor = redisConnection.scan(scanOptions);

        List<String> result = Lists.newArrayList();

        long tmpIndex = 0;
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = currentPage * pageSize;
        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            if (tmpIndex >= startIndex && tmpIndex < endIndex) {
                result.add(key);
            }
            tmpIndex++;
        }

        try {
            cursor.close();
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        } catch (IOException e) {
            log.warn("Redis连接关闭异常, ", e);
        }
        return new PageResult<>(result, tmpIndex);
    }

    /**
     * 删除 Redis 中的某个key
     *
     * @param key 键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除 Redis 中的某些key
     *
     * @param keys 键列表
     */
    public void batchDelete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }
}
