package com.gusuchen.rbac.security.util;

import cn.hutool.json.JSONUtil;
import com.gusuchen.rbac.security.SpringBootDemoRbacSecurityApplicationTests;
import com.gusuchen.rbac.security.common.Consts;
import com.gusuchen.rbac.security.common.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 15:17
 */
@Slf4j
public class RedisUtilTest extends SpringBootDemoRbacSecurityApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void findKeysForPage() {
        PageResult pageResult = redisUtil.findKeysForPage(Consts.REDIS_JWT_KEY_PREFIX + Consts.SYMBOL_STAR, 2, 1);
        log.info("【pageResult】= {}", JSONUtil.toJsonStr(pageResult));
    }
}
