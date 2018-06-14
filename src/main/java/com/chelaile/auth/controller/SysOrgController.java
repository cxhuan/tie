package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.OrgConst;
import com.chelaile.auth.constants.UserConst;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysOrg;
import com.chelaile.auth.model.entity.SysOrgExample;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.service.SysOrgService;
import com.chelaile.auth.service.SysUserRedisService;
import com.chelaile.auth.service.SysUserService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/org")
public class SysOrgController extends BaseController {

	@Resource
	private SysOrgService sysOrgService;

	@Resource
	private SysUserService sysUserService;

	@Autowired
	private SysUserRedisService sysUserRedisService;

	@Resource
	private DiyMapper diyMapper;

	/**
	 * 新增机构
	 * 
	 * @param sysOrg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "新增机构")
	public Object addSysOrg(SysOrg sysOrg) throws Exception {
		if (sysOrg == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (StringUtils.isBlank(sysOrg.getOrgName()) || StringUtils.isBlank(sysOrg.getOrgNo())) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		/*
		 *	判断机构是否重复
		 */
		SysOrgExample example = new SysOrgExample();
		example.createCriteria().andOrgNoEqualTo(sysOrg.getOrgNo());
		List<SysOrg> orgList = sysOrgService.listByExample(example);
		if (!orgList.isEmpty()) {
			return JResponse.fail(ErrCodeConstant.ORG_DUPLICATE);
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

		if (sysOrg.getOrgParent() == null) {
			sysOrg.setOrgLevel(0);
			sysOrg.setDataPermission(OrgConst.DATAPERMISSION_ALL);
		} else {
			SysOrg sysOrgParent = sysOrgService.getById(sysOrg.getOrgParent());
			if (sysOrgParent == null) {
				return JResponse.fail(ErrCodeConstant.PARENT_ORG_ID_ERROR);
			}
			sysOrg.setOrgLevel(sysOrgParent.getOrgLevel() == null ? 1 : (sysOrgParent.getOrgLevel() + 1));
			sysOrg.setDataPermission(OrgConst.DATAPERMISSION_PART);
			if (sysOrg.getCityId() == null) {
				sysOrg.setCityId(sysOrgParent.getCityId());
			}
		}
		sysOrg.setStatus(OrgConst.VALID);
		sysOrg.setCreateTime(DateUtil.getCurrentDatetime());
		sysOrg.setUpdateTime(DateUtil.getCurrentDatetime());
		sysOrg.setUpdateUser(user.getUserAccount());
		int addId = sysOrgService.save(sysOrg);
		return JResponse.success(addId);
	}

	/**
	 * 获取所有机构
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list-all", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object getAllSysOrg() {
		return JResponse.success(sysOrgService.listByExample(new SysOrgExample()));
	}

	/**
	 * 修改机构信息
	 * 
	 * @param sysOrgDto
	 * @return
	 */
	@RequestMapping(value = "/update", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "修改机构")
	public Object updateSysOrg(SysOrg sysOrgDto) {
		if (sysOrgDto == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (sysOrgDto.getId() == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		if (sysOrgDto.getOrgNo() != null) {
			/*
			 *	判断机构是否重复
			 */
			SysOrgExample example = new SysOrgExample();
			example.createCriteria().andOrgNoEqualTo(sysOrgDto.getOrgNo());
			List<SysOrg> orgList = sysOrgService.listByExample(example);
			if (!orgList.isEmpty()) {
				return JResponse.fail(ErrCodeConstant.ORG_DUPLICATE);
			}
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

		if (sysOrgDto.getOrgParent() != null) {
			SysOrg sysOrgParent = sysOrgService.getById(sysOrgDto.getOrgParent());
			if (sysOrgParent == null) {
				return JResponse.fail(ErrCodeConstant.PARENT_ORG_ID_ERROR);
			}
			sysOrgDto.setOrgLevel(sysOrgParent.getOrgLevel() == null ? 1 : (sysOrgParent.getOrgLevel() + 1));
			sysOrgDto.setDataPermission(OrgConst.DATAPERMISSION_PART);
		}
		sysOrgDto.setUpdateUser(currSysUser.getUserAccount());
		sysOrgDto.setUpdateTime(DateUtil.getCurrentDatetime());
		sysOrgService.updateByIdSelective(sysOrgDto);
		return JResponse.success("修改机构信息成功");
	}

	/**
	 * 查询机构(列表,分页)
	 * 
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/page", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysOrgPaged(Integer orgId, Integer status, Page<SysOrg> page) {
		SysOrgExample sysOrgExample = new SysOrgExample();
		SysOrgExample.Criteria c = sysOrgExample.createCriteria();

		if (orgId != null) {
			c.andIdEqualTo(orgId);
		}

		if (status != null) {
			c.andStatusEqualTo(status);
		}

		return JResponse.success(sysOrgService.pageByExample(sysOrgExample, page));
	}

	/**
	 * 查询用户对应的所有机构，包含子机构
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list-by-user", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectUserSysOrg(Integer userId) throws Exception {
		if (userId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		List<SysOrg> orgListRet = Lists.newArrayList();
		List<SysOrg> orgList = diyMapper.getOrgByUser(userId);
		if (orgList != null) {
			orgList.forEach(sysOrg -> {
				SysOrgExample sysOrgExample = new SysOrgExample();
				sysOrgExample.createCriteria().andStatusEqualTo(OrgConst.VALID)
						.andOrgNoLike(sysOrg.getOrgNo() + "#%");
				List<SysOrg> tmpList = sysOrgService.listByExample(sysOrgExample);
				if (!tmpList.isEmpty()) {
					orgListRet.addAll(tmpList);
				}

			});
			orgListRet.sort(Comparator.comparing(SysOrg::getOrgNo));
		}

		return JResponse.success(orgListRet);
	}

	/**
	 * 根据菜单获取对应的机构
	 * 
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list-by-menu", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectOrgByMenuId(Integer menuId) {
		if (menuId == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}
		return JResponse.success(diyMapper.getOrgByMenuId(menuId));
	}
}