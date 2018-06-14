package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysUserOrgMapper;
import com.chelaile.auth.model.entity.SysUserOrg;
import com.chelaile.auth.model.entity.SysUserOrgExample;
import com.chelaile.auth.service.SysUserOrgService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserOrgServiceImpl extends BaseServiceImpl<SysUserOrgMapper, SysUserOrgExample, SysUserOrg, Integer> implements SysUserOrgService {
}