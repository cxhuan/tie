package com.chelaile.auth.service;

import com.chelaile.auth.dao.SysUserAuthMapper;
import com.chelaile.auth.model.entity.SysUserAuth;
import com.chelaile.auth.model.entity.SysUserAuthExample;
import com.chelaile.base.BaseService;

public interface SysUserAuthService extends BaseService<SysUserAuthMapper, SysUserAuthExample, SysUserAuth, Integer> {
}