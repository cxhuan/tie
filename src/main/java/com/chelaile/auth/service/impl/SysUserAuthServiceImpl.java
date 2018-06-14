package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysUserAuthMapper;
import com.chelaile.auth.model.entity.SysUserAuth;
import com.chelaile.auth.model.entity.SysUserAuthExample;
import com.chelaile.auth.service.SysUserAuthService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserAuthServiceImpl extends BaseServiceImpl<SysUserAuthMapper, SysUserAuthExample, SysUserAuth, Integer> implements SysUserAuthService {
}