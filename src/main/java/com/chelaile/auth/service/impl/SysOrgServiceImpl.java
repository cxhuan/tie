package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysOrgMapper;
import com.chelaile.auth.model.entity.SysOrg;
import com.chelaile.auth.model.entity.SysOrgExample;
import com.chelaile.auth.service.SysOrgService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysOrgServiceImpl extends BaseServiceImpl<SysOrgMapper, SysOrgExample, SysOrg, Integer> implements SysOrgService {
}