package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysOrgMenu;
import com.chelaile.auth.model.entity.SysOrgMenuExample;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysOrgMenuService;
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
@RequestMapping(value = "/sys")
public class SysOrgMenuController extends BaseController {

	@Resource
	private SysOrgMenuService sysOrgMenuService;

	@Autowired
	private SysAuthRedisService sysAuthRedisService;

	/**
	 * 配置机构对应的菜单
	 * 
	 * @param orgId
	 * @param menuIds
	 * @return
	 */
	@RequestMapping(value = "/org/menu/add", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "根据机构配置菜单")
	public Object addSysOrgMenu(Integer orgId, String[] menuIds)  {
		if (orgId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}
		SysOrgMenuExample sysOrgMenuExample = new SysOrgMenuExample();
		sysOrgMenuExample.createCriteria().andOrgIdEqualTo(orgId);
		sysOrgMenuService.deleteByExample(sysOrgMenuExample);

		if (ArrayUtils.isNotEmpty(menuIds)) {
			for (String menuId : menuIds) {
				SysOrgMenu sysOrgMenu = new SysOrgMenu();
				sysOrgMenu.setOrgId(orgId);
				sysOrgMenu.setMenuId(Integer.parseInt(menuId));
				sysOrgMenuService.save(sysOrgMenu);
			}
		}

		sysAuthRedisService.deleteByKey(AuthRedisKey.COMMON_AUTH_PRE + "*");
		return JResponse.success("机构菜单关联成功");
	}

	/**
	 * Description: 根据菜单配置机构
	 *
	 * @param menuId
	 * @param orgIds
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 15:46
	 */
	@RequestMapping(value = "/menu/org/add", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "根据菜单配置机构")
	public Object addSysOrgMenuNew(Integer menuId, String[] orgIds) {
		if (menuId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysOrgMenuExample sysOrgMenuExample = new SysOrgMenuExample();
		sysOrgMenuExample.createCriteria().andMenuIdEqualTo(menuId);
		sysOrgMenuService.deleteByExample(sysOrgMenuExample);

		if (ArrayUtils.isNotEmpty(orgIds)) {
			for (String orgId : orgIds) {
				SysOrgMenu sysOrgMenu = new SysOrgMenu();
				sysOrgMenu.setMenuId(menuId);
				sysOrgMenu.setOrgId(Integer.parseInt(orgId));
				sysOrgMenuService.save(sysOrgMenu);
			}
		}

		sysAuthRedisService.deleteByKey(AuthRedisKey.COMMON_AUTH_PRE + "*");
		return JResponse.success("机构菜单关联成功");
	}
}