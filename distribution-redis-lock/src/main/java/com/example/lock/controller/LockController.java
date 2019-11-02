package com.example.lock.controller;

import com.example.lock.annotation.CacheLock;
import com.example.lock.annotation.CacheParam;
import com.example.lock.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 15:53
 */
@Slf4j
@RestController
@RequestMapping(value = "/redis/lock")
public class LockController {

    @GetMapping
    @CacheLock(prefix = "test", expire = 60, delimiter = "#")
    public String query(@CacheParam(name = "token") @RequestParam(name = "token") String token) {
        return "success - " + token;
    }

    @GetMapping(value = "/distributed")
    @RedisLock(lockKey = "redis_distributed_lock")
    public String get(@RequestParam(name = "token") String token) {
        return "success - " + token;
    }
}
