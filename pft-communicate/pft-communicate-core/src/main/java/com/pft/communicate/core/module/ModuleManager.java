package com.pft.communicate.core.module;

import java.util.List;

/**
 * 模块管理类的声明
 *
 * @author majun@12301.cc
 *
 */
public interface ModuleManager {

	/**
	 * 必须实现该方法, 有序返回需要加载的模块类列表
	 *
	 * @return 需要加载的有序的模块类列表
	 */
	public List<Class<? extends Module>> getModules();

	/**
	 * 根据实现类获取模块对象
	 *
	 * @param moduleClass
	 * @return
	 */
	public <T> T getModule(Class<T> moduleClass);

	/**
	 * 初始化模块类, 创建模块对象
	 */
	public void initModules();

	/**
	 * 构造模块对象, 调用模块的init方法, 模块类的来源为
	 *
	 * @see ModuleManager#getModules()
	 */
	public void loadModules();

	public boolean loadModule(Module module);

	/**
	 * 调用模块的start方法
	 */
	public void startModules();

	/**
	 * 调用模块的stop方法
	 */
	public void stopModules();

	/**
	 * 判断模块是否被禁用
	 *
	 * @param moduleClass
	 * @return true if disabled
	 */
	public <T extends Module> boolean isModuleDisabled(Class<T> moduleClass);

}
