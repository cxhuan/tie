package com.chelaile.auth.interceptor;

import java.lang.annotation.*;

/**
 * 自定义注解
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {
	String description() default "";
}