package com.chelaile.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.model.entity.SysOrg;
import com.chelaile.auth.service.SysOrgRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/13 20:39
 */
@Service
public class SysOrgRedisServiceImpl implements SysOrgRedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public List<SysOrg> getByUserId(Integer userId) {
        Object json = redisTemplate.opsForValue().get(String.format(AuthRedisKey.COMMON_ORG_KEY, userId.toString()));
        if (json == null || "".equals(json)) {
            return null;
        }

        return JSON.parseArray(json.toString(), SysOrg.class);
    }

    @Override
    public void add(List<SysOrg> orgList, Integer userId) {
        redisTemplate.opsForValue().set(String.format(AuthRedisKey.COMMON_ORG_KEY, userId.toString()), JSON.toJSONString(orgList));
    }

    @Override
    public void deleteByUser(Integer userId) {
        redisTemplate.delete(String.format(AuthRedisKey.COMMON_ORG_KEY, userId.toString()));
    }
}
