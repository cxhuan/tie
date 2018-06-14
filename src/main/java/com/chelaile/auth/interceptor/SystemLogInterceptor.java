package com.chelaile.auth.interceptor;

import com.chelaile.auth.constants.AuthConst;
import com.chelaile.auth.model.entity.SysLog;
import com.chelaile.auth.model.entity.SysUser;
import com.chelaile.auth.service.SysLogService;
import com.chelaile.auth.service.SysUserService;
import com.chelaile.auth.util.ConfigPropertiesUtil;
import com.chelaile.auth.util.DateUtil;
import com.chelaile.auth.util.IPUtil;
import com.chelaile.auth.util.SecuritySessionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 切点类
 * 
 * @author xiaoyingtong
 * @since 2018-05-25 Pm 20:35
 * @version 1.0
 */
@Aspect
@Component
public class SystemLogInterceptor {

	public Logger log = LogManager.getLogger(this.getClass());

	@Resource
	private SysLogService sysLogService;

	@Resource
	private SysUserService sysUserService;

	@Pointcut("@annotation(com.chelaile.auth.interceptor.SystemLog)")
	public void systemLogAspect() {
	}

	/**
	 * 记录用户的操作
	 *
	 * @param joinPoint 切点
	 */
	@Around("systemLogAspect()")
	public Object systemLog(ProceedingJoinPoint joinPoint) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		SysLog obj = new SysLog();
		try {
			obj = getOsAndBrowserInfo(request);
			obj.setOperateTime(DateUtil.getCurrentDatetime());

			// 请求的IP
			String ip = IPUtil.getIpFromRequest(request);
			obj.setIp(ip);

			SysUser seSysUser = SecuritySessionUtil.init().getAttribute(AuthConst.SESSION_USER);
			if (seSysUser != null) {
				//todo query from redis
				seSysUser = sysUserService.getById(seSysUser.getId());
			}
			if (seSysUser != null) {
				obj.setOperator(seSysUser.getUserAccount());
			}

			getInfoRequestParameter(request, obj);
		} catch (Exception e) {
			log.error("异常信息:{}", e);
		}

		try {
			obj.setDescprition(getMethodDescription(joinPoint));
		} catch (Exception e) {
			log.error("异常信息:{}", e);
		}
		// 添加记录
		try {
			sysLogService.save(obj);
		} catch (Exception e) {
			log.error("异常信息:{}", e);
		}

		Object retVal = null;
		try {
			retVal = joinPoint.proceed();// 执行该方法
		} catch (Exception e) {
			log.error("异常信息:{}", e);
		} catch (Throwable e) {
			log.error("异常信息:{}", e);
		}
		return retVal;
	}

	/**
	 * 获取注解中对方法的描述信息
	 *
	 * @param joinPoint 切点
	 * @return 方法描述
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String getMethodDescription(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		String description = "";
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					description = method.getAnnotation(SystemLog.class).description();
					break;
				}
			}
		}
		return description;
	}

	/**
	 * 获取参数
	 * 
	 * @param request
	 * @param _obj
	 */
	private void getInfoRequestParameter(HttpServletRequest request, SysLog _obj) {
		Map<String, String[]> paramMap = request.getParameterMap();
		if (paramMap != null && !paramMap.isEmpty()) {
			StringBuffer queryString = new StringBuffer();
			for (String key : paramMap.keySet()) {
				String[] values = paramMap.get(key);
				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					if (value != null && value.length() > 128) {
						queryString.append(key).append("=").append(value.substring(0, 128)).append("&");
					} else {
						queryString.append(key).append("=").append(value).append("&");
					}
				}
				if (key.equals("city_id")) {
					String cityId = paramMap.get(key)[0];
					_obj.setCityId(cityId);
					_obj.setCitycn(ConfigPropertiesUtil.getCity(cityId).getCityCn());
				}
				if (key.equals("org_id")) {
					_obj.setOrgId(Integer.parseInt(paramMap.get(key)[0]));
				}
			}
			// 去掉最后一个空格
			queryString.deleteCharAt(queryString.length() - 1);
			_obj.setUrl(request.getRequestURL().toString());
			_obj.setParams(queryString.toString());
		}
	}

	/**
	 * 获取当前请求的操作系统和浏览器
	 * 
	 * @param request
	 * @return
	 */
	public static SysLog getOsAndBrowserInfo(HttpServletRequest request) {
		String browserDetails = request.getHeader("User-Agent");
		String userAgent = browserDetails;
		String user = userAgent.toLowerCase();

		String os = "";
		String browser = "";

		// =================OS Info=======================
		if (userAgent.toLowerCase().indexOf("windows") >= 0) {
			os = "Windows";
		} else if (userAgent.toLowerCase().indexOf("mac") >= 0) {
			os = "Mac";
		} else if (userAgent.toLowerCase().indexOf("x11") >= 0) {
			os = "Unix";
		} else if (userAgent.toLowerCase().indexOf("android") >= 0) {
			os = "Android";
		} else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
			os = "IPhone";
		} else {
			os = "UnKnown(More-Info:" + userAgent + ")";
		}
		// ===============Browser===========================
		if (user.contains("edge")) {
			browser = (userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0]).replace("/", "-");
		} else if (user.contains("msie")) {
			String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
			browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
		} else if (user.contains("safari") && user.contains("version")) {
			browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-"
					+ (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
		} else if (user.contains("opr") || user.contains("opera")) {
			if (user.contains("opera")) {
				browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-"
						+ (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
			} else if (user.contains("opr")) {
				browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
			}
		} else if (user.contains("chrome")) {
			browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
		} else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf("mozilla/4.7") != -1)
				|| (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
			browser = "Netscape-?";
		} else if (user.contains("firefox")) {
			browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
		} else if (user.contains("rv")) {
			String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
			browser = "IE" + IEVersion.substring(0, IEVersion.length() - 1);
		} else {
			browser = "UnKnown(More-Info:" + userAgent + ")";
		}

		SysLog sysLog = new SysLog();
		sysLog.setOs(os);
		sysLog.setBrowser(browser);
		return sysLog;
	}
}
