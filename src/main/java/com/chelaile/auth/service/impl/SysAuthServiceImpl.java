package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysAuthMapper;
import com.chelaile.auth.model.entity.SysAuth;
import com.chelaile.auth.model.entity.SysAuthExample;
import com.chelaile.auth.service.SysAuthService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysAuthServiceImpl extends BaseServiceImpl<SysAuthMapper, SysAuthExample, SysAuth, Integer> implements SysAuthService {
}