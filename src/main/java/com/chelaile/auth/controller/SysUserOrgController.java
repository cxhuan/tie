package com.chelaile.auth.controller;

import com.chelaile.auth.base.BaseController;
import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.constants.AuthRedisKey;
import com.chelaile.auth.dao.DiyMapper;
import com.chelaile.auth.interceptor.SystemLog;
import com.chelaile.auth.model.entity.SysOrg;
import com.chelaile.auth.model.entity.SysUserOrg;
import com.chelaile.auth.model.entity.SysUserOrgExample;
import com.chelaile.auth.service.SysAuthRedisService;
import com.chelaile.auth.service.SysOrgRedisService;
import com.chelaile.auth.service.SysUserOrgService;
import com.chelaile.auth.util.RedisUtil;
import com.chelaile.base.JResponse;
import com.chelaile.base.exception.ErrCodeConstant;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/sys/user/org")
public class SysUserOrgController extends BaseController {

	@Resource
	private SysUserOrgService sysUserOrgService;

	@Autowired
	private SysOrgRedisService sysOrgRedisService;

	@Resource
	private DiyMapper diyMapper;

	/**
	 * 配置用户对应机构
	 * 
	 * @param userId
	 * @param orgIds
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8")
	@ResponseBody
	@SystemLog(description = "配置用户和机构对应关系")
	public Object addSysUserOrg(Integer userId, String[] orgIds) throws Exception {
		if (userId == null || ArrayUtils.isEmpty(orgIds)) {
			return JResponse.fail(ErrCodeConstant.LACK_NECESSARY_PARAM);
		}

		SysUserOrgExample sysUserOrgExample = new SysUserOrgExample();
		sysUserOrgExample.createCriteria().andUserIdEqualTo(userId);
		sysUserOrgService.deleteByExample(sysUserOrgExample);

		for (String orgId : orgIds) {
			SysUserOrg sysUserOrg = new SysUserOrg();
			sysUserOrg.setUserId(userId);
			sysUserOrg.setOrgId(Integer.parseInt(orgId));
			sysUserOrgService.save(sysUserOrg);
		}

		//更新redis信息
		List<SysOrg> orgList = diyMapper.getOrgByUser(userId);
		sysOrgRedisService.add(orgList, userId);

		return JResponse.success("用户机构关联成功");
	}
}