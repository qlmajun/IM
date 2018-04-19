package com.pft.communicate.cache;

/***
 * 字符串缓存操作接口
 *
 * @author majun5@yonyou.com
 *
 */

public interface StringCache<T> extends Cache {

	/***
	 * 根据key获取字符串
	 *
	 * @param key
	 * @return
	 */
	T get();

	/***
	 * 设置字符串缓存
	 *
	 * @param key
	 * @param value
	 */
	void set(T value);

	/***
	 * 设置key的缓存值，添加key的过期时间
	 *
	 * @param key
	 * @param value
	 * @param expireSecond
	 */
	boolean setExpire(T value, int expireSecond);

	/***
	 * 将key值计数增加1
	 *
	 * @param key
	 * @return 返回增加成功后的值
	 */
	long incr();

	/***
	 * 将 key 所储存的值加上增量 increment
	 *
	 * @param key
	 * @param increment
	 * @return 返回增加成功后的值
	 */
	long incrby(int increment);

	/**
	 * 删除给定的一个或多个 key
	 *
	 * @param key
	 */
	void del();

}
