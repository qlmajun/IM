package com.pft.communicate.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/****
 * 自增类型缓存操作接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface NumbericHashCache extends Cache {

	/**
	 * 缓存是否存在
	 *
	 * @param key
	 * @return
	 */
	boolean exists(final String key);

	/**
	 * 获取缓存中的数据
	 *
	 * @param key
	 * @return
	 */
	long getNumber(final String key);

	/**
	 * 设置缓存数据
	 *
	 * @param key
	 * @param number
	 *            old number if exists, -1 表示不存在旧数据
	 * @return
	 */
	long setNumber(String key, long number);

	/**
	 * 计数增加1，并返回增加后数据
	 *
	 * @param key
	 * @return
	 */
	long increase(final String key);

	/**
	 * 计数增加，使用负数减少
	 *
	 * @param key
	 * @param increase
	 * @return
	 */
	long increase(final String key, final long increase);

	Map<String, Long> getNumbers(final Collection<String> keys);

	boolean putNumbers(final Map<String, Long> map);

	/**
	 * 获取所有的key值
	 * 
	 * @return
	 */
	Set<String> getKeys();

}
