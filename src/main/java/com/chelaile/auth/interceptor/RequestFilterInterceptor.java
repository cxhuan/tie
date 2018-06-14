package com.chelaile.auth.interceptor;

import com.alibaba.fastjson.JSON;
import com.chelaile.auth.constants.*;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.model.entity.SysAuth;
import com.chelaile.auth.model.entity.SysMenu;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysUserService;
import com.chelaile.auth.util.DateUtil;
import com.chelaile.auth.util.SecuritySessionUtil;
import com.facebook.presto.jdbc.internal.guava.collect.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RequestFilterInterceptor extends HandlerInterceptorAdapter {

	public Logger log = LogManager.getLogger(this.getClass());

	@Resource
	private SysUserService sysUserService;

	@Resource
	private DiyMapper diyMapper;

	@Autowired
	private SysAuthRedisService sysAuthRedisService;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String path = request.getServletPath();
		if (path.matches(AuthConst.NO_INTERCEPTOR_PATH)) {
			return true;
		}

		SysUser seSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		if (seSysUser == null){
			return false;
		}

		seSysUser = sysUserService.getById(seSysUser.getId());
		if (seSysUser == null) {
			return false;
		}

		Integer userId = seSysUser.getId();
		// 判断当前用户的状态
		if (UserConst.INVALID == seSysUser.getStatus()) {
			return false;
		}

		if (LocalDateTime.now().isAfter(DateUtil.parseTime(seSysUser.getEffectiveTime()))) {
			return false;
		}

		// 超级管理员直接放行
		if (seSysUser.getUserType() == UserConst.SUPER_MANAGER) {
			return true;
		}

		List<String> authStrList = Lists.newArrayList();

		String orgIdStr = request.getParameter(AuthConst.PARAM_ORG_ID);
		if (StringUtils.isBlank(orgIdStr)) {
			return false;
		}

		Integer orgId = Integer.parseInt(orgIdStr);
		List<SysAuth> authList = sysAuthRedisService.listAuth(userId, orgId);
		if (authList == null) {
			authList = Lists.newArrayList();
			// 获取用户对应的白名单对应权限ID
			List<SysAuth> userAuth = diyMapper.getSysAuthByUserId(userId);
			List<Integer> userAuthIdList = Lists.newArrayList();
			for (SysAuth sa : userAuth) {
				userAuthIdList.add(sa.getId());
			}

			List<SysMenu> m1 = diyMapper.getMenuByUserIdAndOrgId(userId, orgId, MenuConst.VALID);
			if (CollectionUtils.isEmpty(m1)) {
				m1 = diyMapper.getMenuByOrgId(orgId, MenuConst.VALID);
			}
			// 获取权限信息(去重之后)
			Map<Integer, SysAuth> authMap = Maps.newHashMap();
			for (SysMenu sysMenu : m1) {
				List<SysAuth> tp = diyMapper.getSysAuthByMenuId(sysMenu.getId());
				for (SysAuth sysAuth : tp) {
					authMap.put(sysAuth.getId(), sysAuth);
				}
			}
			// 权限列表中的黑名单权限判断
			for (Integer i : authMap.keySet()) {
				SysAuth t = authMap.get(i);
				if (t.getGlobalBlacklist() != null && t.getGlobalBlacklist() == AuthConst.GLOBALBLACKLIST_V
						&& !userAuthIdList.contains(t.getId()))
					continue;
				authList.add(t);
			}

			sysAuthRedisService.setAuthList(userId, orgId, authList);
		}
		for (SysAuth s : authList) {
			authStrList.add(s.getAuthUrl());
		}
		if (authStrList.contains(path)) {
			return true;
		}

		// TODO 拦截日志记录
		return false;
	}
}
