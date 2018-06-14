package com.chelaile.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.model.entity.SysAuth;
import com.chelaile.auth.service.SysAuthRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/14 14:59
 */
@Service
public class SysAuthRedisServiceImpl implements SysAuthRedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<SysAuth> listAuth(Integer userId, Integer orgId) {
        Object authJson = redisTemplate.opsForValue().get(String.format(AuthRedisKey.COMMON_AUTH_KEY,
                userId.toString(), orgId.toString()));
        if (authJson != null && !"".equals(authJson)) {
            return JSON.parseArray(authJson.toString(), SysAuth.class);
        }
        return null;
    }

    @Override
    public void setAuthList(Integer userId, Integer orgId, List<SysAuth> list) {
        redisTemplate.opsForValue().set(String.format(AuthRedisKey.COMMON_AUTH_KEY,
                userId.toString(), orgId.toString()), JSON.toJSONString(list));
    }

    @Override
    public void deleteByKey(String key) {
        Set<String> keys = redisTemplate.keys(key);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
}
