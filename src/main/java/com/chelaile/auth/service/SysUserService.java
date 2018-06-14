package com.chelaile.auth.service;

import com.chelaile.auth.dao.SysUserMapper;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.model.entity.SysUserExample;
import com.chelaile.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

public interface SysUserService extends BaseService<SysUserMapper, SysUserExample, SysUser, Integer> {
}