package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysUserAuth;
import com.chelaile.auth.model.entity.SysUserAuthExample;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysUserAuthService;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/sys/auth/user")
public class SysUserAuthController extends BaseController {

	@Resource
	private SysUserAuthService sysUserAuthService;

	@Autowired
	private SysAuthRedisService sysAuthRedisService;

	/**
	 * Description: 根据权限配置用户白名单
	 *
	 * @param authId
	 * @param userIds
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 15:50
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "根据权限配置用户白名单")
	public Object addSysUserAuth(Integer authId, String[] userIds) {
		if (authId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysUserAuthExample sysUserAuthExample = new SysUserAuthExample();
		sysUserAuthExample.createCriteria().andAuthIdEqualTo(authId);
		sysUserAuthService.deleteByExample(sysUserAuthExample);

		if (ArrayUtils.isNotEmpty(userIds)) {
			for (String userId : userIds) {
				SysUserAuth sysUserAuth = new SysUserAuth();
				sysUserAuth.setAuthId(authId);
				sysUserAuth.setUserId(Integer.parseInt(userId));
				sysUserAuthService.save(sysUserAuth);
			}
		}

		if (ArrayUtils.isNotEmpty(userIds)) {
			for (String userId : userIds) {
				sysAuthRedisService.deleteByKey(String.format(AuthRedisKey.COMMON_AUTH_KEY, userId, "*"));
			}
		}
		return JResponse.success("用户权限信息关联成功");
	}
}