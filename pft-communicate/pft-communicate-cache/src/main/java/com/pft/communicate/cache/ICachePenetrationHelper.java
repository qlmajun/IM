package com.pft.communicate.cache;

/**
 * 解决缓存穿透问题的辅助缓存类
 * 
 * @author majun@12301.cc
 */
public interface ICachePenetrationHelper {

	/**
	 * 标记元素相应数据在数据库中为空
	 * 
	 * @param members
	 */
	public void markNone(String... members);

	/**
	 * 解除标记元素
	 * 
	 * @param members
	 */
	public void unMark(String... members);

	/**
	 * 判断元素是否被标记
	 * 
	 * @param member
	 * @return
	 */
	public boolean isMarked(String member);

}
