package com.pft.communicate.core.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.core.exception.ModuleNotFoundException;
import com.pft.communicate.core.module.annotation.ModuleConfig;
import com.pft.communicate.core.module.lifecyle.LifeCycle.LifeCycleState;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communicate.core.support.clazz.ImplementationClassFinder;

/**
 * 模块的管理类的实现, 默认采用扫描方式, 加载所有实现{@code Module}的实现类
 *
 * @author majun@12301.cc
 *
 */
public class ModuleManagerImpl implements ModuleManager {

	private static final Logger logger = LoggerFactory.getLogger(ModuleManagerImpl.class);

	private List<Class<? extends Module>> sortModulesByStartupPriority;

	private Map<Class<? extends Module>, Module> modules = new HashMap<Class<? extends Module>, Module>();

	@SuppressWarnings("rawtypes")
	private Map<Class, Module> interfaceModuleMap = new LinkedHashMap<Class, Module>();

	private AtomicBoolean loaded = new AtomicBoolean(false);
	private AtomicBoolean started = new AtomicBoolean(false);

	private String basePackage;

	private static ModuleManagerImpl moduleManager;

	protected ModuleManagerImpl() {
		super();
		if (moduleManager != null) {
			logger.warn(
					"be careful when constructor moduleManager twice above, Method getInstance() will not behave normally.");
		}
		moduleManager = this;
	}

	/**
	 * 指定module位于哪些包下
	 *
	 * @param basePackage
	 *            扫描Module的package
	 */
	public ModuleManagerImpl(String basePackage) {
		this();
		this.basePackage = basePackage;
		if (StringUtils.isEmpty(this.basePackage)) {
			this.basePackage = this.getClass().getPackage().getName();
		}
	}

	public static ModuleManager getInstance() {
		return moduleManager;
	}

	/**
	 * 默认返回所有当前目录下所有继承Module的类
	 */
	@Override
	public List<Class<? extends Module>> getModules() {
		return ImplementationClassFinder.getAllClassByInterface(basePackage, Module.class, false);
	}

	public List<Class<? extends Module>> sortModulesByStartupPriority(List<Class<? extends Module>> moduleList) {

		moduleList.sort(new Comparator<Class<? extends Module>>() {

			@Override
			public int compare(Class<? extends Module> o1, Class<? extends Module> o2) {

				if (o1.getName().equals(o2.getName())) {
					return 0;
				}

				ModuleConfig annotation1 = o1.getAnnotation(ModuleConfig.class);
				ModuleConfig annotation2 = o2.getAnnotation(ModuleConfig.class);

				int startupPriority1 = ModuleConfig.DEFAULT_STARTUP_PRIORITY;
				int startupPriority2 = ModuleConfig.DEFAULT_STARTUP_PRIORITY;

				if (annotation1 != null) {
					startupPriority1 = annotation1.startupPriority();
				}
				if (annotation2 != null) {
					startupPriority2 = annotation2.startupPriority();
				}

				return startupPriority1 - startupPriority2;
			}
		});

		printModules("module will be start in sequence", moduleList);

		return moduleList;

	}

	private void printModules(String header, List<Class<? extends Module>> moduleList) {
		StringBuilder builder = new StringBuilder();
		for (Class<? extends Module> moduleClazz : moduleList) {
			builder.append("        ").append(moduleClazz.getName()).append("\n");
		}
		logger.info(header + "\n" + builder.toString());
	}

	public List<Class<? extends Module>> sortModulesByStopPriority(List<Class<? extends Module>> moduleList) {

		moduleList.sort(new Comparator<Class<? extends Module>>() {

			@Override
			public int compare(Class<? extends Module> o1, Class<? extends Module> o2) {

				if (o1.getName().equals(o2.getName())) {
					return 0;
				}

				ModuleConfig annotation1 = o1.getAnnotation(ModuleConfig.class);
				ModuleConfig annotation2 = o2.getAnnotation(ModuleConfig.class);

				int stopPriority1 = ModuleConfig.DEFAULT_STOP_PRIORITY;
				int stopPriority2 = ModuleConfig.DEFAULT_STOP_PRIORITY;

				if (annotation1 != null) {
					stopPriority1 = annotation1.stopPriority();
				}
				if (annotation2 != null) {
					stopPriority2 = annotation2.stopPriority();
				}

				return stopPriority1 - stopPriority2;
			}
		});
		printModules("module will be stop in sequence", moduleList);
		return moduleList;
	}

