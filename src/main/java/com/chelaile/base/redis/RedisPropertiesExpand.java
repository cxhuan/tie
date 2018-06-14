package com.chelaile.base.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis配置信息，支持产生RedisConnectionFactory和StringRedisTemplate
 *
 */
@Configuration
@ConfigurationProperties("spring.redis")
@Primary
public class RedisPropertiesExpand extends RedisProperties {

    public RedisConnectionFactory buildConnectionFactory() {
        return RedisConnectionFactoryMaker.make(this);
    }

    public StringRedisTemplate buildStringTemplate() {
        StringRedisTemplate ret = new StringRedisTemplate();
        ret.setConnectionFactory(buildConnectionFactory());
        return ret;
    }

}
