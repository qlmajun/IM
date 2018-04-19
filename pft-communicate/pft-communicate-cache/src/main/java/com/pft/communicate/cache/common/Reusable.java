package com.pft.communicate.cache.common;

/**
 * 可以重新初始化的对象
 *
 * @author majun@12301.cc
 *
 */
public interface Reusable {

	public void reinit(String id);

	public void recyle();

}
