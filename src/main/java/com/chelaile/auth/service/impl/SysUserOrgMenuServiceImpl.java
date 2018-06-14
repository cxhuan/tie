package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysUserOrgMenuMapper;
import com.chelaile.auth.model.entity.SysUserOrgMenu;
import com.chelaile.auth.model.entity.SysUserOrgMenuExample;
import com.chelaile.auth.service.SysUserOrgMenuService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserOrgMenuServiceImpl extends BaseServiceImpl<SysUserOrgMenuMapper, SysUserOrgMenuExample, SysUserOrgMenu, Integer> implements SysUserOrgMenuService {
}