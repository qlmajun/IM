package com.pft.communicate.core.exception;

/****
 * 自定义模块未找到异常
 * 
 * @author majun@12301.cc
 *
 */
public class ModuleNotFoundException extends PftCommunicationException {

	private Class<?> moduleClass;

	public ModuleNotFoundException(Class<?> moduleClass) {
		super("module " + moduleClass.getName() + " not found");
		this.moduleClass = moduleClass;
	}

	private static final long serialVersionUID = -2422535032785661168L;

	@Override
	public int getCode() {
		return 50001;
	}

	public Class<?> getModuleClass() {
		return moduleClass;
	}

}
