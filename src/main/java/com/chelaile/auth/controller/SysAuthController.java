package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysAuth;
import com.chelaile.auth.model.entity.SysAuthExample;
import com.chelaile.auth.model.entity.SysUserAuthExample;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysAuthService;
import com.chelaile.auth.service.SysMenuAuthService;
import com.chelaile.auth.service.SysUserAuthService;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/auth")
public class SysAuthController extends BaseController {

	@Resource
	private SysAuthService sysAuthService;

	@Resource
	private SysMenuAuthService sysMenuAuthService;

	@Resource
	private SysUserAuthService sysUserAuthService;

	@Autowired
	private SysAuthRedisService sysAuthRedisService;

	@Resource
	private DiyMapper diyMapper;

	/**
	 * 新增权限信息
	 * 
	 * @param sysAuth
	 * @return
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "新增权限")
	public Object addSysAuth(SysAuth sysAuth) {
		if (sysAuth == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (StringUtils.isBlank(sysAuth.getAuthName()) || StringUtils.isBlank(sysAuth.getAuthUrl())) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}
		sysAuthService.save(sysAuth);
		return JResponse.success("添加权限信息成功");
	}

	/**
	 * 删除权限信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "删除权限")
	public Object deleteSysAuth(Integer id) {
		if (id == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}
		sysMenuAuthService.deleteById(id);

		SysUserAuthExample sysUserAuthExample = new SysUserAuthExample();
		sysUserAuthExample.createCriteria().andAuthIdEqualTo(id);
		sysUserAuthService.deleteByExample(sysUserAuthExample);

		sysAuthService.deleteById(id);
		sysAuthRedisService.deleteByKey(AuthRedisKey.COMMON_AUTH_PRE + "*");

		return JResponse.success("删除权限信息成功");
	}

	/**
	 * 获取所有权限信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list-all", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object getAllSysAuth() {
		List<SysAuth> list = sysAuthService.listByExample(new SysAuthExample());
		return JResponse.success(list);
	}

	/**
	 * 修改权限信息
	 * 
	 * @param sysAuth
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/update", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "更新权限")
	public Object updateSysAuth(SysAuth sysAuth) throws Exception {
		if (sysAuth == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (sysAuth.getId() == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}
		sysAuthService.updateByIdSelective(sysAuth);
		sysAuthRedisService.deleteByKey(AuthRedisKey.COMMON_AUTH_PRE + "*");

		return JResponse.success("修改权限信息成功");
	}

	/**
	 * 获取权限信息(列表,分页)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/page", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysAuthPaged(String authName, String authUrl, Page<SysAuth> page) {
		SysAuthExample example = new SysAuthExample();
		SysAuthExample.Criteria c = example.createCriteria();
		if (StringUtils.isNotBlank(authName)) {
			c.andAuthNameLike("%" + authName + "%");
		}
		if (StringUtils.isNotBlank(authUrl)) {
			c.andAuthUrlLike("%" + authUrl + "%");
		}
		sysAuthService.pageByExample(example, page);

		return JResponse.success(page);
	}

	/**
	 * 根据菜单获取权限
	 *
	 * @param menuId
	 * @return
	 */
	@RequestMapping(value = "/list-by-menu", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysAuthByMenuId(Integer menuId) {
		if (menuId == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		List<SysAuth> list = diyMapper.getSysAuthByMenuId(menuId);

		return JResponse.success(list);
	}
}