	@Override
	public <T extends Module> boolean isModuleDisabled(Class<T> moduleClass) {
		ModuleConfig annotation = moduleClass.getAnnotation(ModuleConfig.class);
		if (annotation == null || annotation.enable()) {
			return false;
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getModule(Class<T> moduleClass) {

		T module = null;

		if (Module.class.isAssignableFrom(moduleClass)) {
			module = (T) modules.get(moduleClass);
		}

		if (module == null) {
			module = (T) interfaceModuleMap.get(moduleClass);
		}

		// 使用父类Module获取子类Module
		if (module == null) {
			for (Class<?> clazz : modules.keySet()) {
				if (moduleClass.isAssignableFrom(clazz)) {
					module = (T) modules.get(clazz);
					interfaceModuleMap.put(moduleClass, (Module) module);
				}
			}
		}

		if (module == null) {
			ModuleNotFoundException moduleNotFoundException = new ModuleNotFoundException(moduleClass);
			logger.error("Attention please!!!, module " + moduleClass + " not found ", moduleNotFoundException);
			throw moduleNotFoundException;
		}

		return module;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void initModules() {

		if (loaded.get()) {
			logger.warn("repeated execution, can not load modules twice, ignore...");
			return;
		}

		sortModulesByStartupPriority = sortModulesByStartupPriority(getModules());

		// 构造模块对象
		for (Class<? extends Module> moduleClass : sortModulesByStartupPriority) {
			Module module;
			try {
				module = moduleClass.newInstance();
			} catch (IllegalAccessException e) {
				logger.error("IllegalAccessException when init module " + moduleClass.getSimpleName(), e);
				continue;
			} catch (InstantiationException e) {
				logger.error("InstantiationException when construct module object " + moduleClass.getSimpleName(), e);
				continue;
			} catch (LifeCycleException e) {
				logger.error("LifeCycleException when init module " + moduleClass.getSimpleName(), e);
				continue;
			}
			modules.put(moduleClass, module);
			Class[] interfaces = moduleClass.getInterfaces();
			if (interfaces.length > 0) {
				for (Class interf : interfaces) {
					if (moduleClass.getSimpleName().contains(interf.getSimpleName().substring(1))) {
						interfaceModuleMap.put(interf, module);
						logger.info("find interface {} for Module {}", interf.getSimpleName(),
								moduleClass.getSimpleName());
					}
				}
			}
		}
	}

	@Override
	public void loadModules() {

		for (Class<? extends Module> moduleClazz : sortModulesByStartupPriority) {
			Module module = modules.get(moduleClazz);
			try {
				loadModule(module);
			} catch (Exception e) {
				logger.error("Exception when init module " + moduleClazz.getSimpleName(), e);
			}
		}

	}

	@Override
	public boolean loadModule(Module module) {

		// 被禁用
		if (isModuleDisabled(module.getClass())) {
			logger.warn("Module {} disabled", module.getClass().getName());
			return false;
		}

		// 已经load
		if (module.lifeCycleState.ordinal() >= LifeCycleState.after_init.ordinal()) {
			return true;
		}

		module.init();
		return true;

	}

	@Override
	public void startModules() {

		if (started.get()) {
			logger.warn("repeated execution, can not start modules twice, ignore...");
			return;
		}

		for (Class<? extends Module> moduleClazz : sortModulesByStartupPriority) {
			Module module = modules.get(moduleClazz);
			if (isModuleDisabled(moduleClazz)) {
				logger.warn("Module {} disabled", module.getClass().getName());
				continue;
			}

			try {
				module.start();
			} catch (LifeCycleException e) {
				logger.error("LifeCycleException when start module " + module.getModuleName(), e);
			} catch (Exception e) {
				logger.error("Exception when start module " + module.getModuleName(), e);
			}
		}

	}

	@Override
	public void stopModules() {

		ArrayList<Class<? extends Module>> moduleClassList = new ArrayList<Class<? extends Module>>(modules.keySet());

		sortModulesByStopPriority(moduleClassList);

		for (Class<? extends Module> moduleClazz : moduleClassList) {
			Module module = modules.get(moduleClazz);
			try {
				module.stop();
			} catch (LifeCycleException e) {
				logger.error("LifeCycleException when stop module " + module.getModuleName(), e);
			} catch (Exception e) {
				logger.error("Exception when stop module " + module.getModuleName(), e);
			}
		}
		loaded.set(false);
		started.set(false);
	}

}
