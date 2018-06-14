package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysMenuAuthMapper;
import com.chelaile.auth.model.entity.SysMenuAuth;
import com.chelaile.auth.model.entity.SysMenuAuthExample;
import com.chelaile.auth.service.SysMenuAuthService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysMenuAuthServiceImpl extends BaseServiceImpl<SysMenuAuthMapper, SysMenuAuthExample, SysMenuAuth, Integer> implements SysMenuAuthService {
}