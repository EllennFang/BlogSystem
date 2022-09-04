package com.ellenfang;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisSpringBootApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        // redisTemplate.opsForValue() 操作字符串，类似String
        // redisTemplate.opsForList()  操作List，类似List
        // ...
        // 除了进阶的操作，我们常用的方法都可以直接通过 redisTemplate 操作，比如：事务，和基本的CRUD

        // 获取 redis 的连接对象
        // RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        // connection.flushAll();
        // connection.flushDb();

        redisTemplate.opsForValue().set("mykey", "DUODUO");
        System.out.println(redisTemplate.opsForValue().get("mykey"));
    }
}
