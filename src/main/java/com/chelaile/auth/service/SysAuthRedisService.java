package com.chelaile.auth.service;

import com.chelaile.auth.model.entity.SysAuth;

import java.util.List;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/14 14:49
 */
public interface SysAuthRedisService {
    List<SysAuth> listAuth(Integer userId, Integer orgId);

    void setAuthList(Integer userId, Integer orgId, List<SysAuth> list);

    void deleteByKey(String key);
}
