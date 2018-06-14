package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysMenuAuth;
import com.chelaile.auth.model.entity.SysMenuAuthExample;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysMenuAuthService;
import com.chelaile.auth.util.RedisUtil;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Set;

@Controller
@RequestMapping(value = "/sys/menu/auth")
public class SysMenuAuthController extends BaseController {

	@Resource
	private SysMenuAuthService sysMenuAuthService;

	@Autowired
	private SysAuthRedisService sysAuthRedisService;

	/**
	 * Description: 给菜单配置权限
	 *
	 * @param menuId
	 * @param authIds
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 15:35
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "配置菜单权限对应关系")
	public Object addSysMenuAuth(Integer menuId, String[] authIds) {
		if (menuId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysMenuAuthExample sysMenuAuthExample = new SysMenuAuthExample();
		sysMenuAuthExample.createCriteria().andMenuIdEqualTo(menuId);
		sysMenuAuthService.deleteByExample(sysMenuAuthExample);

		if (ArrayUtils.isNotEmpty(authIds)) {
			for (String authId : authIds) {
				SysMenuAuth sysMenuAuth = new SysMenuAuth();
				sysMenuAuth.setMenuId(menuId);
				sysMenuAuth.setAuthId(Integer.parseInt(authId));
				sysMenuAuthService.save(sysMenuAuth);
			}
		}

		sysAuthRedisService.deleteByKey(AuthRedisKey.COMMON_AUTH_PRE + "*");
		return JResponse.success("菜单权限关联成功");
	}
}