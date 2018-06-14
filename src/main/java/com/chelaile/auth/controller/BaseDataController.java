package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.base.City;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.MenuConst;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.model.dto.SysUserOrgMenuDto;
import com.chelaile.auth.model.entity.SysMenu;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.util.ConfigPropertiesUtil;
import com.chelaile.auth.util.SecuritySessionUtil;
import com.chelaile.base.exception.BusinessException;
import com.chelaile.base.exception.ErrCodeConstant;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping(value = "/base")
public class BaseDataController extends BaseController {

	@Resource
	private DiyMapper diyMapper;

	/**
	 * Description: 获取系统支持城市
	 *  
	 * @return:
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 16:18
	 */
	@RequestMapping(value = "/supportcities", produces = "application/json;charset=UTF-8")
	public List<City> getSupportCities() {
		return ConfigPropertiesUtil.getSupportCities();
	}

	/**
	 * 获取当前用户机构的菜单
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/menu/list-by-org", produces = "application/json;charset=UTF-8")
	public List<SysMenu> getMenu(Integer orgId) throws Exception {
		SysUser sysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		if (sysUser == null) {
			throw new BusinessException(ErrCodeConstant.AUTH_USER_NOT_EXIST);
		}

		if (orgId == null) {
			throw new BusinessException(ErrCodeConstant.AUTH_ORG_NOT_EXIST);
		}

		// 获取用户机构菜单对应关系
		List<SysMenu> listMenus = diyMapper.getMenuByUserIdAndOrgId(sysUser.getId(), orgId, MenuConst.VALID);
		if (CollectionUtils.isEmpty(listMenus)) {
			listMenus = diyMapper.getMenuByOrgId(orgId, MenuConst.VALID);
		}
		listMenus.sort(Comparator.comparing(SysMenu::getMenuOrder));

		return listMenus;
	}
}