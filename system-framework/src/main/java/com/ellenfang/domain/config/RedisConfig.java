package com.ellenfang.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 编写我们呢自己的 redisTemplate
     * @return 自定义的 redisTemplate
     */
    @Bean
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // 我们为了自己开发的方便，一般直接使用 <String, Object>
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 连接工厂
        template.setConnectionFactory(connectionFactory);

        // Json 序列化
        FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Object.class);

        // String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 采用 String 的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash 的 key 也采用 String 的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value 采用 jackson 的序列化方式
        template.setValueSerializer(serializer);
        // hash 的 key 也采用 jackson 的序列化方式
        template.setHashValueSerializer(serializer);

        // 配置完之后将所有的 properties 设置进去
        template.afterPropertiesSet();

        return template;
    }
}