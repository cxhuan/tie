package com.chelaile.base.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


@Configuration
@ConfigurationProperties("cll.redis.primary")
public class RedisProperties extends RedisPropertiesExpand {

    @Bean
    public StringRedisTemplate stringTpl() {
        return buildStringTemplate();
    }

}
