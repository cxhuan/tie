package com.chelaile.auth.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Spring context util 获得 Spring context 的 context
 * 
 * @author xiaoyingtong
 * 
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext context = null;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (context == null)
			context = applicationContext;
	}
	
	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath*:spring*.xml");
		}
		return context;
	}

	public static void setContext(ApplicationContext context) {
		SpringContextUtil.context = context;
	}

	public static Object getBean(String beanId) {
		return getContext().getBean(beanId);
	}

}