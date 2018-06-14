package com.chelaile.auth.service;

import com.chelaile.auth.dao.SysAuthMapper;
import com.chelaile.auth.model.entity.SysAuth;
import com.chelaile.auth.model.entity.SysAuthExample;
import com.chelaile.base.BaseService;

public interface SysAuthService extends BaseService<SysAuthMapper, SysAuthExample, SysAuth, Integer> {
}