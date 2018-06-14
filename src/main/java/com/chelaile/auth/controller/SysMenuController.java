package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.MenuConst;
import com.chelaile.auth.constants.UserConst;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.*;
import com.chelaile.auth.service.*;
import com.chelaile.auth.util.DateUtil;
import com.chelaile.auth.util.SecuritySessionUtil;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/menu")
public class SysMenuController extends BaseController {

	@Resource
	private SysUserService sysUserService;

	@Autowired
	private SysUserRedisService sysUserRedisService;

	@Resource
	private SysMenuService sysMenuService;

	@Resource
	private SysOrgMenuService sysOrgMenuService;

	@Resource
	private SysMenuAuthService sysMenuAuthService;

	@Resource
	private SysUserOrgMenuService sysUserOrgMenuService;

	@Resource
	private DiyMapper diyMapper;

	/**
	 * 新增菜单
	 * 
	 * @param sysMenu
	 * @return
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "新增菜单")
	public Object addSysMenu(SysMenu sysMenu) {
		if (sysMenu == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (StringUtils.isBlank(sysMenu.getMenuName())) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysUser currSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		SysUser user = sysUserRedisService.getById(currSysUser.getId());
		if (user == null) {
			user = sysUserService.getById(currSysUser.getId());
			sysUserRedisService.set(user);
		}
		if (user.getUserType() == null || user.getUserType() != UserConst.SUPER_MANAGER) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_OP_UNPERMISSION);
		}

		if (sysMenu.getParentId() == null) {
			sysMenu.setMenuLevel(0);
		} else {
			SysMenu sysMenuParent = sysMenuService.getById(sysMenu.getParentId());
			if (sysMenuParent == null) {
				//父级菜单不存在
				return JResponse.fail(ErrCodeConstant.PARAM_IS_INVALID);
			}
			sysMenu.setMenuLevel(sysMenuParent.getMenuLevel() == null ? 1 : (sysMenuParent.getMenuLevel() + 1));
		}
		sysMenu.setMenuStatus(MenuConst.VALID);
		sysMenu.setUpdateUser(user.getUserAccount());
		sysMenu.setCreateTime(DateUtil.getCurrentDatetime());
		sysMenu.setUpdateTime(DateUtil.getCurrentDatetime());
		int addId = sysMenuService.save(sysMenu);
		return JResponse.success(addId);
	}

	/**
	 * 删除菜单
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "删除菜单")
	public Object deleteSysMenu(Integer id) {
		if (id == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysMenuAuthExample sysMenuAuthExample = new SysMenuAuthExample();
		sysMenuAuthExample.createCriteria().andMenuIdEqualTo(id);
		sysMenuAuthService.deleteByExample(sysMenuAuthExample);

		SysOrgMenuExample sysOrgMenuExample = new SysOrgMenuExample();
		sysOrgMenuExample.createCriteria().andMenuIdEqualTo(id);
		sysOrgMenuService.deleteByExample(sysOrgMenuExample);

		SysUserOrgMenuExample sysUserOrgMenuExample = new SysUserOrgMenuExample();
		sysUserOrgMenuExample.createCriteria().andMenuIdEqualTo(id);
		sysUserOrgMenuService.deleteByExample(sysUserOrgMenuExample);

		sysMenuService.deleteById(id);

		return JResponse.success("删除菜单成功");
	}

	/**
	 * 获取所有的菜单
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list-all", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object getAllSysMenu() {
		return JResponse.success(sysMenuService.listByExample(new SysMenuExample()));
	}

	/**
	 * 更新菜单
	 * 
	 * @param sysMenuDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/update", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "更新菜单")
	public Object updateSysMenu(SysMenu sysMenuDto) throws Exception {
		if (sysMenuDto == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (sysMenuDto.getId() == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysUser currSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		SysUser user = sysUserRedisService.getById(currSysUser.getId());
		if (user == null) {
			user = sysUserService.getById(currSysUser.getId());
			sysUserRedisService.set(user);
		}

		if (user.getUserType() == null || user.getUserType() != UserConst.SUPER_MANAGER) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_OP_UNPERMISSION);
		}

		if (sysMenuDto.getParentId() != null) {
			SysMenu sysOrgParent = sysMenuService.getById(sysMenuDto.getParentId());
			sysMenuDto.setMenuLevel(sysOrgParent.getMenuLevel() == null ? 1 : (sysOrgParent.getMenuLevel() + 1));
		}
		sysMenuDto.setCreateTime(DateUtil.getCurrentDatetime());
		sysMenuDto.setUpdateTime(DateUtil.getCurrentDatetime());
		sysMenuDto.setUpdateUser(user.getUserAccount());
		sysMenuService.updateByIdSelective(sysMenuDto);
		return JResponse.success("修改菜单信息成功");
	}

	/**
	 * Description: 获取菜单(列表,分页)
	 *
	 * @param menuName
	 * @param menuUrl
	 * @param status
	 * @param page
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 14:16
	 */
	@RequestMapping(value = "/page", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysMenuPaged(String menuName, String menuUrl, Integer status, Page<SysMenu> page) {
		SysMenuExample sysMenuExample = new SysMenuExample();
		SysMenuExample.Criteria c = sysMenuExample.createCriteria();
		if (StringUtils.isNotBlank(menuName)) {
			c.andMenuNameLike("%" + menuName + "%");
		}
		if (StringUtils.isNotBlank(menuUrl)) {
			c.andMenuUrlLike("%" + menuUrl + "%");
		}
		if (status != null) {
			c.andMenuStatusEqualTo(status);
		}

		return JResponse.success(sysMenuService.pageByExample(sysMenuExample, page));
	}

	/**
	 * Description: 根据用户机构查询菜单
	 *
	 * @param orgId
	 * @param userId
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 14:22
	 */
	@RequestMapping(value = "/list-by-user-org", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysUserOrgMenu(Integer orgId, Integer userId) {

		if (orgId == null || userId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		List<SysMenu> list = diyMapper.getMenuByOrgId(orgId, MenuConst.VALID);
		if (list.isEmpty()) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_INVALID);
		}

		List<SysMenu> tmpList = diyMapper.getMenuByUserIdAndOrgId(userId, orgId, MenuConst.VALID);
		if (!tmpList.isEmpty()) {
			List<Integer> idList = Lists.newArrayList();
			for (SysMenu obj : tmpList) {
				idList.add(obj.getId());
			}
			for (SysMenu sysMenu : list) {
				if (idList.contains(sysMenu.getId())) {
					sysMenu.setChecked(1);
				}
			}
		}

		return JResponse.success(list);
	}

	/**
	 * 根据机构查询菜单信息
	 * 
	 * @param orgId
	 * @return
	 */
	@RequestMapping(value = "/list-by-org", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysMenuByOrgId(Integer orgId) {
		if (orgId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		return JResponse.success(diyMapper.getMenuByOrgId(orgId, MenuConst.VALID));
	}
}