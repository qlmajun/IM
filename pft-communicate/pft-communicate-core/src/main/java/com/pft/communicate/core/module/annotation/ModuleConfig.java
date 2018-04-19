package com.pft.communicate.core.module.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解声明模块的启动和关闭顺序, 以及是否被启用; 在某些模块启动对顺序敏感的情况下使用
 *
 * @author majun@12301.cc
 *
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleConfig {

	public static final int DEFAULT_STARTUP_PRIORITY = 100;
	public static final int DEFAULT_STOP_PRIORITY = 100;

	/**
	 * 模块启动的优先级, 数字越小越优先启动
	 *
	 * @return
	 */
	public int startupPriority() default DEFAULT_STARTUP_PRIORITY;

	/**
	 * 模块是否被禁用
	 *
	 * @return
	 */
	public boolean enable() default true;

	/**
	 * 模块关闭的优先级, 数字越小越优先启动
	 *
	 * @return
	 */
	public int stopPriority() default DEFAULT_STOP_PRIORITY;
}
