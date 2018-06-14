package com.chelaile.auth.service;

import com.chelaile.auth.model.entity.SysUser;

/**
 * Description:
 *
 * @author: cxhuan
 * @create: 2018/6/13 20:20
 */
public interface SysUserRedisService {
    SysUser getById(Integer userId);

    void set(SysUser user);

    void delete(Integer userId);

}
