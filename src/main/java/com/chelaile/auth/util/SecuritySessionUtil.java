package com.chelaile.auth.util;

import com.chelaile.auth.base.LogObj;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class SecuritySessionUtil extends LogObj {

	public Session session = null;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public static SecuritySessionUtil init() {
		SecuritySessionUtil self = new SecuritySessionUtil();
		Subject currentUser = SecurityUtils.getSubject();
		self.setSession(currentUser.getSession());
		return self;
	}

	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	public void setAttribute(String name, Object value, Object defaultValue) {
		if (value == null) {
			session.setAttribute(name, defaultValue);
			return;
		}
		session.setAttribute(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T) session.getAttribute(name);
	}

	public <T> T getAttribute(String name, T defaultValue) {
		T ret = getAttribute(name);
		return ret == null ? defaultValue : ret;
	}

	public void removeAttribute(String name) {
		Object obj = getAttribute(name);
		if (obj != null) {
			session.removeAttribute(name);
		}
	}
}
