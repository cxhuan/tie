package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysOrgMenuMapper;
import com.chelaile.auth.model.entity.SysOrgMenu;
import com.chelaile.auth.model.entity.SysOrgMenuExample;
import com.chelaile.auth.service.SysOrgMenuService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysOrgMenuServiceImpl extends BaseServiceImpl<SysOrgMenuMapper, SysOrgMenuExample, SysOrgMenu, Integer> implements SysOrgMenuService {
}