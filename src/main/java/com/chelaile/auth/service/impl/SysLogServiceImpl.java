package com.chelaile.auth.service.impl;

import com.chelaile.auth.dao.SysLogMapper;
import com.chelaile.auth.model.entity.SysLog;
import com.chelaile.auth.model.entity.SysLogExample;
import com.chelaile.auth.service.SysLogService;
import com.chelaile.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogMapper, SysLogExample, SysLog, Long> implements SysLogService {
}