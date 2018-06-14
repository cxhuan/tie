package com.chelaile.busms.service.impl;


import com.chelaile.busms.constants.BusmsRedisKey;
import com.chelaile.busms.model.entity.ChatDOExample;
import com.chelaile.busms.mysql.dao.ChatDOMapper;
import com.chelaile.busms.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 业务service
 * @author cxhuan
 */
@Service("testService")
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private ChatDOMapper chatDOMapper;


    @Override
    public String test() {
        redisTemplate.opsForHash().put(BusmsRedisKey.REDIS_TEST, "testkey", "testvalue");
        ChatDOExample ex = new ChatDOExample();
        ex.createCriteria().andContentEqualTo("hello");
        System.out.println(chatDOMapper.countByExample(ex));

        return "hello world";
    }









}
