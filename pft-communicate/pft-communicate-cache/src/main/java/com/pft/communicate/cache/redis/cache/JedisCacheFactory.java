package com.pft.communicate.cache.redis.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.exception.DuplicateCacheDeclareException;
import com.pft.communicate.cache.redis.cache.impl.JedisHashCache;
import com.pft.communicate.cache.redis.cache.impl.JedisListCache;
import com.pft.communicate.cache.redis.cache.impl.JedisNumbericHashCache;
import com.pft.communicate.cache.redis.cache.impl.JedisSetCache;
import com.pft.communicate.cache.redis.cache.impl.JedisSortedSetCache;
import com.pft.communicate.cache.redis.cache.impl.JedisStringCache;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

public class JedisCacheFactory {

	private static final Logger logger = LoggerFactory.getLogger(JedisCacheFactory.class);

	private String namePrefix;

	private Map<String, JedisCacheGroup<? extends JedisCacheImpl>> cacheGroups = new ConcurrentHashMap<String, JedisCacheGroup<? extends JedisCacheImpl>>();

	private JedisConnectionFactory jedisConnectionFactory;

	public JedisCacheFactory(final String prefix, final JedisConnectionFactory jedisConnectionFactory) {
		super();
		if (prefix == null || prefix.trim().length() == 0) {
			throw new IllegalArgumentException("cache name prefix can not be empty");
		} else {
			this.namePrefix = prefix;
		}
		this.jedisConnectionFactory = jedisConnectionFactory;
	}

	public void addCache(JedisCacheGroup<? extends JedisCacheImpl> cacheGroup) {
		String cacheCateforyName = cacheGroup.getCacheCateforyName();
		JedisCacheGroup<? extends JedisCacheImpl> jedisCacheGroup = cacheGroups.get(cacheCateforyName);
		if (jedisCacheGroup != null) {
			if (!jedisCacheGroup.equals(jedisCacheGroup)) {
				throw new DuplicateCacheDeclareException("duplicate cache decalared, data inconsistency may occur");
			}
			return;
		}
		cacheGroups.put(cacheCateforyName, cacheGroup);
	}

	@SuppressWarnings("unchecked")
	public <T extends JedisCacheImpl> JedisCacheGroup<? extends JedisCacheImpl> createCache(final String cacheName, final Class<T> cacheClass, int maxTotal, int maxIdle, int minIdle) {

		synchronized (cacheName.intern()) {
			JedisCacheGroup<T> cache = (JedisCacheGroup<T>) cacheGroups.get(cacheName);
			if (cache != null) {
				return cache;
			}
			try {
				cache = new JedisCacheGroup<T>(namePrefix, cacheClass, cacheName, jedisConnectionFactory, maxTotal, maxIdle, minIdle);
				cacheGroups.put(cacheName, cache);
			} catch (Exception e) {
				logger.error("{} when create cache with name {}", e.getClass().getSimpleName(), cacheName, e);
			}
			return cache;
		}
	}

	public <T extends JedisCacheImpl> JedisCacheGroup<? extends JedisCacheImpl> createCache(final String cacheName, final Class<T> cacheClass) {
		return createCache(cacheName, cacheClass, ReusableObjectPool.OBJECT_POOL_MAX_TOTAL, ReusableObjectPool.OBJECT_POOL_MAX_IDLE, ReusableObjectPool.OBJECT_POOL_MIN_IDLE);
	}

	@SuppressWarnings("unchecked")
	public <K extends Serializable, V extends Serializable> JedisCacheGroup<JedisHashCache<K, V>> createHashCache(final String cacheName) {
		return (JedisCacheGroup<JedisHashCache<K, V>>) createCache(cacheName, JedisHashCache.class);
	}

	public <K extends Serializable, V extends Serializable> JedisHashCache<K, V> createSimpleHashCache(final String cacheName) {
		return new JedisHashCache<>(cacheName, cacheName, jedisConnectionFactory);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> JedisCacheGroup<JedisListCache<T>> createListCache(final String cacheName) {
		return (JedisCacheGroup<JedisListCache<T>>) createCache(cacheName, JedisListCache.class);
	}

	@SuppressWarnings("unchecked")
	public JedisCacheGroup<JedisNumbericHashCache> createNumbericHashCache(final String cacheName) {
		return (JedisCacheGroup<JedisNumbericHashCache>) createCache(cacheName, JedisNumbericHashCache.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> JedisCacheGroup<JedisSetCache<T>> createSetCache(final String cacheName) {
		return (JedisCacheGroup<JedisSetCache<T>>) createCache(cacheName, JedisSetCache.class);
	}

	public <T extends Serializable> JedisSetCache<T> createSimpleSetCache(final String cacheName) {
		return new JedisSetCache<T>(namePrefix, cacheName, jedisConnectionFactory);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> JedisCacheGroup<JedisSortedSetCache<T>> createSortedSetCache(final String cacheName) {
		return (JedisCacheGroup<JedisSortedSetCache<T>>) createCache(cacheName, JedisSortedSetCache.class);
	}

	public <T extends Serializable> JedisSortedSetCache<T> createSimpleSortedSetCache(final String cacheName) {
		return new JedisSortedSetCache<T>(namePrefix, cacheName, jedisConnectionFactory);
	}

	@SuppressWarnings("unchecked")
	public <T extends Serializable> JedisCacheGroup<JedisStringCache<T>> createStringCache(final String cacheName) {
		return (JedisCacheGroup<JedisStringCache<T>>) createCache(cacheName, JedisStringCache.class);
	}

	public <T extends Serializable> JedisStringCache<T> createSimpleStringCache(final String cacheName) {
		return new JedisStringCache<T>(namePrefix, cacheName, jedisConnectionFactory);
	}

	public String getCacheNamePrefix() {
		return namePrefix;
	}

	public JedisConnectionFactory getJedisConnectionFactory() {
		return jedisConnectionFactory;
	}
}
