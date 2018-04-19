package com.pft.communicate.cache;

import java.util.List;
import java.util.Set;

/****
 * Set类型缓存操作接口声明
 * 
 * @author majun@12301.cc
 *
 * @param <T>
 */
public interface SetCache<T> extends Cache {

	long addToSet(@SuppressWarnings("unchecked") final T... members);

	Set<T> getMembers();

	long removeFromSet(@SuppressWarnings("unchecked") final T... members);

	long size();

	boolean isMember(final T member);

	/**
	 * @deprecated {@code SetCache.isMember(T)}
	 * @param member
	 * @return
	 */
	@Deprecated
	boolean exists(final T member);

	T getRandomMember();

	List<T> getRandomMembers(int size);

}
