package com.pft.communicate.core.support.clazz;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImplementationClassFinder {

	private static final Logger logger = LoggerFactory.getLogger(ImplementationClassFinder.class);

	private static final char SPRIT = '/';
	private static final char DOT = '.';
	private static final String CLASS = ".class";
	private static final String FILE = "file";
	private static final String JAR = "jar";
	private static final String CHARACTER = "UTF-8";

	public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> interfaceClass) {
		return getAllClassByInterface(interfaceClass, false);
	}

	/**
	 * 取得某个接口下所有实现这个接口的类
	 */
	public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> interfaceClass,
			boolean containsInterfaceOrAbstract) {
		return getAllClassByInterface(interfaceClass, interfaceClass, containsInterfaceOrAbstract);
	}

	@SuppressWarnings({ "rawtypes" })
	public static <T> List<Class<? extends T>> getAllClassByInterface(Class basePackageClass, Class<T> interfaceClass,
			boolean containsInterfaceOrAbstract) {

		String packageName = basePackageClass.getPackage().getName();
		return getAllClassByInterface(packageName, interfaceClass, containsInterfaceOrAbstract);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<Class<? extends T>> getAllClassByInterface(String basepackage, Class<T> interfaceClass,
			boolean containsInterfaceOrAbstract) {

		// 获取当前包下以及子包下所以的类
		List<Class<?>> allClass = null;
		try {
			allClass = getClasses(basepackage);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException when get class by package name {} ", basepackage, e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("IOException when get class by package name {} ", basepackage, e);
			throw new RuntimeException(e);
		}

		if (allClass == null) {
			return null;
		}

		List<Class<? extends T>> returnClassList = new ArrayList<Class<? extends T>>();
		for (Class classes : allClass) {
			if (!containsInterfaceOrAbstract
					&& (classes.isInterface() || Modifier.isAbstract(classes.getModifiers()))) {
				continue;
			}
			// 判断是否是同一个接口
			if (interfaceClass.isAssignableFrom(classes)) {
				// 本身不加入进去
				if (!interfaceClass.equals(classes)) {
					returnClassList.add(classes);
				}
			}
		}
		return returnClassList;
	}

	/**
	 * 从包package中获取所有的Class
	 *
	 * @param pack
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
		// 第一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageDirName = packageName.replace(DOT, SPRIT);
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			// 得到协议的名称
			String protocol = url.getProtocol();
			// 如果是以文件的形式保存在服务器上
			if (FILE.equals(protocol)) {
				// 获取包的物理路径
				String filePath = URLDecoder.decode(url.getFile(), CHARACTER);
				// 以文件的方式扫描整个包下的文件 并添加到集合中
				findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
			}

			if (JAR.equals(protocol)) {
				dealJARProtocol(packageDirName, packageName, url, recursive, classes);
			}
		}

		return classes;
	}

	/***
	 * 处理协议名称为jar 获取classe集合
	 *
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static void dealJARProtocol(String packageDirName, String packageName, URL url, boolean recursive,
			List<Class<?>> classes) throws ClassNotFoundException, IOException {

		JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

		Enumeration<JarEntry> entries = jar.entries();

		while (entries.hasMoreElements()) {
			// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			// 如果是以/开头的
			if (name.charAt(0) == SPRIT) {
				name = name.substring(1);
			}
			// 如果前半部分和定义的包名不相同
			if (!name.startsWith(packageDirName)) {
				return;
			}
			int idx = name.lastIndexOf(SPRIT);
			// 如果以"/"结尾 是一个包
			if (idx != -1) {
				// 获取包名 把"/"替换成"."
				packageName = name.substring(0, idx).replace(SPRIT, DOT);
			}
			// 如果不可以迭代下去
			if ((idx == -1) && !recursive) {
				return;
			}
			// 如果是一个.class文件 而且不是目录
			if (name.endsWith(CLASS) && !entry.isDirectory()) {
				// 去掉后面的".class" 获取真正的类名
				String className = name.substring(packageName.length() + 1, name.length() - 6);
				// 添加到classes
				classes.add(Class.forName(packageName + DOT + className));
			}
		}
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 *
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 * @throws ClassNotFoundException
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			final boolean recursive, List<Class<?>> classes) throws ClassNotFoundException {

		File dir = new File(packagePath);

		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		File[] dirsOrClassFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(CLASS));
			}
		});

		for (File file : dirsOrClassFiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + DOT + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				classes.add(Class.forName(packageName + DOT + className));
			}
		}
	}
}
