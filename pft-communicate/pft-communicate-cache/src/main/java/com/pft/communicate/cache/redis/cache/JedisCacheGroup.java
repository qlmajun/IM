package com.pft.communicate.cache.redis.cache;

import java.lang.reflect.Constructor;

import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

public class JedisCacheGroup<T extends JedisCacheImpl> extends ReusableObjectPool<T> {

	private String namePrefix;

	private String cacheCateforyName;

	private JedisConnectionFactory jedisConnectionFactory;

	private Class<T> cacheClazz;

	@SuppressWarnings("unchecked")
	public JedisCacheGroup(final String namePrefix, final Class<? extends JedisCacheImpl> cacheClazz, final String cacheName,
			final JedisConnectionFactory jedisConnectionFactory, int maxTotal, int maxIdle, int minIdle) {
		super(maxTotal, maxIdle, minIdle);
		this.namePrefix = namePrefix;
		this.cacheClazz = (Class<T>) cacheClazz;
		this.cacheCateforyName = cacheName;
		this.jedisConnectionFactory = jedisConnectionFactory;
	}

	public JedisCacheGroup(final String namePrefix, final Class<? extends JedisCacheImpl> cacheClazz, final String cacheName,
			final JedisConnectionFactory jedisConnectionFactory) {
		this(namePrefix, cacheClazz, cacheName, jedisConnectionFactory, ReusableObjectPool.OBJECT_POOL_MAX_TOTAL,
				ReusableObjectPool.OBJECT_POOL_MAX_IDLE, ReusableObjectPool.OBJECT_POOL_MIN_IDLE);

	}

	public JedisCacheGroup(final JedisCacheFactory cacheFactory, final String cacheName, final Class<? extends JedisCacheImpl> cacheClazz) {
		this(cacheFactory.getCacheNamePrefix(), cacheClazz, cacheName, cacheFactory.getJedisConnectionFactory());
		cacheFactory.addCache(this);
	}

	public T getCacheItem(String name) {
		return getCacheItem(name, true);
	}

	/**
	 * added by zongtf3@yonyou.com <2016年4月25日>
	 * 获取cache，指定回收策略
	 */
	public T getCacheItem(String name, boolean autoRecycle) {

		T jedisCache = getObject(name);
		jedisCache.setAutoRecycle(autoRecycle);

		return jedisCache;
	}

	public String getCacheCateforyName() {
		return cacheCateforyName;
	}

	@Override
	public T create() throws Exception {
		Constructor<T> constructor = cacheClazz.getConstructor(String.class, String.class, JedisConnectionFactory.class, ReusableObjectPool.class);
		return constructor.newInstance(JedisCacheGroup.this.namePrefix, JedisCacheGroup.this.cacheCateforyName,
				JedisCacheGroup.this.jedisConnectionFactory, this);
	}

}
