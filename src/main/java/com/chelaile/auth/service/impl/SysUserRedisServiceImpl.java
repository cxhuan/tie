package com.chelaile.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.service.SysUserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/13 20:22
 */
@Service
public class SysUserRedisServiceImpl implements SysUserRedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public SysUser getById(Integer userId) {
        Object userJson = redisTemplate.opsForValue().get(String.format(AuthRedisKey.COMMON_USER_KEY, userId.toString()));
        if (userJson == null || "".equals(userJson)) {
            return null;
        }
        return JSON.parseObject(userJson.toString(), SysUser.class);
    }

    @Override
    public void set(SysUser user) {
        redisTemplate.opsForValue().set(String.format(AuthRedisKey.COMMON_USER_KEY, user.getId()),
                JSON.toJSONString(user), AuthConst.REDIS_COMMON_EXPIRE);
    }

    @Override
    public void delete(Integer userId) {
        redisTemplate.delete(String.format(AuthRedisKey.COMMON_USER_KEY, userId.toString()));
    }

}
