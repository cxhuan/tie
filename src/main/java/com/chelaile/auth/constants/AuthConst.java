package com.chelaile.auth.constants;

import com.chelaile.auth.base.BaseModel;

public class AuthConst extends BaseModel {

	private static final long serialVersionUID = 1L;

	public static final int REDIS_COMMON_EXPIRE = 3600;//用户session过期时间为1小时

	public static final int VERIFY_CODE_EXPIRE = 300000;//验证码有效期为5分钟

	public static final int GLOBALBLACKLIST_V = 1;

	public static final String DEFUALT_PASSWORD = "chelaile123";

	public static final String SESSION_USER = "SESSIONUSER";

	public static final String PARAM_ORG_ID = "orgId";

	public static final String NO_INTERCEPTOR_PATH = ".*/((monitor)|(redirect)|(busrun)|(api)|(web)|(login)|(index)).*"; // 不对匹配该值的访问路径拦截（正则）

	public static <T> String getRedisKey(String pre, T t) {
		return pre + t;
	}

	public static String getRedisDataKey(String pre, Integer userId, Integer orgId) {
		return pre + userId + ":" + orgId;
	}
}