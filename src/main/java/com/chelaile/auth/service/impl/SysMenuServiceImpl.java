package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysMenuMapper;
import com.chelaile.auth.model.entity.SysMenu;
import com.chelaile.auth.model.entity.SysMenuExample;
import com.chelaile.auth.service.SysMenuService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuMapper, SysMenuExample, SysMenu, Integer> implements SysMenuService {
}