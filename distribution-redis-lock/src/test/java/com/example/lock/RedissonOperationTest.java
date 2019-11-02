package com.example.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-22
 * 2019-10-13 19:49
 */
@Slf4j
public class RedissonOperationTest {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setTimeout(1000000)
                .setAddress("redis://192.168.2.20:6379")
                .setDatabase(3);
        RedissonClient client = Redisson.create(config);

        RList<String> nameList = client.getList("nameList");
        nameList.clear();
        nameList.add("bingo");
        nameList.add("hello");
        nameList.add("redis");
        nameList.remove(-1);

        nameList.forEach(System.out::println);

        RMap<String, String> map = client.getMap("personalInfo");
        map.put("name", "yanglbme");
        map.put("address", "Shenzhen");
        map.put("link", "https://github.com/yanglbme");

        client.shutdown();

    }
}
