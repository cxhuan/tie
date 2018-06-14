package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysUserOrgMenu;
import com.chelaile.auth.model.entity.SysUserOrgMenuExample;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysUserOrgMenuService;
import com.chelaile.auth.util.RedisUtil;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/sys/user-org-menu")
public class SysUserOrgMenuController extends BaseController {

	@Resource
	private SysUserOrgMenuService sysUserOrgMenuService;

	@Autowired
	private SysAuthRedisService sysAuthRedisService;

	/**
	 * 配置用户对应机构，对应菜单
	 * 
	 * @param userId
	 * @param orgId
	 * @param menuIds
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "配置用户机构对应菜单")
	public Object addSysUserOrgMenu(Integer userId, Integer orgId, String[] menuIds) {
		if (userId == null || orgId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysUserOrgMenuExample sysUserOrgMenuExample = new SysUserOrgMenuExample();
		sysUserOrgMenuExample.createCriteria().andUserIdEqualTo(userId)
				.andOrgIdEqualTo(orgId);
		sysUserOrgMenuService.deleteByExample(sysUserOrgMenuExample);

		if (ArrayUtils.isNotEmpty(menuIds)) {
			for (String menuId : menuIds) {
				SysUserOrgMenu sysUserOrgMenu = new SysUserOrgMenu();
				sysUserOrgMenu.setUserId(userId);
				sysUserOrgMenu.setOrgId(orgId);
				sysUserOrgMenu.setMenuId(Integer.parseInt(menuId));
				sysUserOrgMenuService.save(sysUserOrgMenu);
			}
		}

		sysAuthRedisService.deleteByKey(String.format(AuthRedisKey.COMMON_AUTH_KEY, userId, orgId));

		return JResponse.success("用户机构菜单关联成功");
	}
}