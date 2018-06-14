package com.chelaile.auth.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AskTimeHandlerInterceptor extends HandlerInterceptorAdapter {

	protected Logger log = LogManager.getLogger(AskTimeHandlerInterceptor.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("StopWatch-StartTime");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long beginTime = System.currentTimeMillis();// 1、开始时间
		startTimeThreadLocal.set(beginTime);// 线程绑定变量（该数据只有当前请求的线程可见）
		return true;// 继续流程
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		long endTime = System.currentTimeMillis();// 2、结束时间
		long beginTime = startTimeThreadLocal.get();// 得到线程绑定的局部变量（开始时间）
		long consumeTime = endTime - beginTime;// 3、消耗的时间
		if (consumeTime > 15 * 1000) {// 此处认为处理时间超过15*1000毫秒的请求为慢请求
			StringBuffer logStr = new StringBuffer(sdf.format(new Date()));
			logStr.append("\t" + "请求耗时:" + consumeTime / 1000 + "s");
			logStr.append("\t" + getInfoRequestParameter(request));
			log.info(logStr.toString());
		}
	}

	private String getInfoRequestParameter(HttpServletRequest request) {
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
			}
			// 去掉最后一个空格
			queryString.deleteCharAt(queryString.length() - 1);
			return request.getRequestURL() + "?" + queryString;
		}
		return request.getRequestURL().toString();
	}
}