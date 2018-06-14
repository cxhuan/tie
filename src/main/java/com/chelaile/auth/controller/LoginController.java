package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.config.EnvConfig;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.UserConst;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.dto.SysUserDto;
import com.chelaile.auth.model.entity.SysOrg;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.model.entity.SysUserExample;
import com.chelaile.auth.service.SysOrgRedisService;
import com.chelaile.auth.service.SysUserRedisService;
import com.chelaile.auth.service.SysUserService;
import com.chelaile.auth.util.CommonProcessUtil;
import com.chelaile.auth.util.DateUtil;
import com.chelaile.auth.util.IPUtil;
import com.chelaile.auth.util.SecuritySessionUtil;
import com.chelaile.auth.util.verifycode.ImageCode;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.BusinessException;
import com.chelaile.base.exception.ErrCodeConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class LoginController extends BaseController {

	@Resource
	private SysUserService sysUserService;

	@Autowired
	private SysUserRedisService sysUserRedisService;

	@Autowired
	private SysOrgRedisService sysOrgRedisService;

	@Autowired
	private EnvConfig envConfig;

	@Resource
	private DiyMapper diyMapper;

	@RequestMapping(value = "/redirect", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object redirect() {

		return JResponse.fail(ErrCodeConstant.AUTH_REDIRECT);
	}

	/**
	 * Description: 生成验证码
	 *
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/13 20:49
	 */
	@RequestMapping("/verify-code")
	public void generateVerifyCode(HttpServletRequest httpServletRequest,
								   HttpServletResponse httpServletResponse) throws Exception{
		OutputStream os = httpServletResponse.getOutputStream();
		Map<String,Object> map = ImageCode.getImageCode(60, 20);
		httpServletRequest.getSession().setAttribute("checkcode", map.get("codeText").toString().toLowerCase());
		httpServletRequest.getSession().setAttribute("codeTime",new Date().getTime());
		try {
			ImageIO.write((BufferedImage) map.get("image"), "JPEG", os);
		} catch (IOException e) {
			throw new BusinessException(ErrCodeConstant.VERIFY_CODE_GENERATE_FAIL);
		}
	}

	/**
	 * Description: 验证验证码
	 *
	 * @param request
	 * @return:
	 *
	 * @auther: cxhuan
	 * @date: 2018/6/13 11:24
	 */
	public boolean checkcode(HttpServletRequest request) throws Exception {
		String checkCode = request.getParameter("verifyCode");
		Object cko = request.getSession().getAttribute("checkcode");//验证码对象
		if(cko == null){
			return false;
		}
		String captcha = cko.toString();
		Date now = new Date();
		Long codeTime = Long.valueOf(request.getSession().getAttribute("codeTime")+"");
		if(StringUtils.isEmpty(checkCode) || captcha == null ||  !(checkCode.equalsIgnoreCase(captcha))) {
			return false;
		} else if (now.getTime() - codeTime > AuthConst.VERIFY_CODE_EXPIRE) {
			//验证码有效时长为5分钟
			return false;
		}else {
			request.getSession().removeAttribute("checkcode");
		}
		return true;
	}

	/**
	 * Description: 登录
	 *
	 * @param request
	 * @param username
	 * @param password
	 * @param verifyCode
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/13 20:49
	 */
	@RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "登陆")
	public Object login(HttpServletRequest request,
						@RequestParam(required = false) String username,
                        @RequestParam(required = false) String password,
						@RequestParam(required = false) String verifyCode) throws Exception {
		if (username == null || password == null || verifyCode == null) {
			return redirect();
		}

		/*---START---验证码校验*/
		if (envConfig.isVerifyCode() && !checkcode(request)) {
			return JResponse.fail(ErrCodeConstant.AUTH_VERIFY_CODE_INCORRECT);
		}
		/*---END---验证码校验*/

		/*---START---校验用户 */
		SysUser sysUserCheck = null;
		SysUserExample sysUserExample = new SysUserExample();
		sysUserExample.createCriteria().andUserAccountEqualTo(username);
		List<SysUser> tmp = sysUserService.listByExample(sysUserExample);
		if (!tmp.isEmpty()) {
			sysUserCheck = tmp.get(0);
		}
		if (sysUserCheck == null) {
			return JResponse.fail(ErrCodeConstant.AUTH_LOGIN_FAIL);
		}
		if (UserConst.INVALID == sysUserCheck.getStatus()) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_INVALID);
		}
		if (LocalDateTime.now().isAfter(DateUtil.parseTime(sysUserCheck.getEffectiveTime()))) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_EXPIRE);
		}
		/*---END---校验用户 */

		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		try {
			subject.login(token);
			if (!subject.isAuthenticated()) {
				return JResponse.fail(ErrCodeConstant.AUTH_LOGIN_FAIL);
			}
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return JResponse.fail(ErrCodeConstant.AUTH_LOGIN_FAIL);
		}

		//从redis获取用户信息
		SysUser sysUser = sysUserRedisService.getById(sysUserCheck.getId());
		if (sysUser == null) {
			sysUser = sysUserService.getById(sysUserCheck.getId());
			sysUserRedisService.set(sysUser);
		}

		sysUser.setLastLoginTime(DateUtil.getCurrentDatetime());
		sysUser.setLastLoginIp(IPUtil.getIpFromRequest(request));
		sysUserService.updateByIdSelective(sysUser);

		List<SysOrg> orgList = sysOrgRedisService.getByUserId(sysUser.getId());
		if (orgList == null) {
			orgList = diyMapper.getOrgByUser(sysUser.getId());
			sysOrgRedisService.add(orgList, sysUser.getId());
		}

		CommonProcessUtil.handleCityCn(orgList);
		SysUserDto sysUserRet = new SysUserDto();
		sysUserRet.setId(sysUser.getId());
		sysUserRet.setUserAccount(sysUser.getUserAccount());
		sysUserRet.setUserName(sysUser.getUserName());
		sysUserRet.setOrgList(orgList);
		return JResponse.success(sysUserRet);
	}

	/**
	 * Description: 退出登录
	 *
	 * @param 
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/13 21:05
	 */
	@RequestMapping(value = "/logout", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object logout() {
		SecuritySessionUtil.init().removeAttribute(AuthConst.SESSION_USER);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.logout();
		} catch (AuthenticationException e) {
			e.printStackTrace();
			log.error(e.toString());
			return JResponse.fail(ErrCodeConstant.SYSTEM_INTERNAL_EXCEPTION);
		}
		return JResponse.success("退出成功");
	}

	/**
	 * Description: 修改密码
	 *  
	 * @param oldPassword
	 * @param newPassword
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/13 21:14
	 */
	@RequestMapping(value = "/password/change", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "修改密码")
	public Object changePassword(@RequestParam(required = true) String oldPassword,
                                 @RequestParam(required = true) String newPassword) {
		if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysUser sysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		String passwd = new SimpleHash("SHA-1", "", oldPassword).toString();
		SysUserExample sysUserExample = new SysUserExample();
		sysUserExample.createCriteria().andUserAccountEqualTo(sysUser.getUserAccount())
				.andUserPasswordEqualTo(passwd);
		List<SysUser> listUser = sysUserService.listByExample(sysUserExample);
		if (CollectionUtils.isEmpty(listUser)) {
			return JResponse.fail(ErrCodeConstant.AUTH_OLD_PASSWORD_ERROR);
		}

		String newPasswd = new SimpleHash("SHA-1", "", newPassword).toString();
		sysUser = listUser.get(0);
		sysUser.setUserPassword(newPasswd);
		sysUserService.updateByIdSelective(sysUser);
		sysUserRedisService.set(sysUser);
		
		return JResponse.success("修改密码成功");
	}

	/**
	 * Description: 重置密码
	 *  
	 * @param userId
	 * @return: 
	 *       
	 * @auther: cxhuan
	 * @date: 2018/6/13 21:15
	 */
	@RequestMapping(value = "/password/reset", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	@SystemLog(description = "重置密码")
	public Object resetPassword(Integer userId) throws Exception {
		if (userId == null) {
			return JResponse.fail(ErrCodeConstant.PARAM_IS_NULL);
		}

		SysUser currSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
		SysUser opUser = sysUserService.getById(currSysUser.getId());
		if (opUser.getUserType() == null || opUser.getUserType() != UserConst.SUPER_MANAGER) {
			return JResponse.fail(ErrCodeConstant.AUTH_USER_OP_UNPERMISSION);
		}

		String passwd = new SimpleHash("SHA-1", "", AuthConst.DEFUALT_PASSWORD).toString();
		SysUser upDto = new SysUser();
		upDto.setId(userId);
		upDto.setUserPassword(passwd);
		sysUserService.updateByIdSelective(upDto);

		SysUser user = sysUserService.getById(userId);
		if (user != null) {
			sysUserRedisService.set(user);
		}

		return JResponse.success("重置密码成功");
	}
}