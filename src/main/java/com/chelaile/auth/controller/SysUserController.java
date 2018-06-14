package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.constants.UserConst;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.model.entity.SysUserExample;
import com.chelaile.auth.service.SysUserRedisService;
import com.chelaile.auth.service.SysUserService;
import com.chelaile.auth.util.DateUtil;
import com.chelaile.auth.util.RedisUtil;
import com.chelaile.auth.util.SecuritySessionUtil;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import com.github.pagehelper.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Controller;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/user")
public class SysUserController extends BaseController {

	@Resource
	private SysUserService sysUserService;

	@Resource
	private SysUserRedisService sysUserRedisService;

	@Resource
	private DiyMapper diyMapper;

	/**
	 * 新增用户信息
	 * 
	 * @param sysUser
	 * @return
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "新增用户")
	public Object addSysUser(SysUser sysUser) {
		if (sysUser == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (StringUtils.isBlank(sysUser.getUserAccount())
				|| StringUtils.isBlank(sysUser.getUserName())) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		//todo 邮箱电话验证

		if (sysUser.getUserType() != UserConst.SUPER_MANAGER
				&& sysUser.getUserType() != UserConst.MANAGER
				&& sysUser.getUserType() != UserConst.COMMON) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_INVALID);
		}

		SysUser currSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		currSysUser = sysUserService.getById(currSysUser.getId());
		// 只能添加下级的用户
		if (currSysUser.getUserType() >= sysUser.getUserType()) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_OP_UNPERMISSION);
		}

		SysUserExample example = new SysUserExample();
		example.createCriteria().andUserAccountEqualTo(sysUser.getUserAccount());
		List<SysUser> list = sysUserService.listByExample(example);
		if (CollectionUtils.isNotEmpty(list)) {
			return JResponse.fail(ErrCodeConstant.USER_DUPLICATE);
		}

		sysUser.setUserPassword(new SimpleHash("SHA-1", "",
				AuthConst.DEFUALT_PASSWORD).toString());
		sysUser.setStatus(UserConst.VALID);
		sysUser.setCreateUser(StringUtils.isBlank(currSysUser.getCreateUser()) ? currSysUser.getUserAccount()
				: (currSysUser.getCreateUser() + "@" + currSysUser.getUserAccount()));
		sysUser.setUpdateUser(currSysUser.getUserAccount());
		sysUser.setCreateTime(DateUtil.getCurrentDatetime());
		sysUser.setUpdateTime(DateUtil.getCurrentDatetime());
		sysUserService.save(sysUser);
		return JResponse.success("添加用户成功");
	}

	/**
	 * 修改用户信息
	 * 
	 * @param sysUserDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/update", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "修改用户")
	public Object updateSysUser(SysUser sysUserDto) {
		if (sysUserDto == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		if (sysUserDto.getId() == null) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}
		if (StringUtils.isNotBlank(sysUserDto.getUserAccount())) {
			return JResponse.fail(ErrCodeConstant.USER_ACCOUNT_CANNOT_MODIFY);
		}

		//todo 邮箱电话验证

		if (sysUserDto.getUserType() != UserConst.SUPER_MANAGER
				&& sysUserDto.getUserType() != UserConst.MANAGER
				&& sysUserDto.getUserType() != UserConst.COMMON) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_INVALID);
		}

		SysUser sysUserOrigin = sysUserService.getById(sysUserDto.getId());
		SysUser currSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		SysUser opUser = sysUserRedisService.getById(currSysUser.getId());
		if (opUser == null) {
			opUser = sysUserService.getById(currSysUser.getId());
			sysUserRedisService.set(opUser);
		}

		// 只能修改下级的用户信息
		if (opUser.getUserType() >= sysUserOrigin.getUserType()) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_OP_UNPERMISSION);
		}
		if (opUser.getUserType() >= sysUserDto.getUserType()) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_OP_UNPERMISSION);
		}

		sysUserDto.setUpdateUser(opUser.getUserAccount());
		sysUserDto.setUpdateTime(DateUtil.getCurrentDatetime());
		// 防注入改密
		if (StringUtils.isNotBlank(sysUserDto.getUserPassword())) {
			sysUserDto.setUserPassword(null);
		}
		sysUserService.updateByIdSelective(sysUserDto);
		SysUser user = sysUserService.getById(sysUserDto.getId());
		sysUserRedisService.set(user);

		return JResponse.success("修改用户信息成功");
	}

	/**
	 * Description: 查询用户信息(列表,分页)
	 *  
	 * @param userStatus
	 * @param userAccount
	 * @param username
	 * @param page
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/14 11:37
	 */
	@RequestMapping(value = "/page", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysUserPaged(Integer userStatus, String userAccount,
									 String username, Page<SysUser> page) {
		SysUserExample sysUserExample = new SysUserExample();
		SysUserExample.Criteria c = sysUserExample.createCriteria();

		/*
		 *	只能查询当前用户创建的用户树
		 */
		SysUser curSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		SysUser user = sysUserRedisService.getById(curSysUser.getId());
		if (user == null) {
			user = sysUserService.getById(curSysUser.getId());
			if (user == null) {
				return JResponse.fail(ErrCodeConstant.AUTH_USER_NOT_EXIST);
			}
			sysUserRedisService.set(user);
		}
		c.andCreateUserLike(user.getUserAccount() + "@%");

		if (StringUtils.isNotBlank(userAccount)) {
			c.andUserAccountLike("%" + userAccount + "%");
		}
		if (StringUtils.isNotBlank(username)) {
			c.andUserNameLike("%" + username + "%");
		}
		if (userStatus != null) {
			c.andStatusEqualTo(userStatus);
		}

		sysUserService.pageByExample(sysUserExample, page);

		return JResponse.success(page);
	}

	/**
	 * 根据权限获取对应用户信息
	 * 
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list-by-auth", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object selectSysUserByAuthId(Integer authId) {
		if (authId == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}
		List<SysUser> list = diyMapper.getSysUserByAuthId(authId);
		return JResponse.success(list);
	}
}