package com.pft.communicate.cache;

/**
 * 缓存接口, 缓存的名称的组成为: <b>fullName = prefix+"::"+cachename+"::"+itemName</b>
 *
 * @author majun@12301.cc
 *
 */
public interface Cache {

	public static final String SPLIT_PREFIX = "::";
	public static final String SPLIT_NAME = ":";

	/**
	 * 返回缓存的统一前缀
	 *
	 * @return 缓存的统一前缀
	 */
	String getNamePrefix();

	/**
	 * 返回缓存的类别
	 *
	 * @return 缓存的类别名称
	 */
	String getCacheCategoryName();

	/**
	 * 返回缓存的条目名称
	 *
	 * @return 缓存的条目名称
	 */
	String getCacheItemName();

	/**
	 * 返回生成的缓存的名称, 缓存的名称的组成为: <b>fullName =
	 * prefix+"::"+cachename+"::"+itemName</b>
	 *
	 * @return 生成的缓存的名称
	 */
	String getCacheFullName();

	/**
	 * 删除整个缓存
	 *
	 * @return 如果删除的缓存个数
	 */
	int removeCache();

	/**
	 * 判断缓存是否存在
	 *
	 * @return false 如果不存在
	 */
	abstract boolean exists();

}
