package com.pft.communicate.cache.impl;

import com.pft.communicate.cache.ICachePenetrationHelper;
import com.pft.communicate.cache.redis.cache.JedisCacheFactory;
import com.pft.communicate.cache.redis.cache.impl.JedisSetCache;

/**
 * 解决缓存穿透问题的辅助缓存类
 * 
 * @author majun@12301.cc
 */
public class CachePenetrationHelper implements ICachePenetrationHelper {

	private static final String prefix = "__none__";

	private JedisSetCache<String> notExistInDBCache;

	public CachePenetrationHelper(String cacheName, JedisCacheFactory cacheFactory) {
		notExistInDBCache = new JedisSetCache<String>(cacheFactory.getCacheNamePrefix(), prefix + cacheName,
				cacheFactory.getJedisConnectionFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yonyou.iuap.sns.stellar.common.cache.ICachePenetrationHelper#markNone
	 * (java.lang.String[])
	 */
	@Override
	public void markNone(String... members) {
		notExistInDBCache.addToSet(members);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yonyou.iuap.sns.stellar.common.cache.ICachePenetrationHelper#unMark(
	 * java.lang.String[])
	 */
	@Override
	public void unMark(String... members) {
		notExistInDBCache.removeFromSet(members);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yonyou.iuap.sns.stellar.common.cache.ICachePenetrationHelper#
	 * existInDB(java.lang.String)
	 */
	@Override
	public boolean isMarked(String member) {
		return notExistInDBCache.exists(member);
	}

}
