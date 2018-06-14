package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysUserMapper;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.model.entity.SysUserExample;
import com.chelaile.auth.service.SysUserService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUserExample, SysUser, Integer> implements SysUserService {
}