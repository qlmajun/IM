package com.pft.communicate.core.cache;

import org.apache.commons.configuration2.ImmutableConfiguration;

import com.pft.communicate.cache.redis.cache.JedisCacheFactory;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;
import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.annotation.ModuleConfig;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communicate.core.starter.SimpleStarter;

import redis.clients.jedis.JedisPoolConfig;

/****
 * redis操作管理类
 * 
 * @author majun@12301.cc
 *
 */
@ModuleConfig(startupPriority = 1)
public class RedisCacheManager extends Module {

	private JedisConnectionFactory jedisConnectionFactory;

	private JedisCacheFactory jedisCacheFactory;

	/** 非常重要, 请勿随意修改, 可能导致所有缓存失效 **/
	private static final String CACHE_PREFIX = "cache_pft";

	public RedisCacheManager() {
		this(SimpleStarter.getStarter().getConfiguration());
	}

	public RedisCacheManager(ImmutableConfiguration configuration) {
		super();

		String hostname = configuration.getString("cache.redis.host", "127.0.0.1");
		int port = configuration.getInt("cache.redis.port", 6379);
		String password = configuration.getString("cache.redis.password");

		String cacheNamePrefix = configuration.getString("cache.name.prefix", CACHE_PREFIX);
		int timeout = configuration.getInt("cache.redis.timeout.ms", 30000);

		int maxTotal = configuration.getInt("cache.redis.pool.maxTotal", 2000);
		int maxIdle = configuration.getInt("cache.redis.pool.maxIdle", 20);
		int timeBetweenEvictionRunsMillis = configuration.getInt("cache.redis.pool.timeBetweenEvictionRunsMillis", 30000);
		int minEvictableIdleTimeMillis = configuration.getInt("cache.redis.pool.minEvictableIdleTimeMillis", 30000);
		boolean testOnBorrowtestOnBorrow = configuration.getBoolean("cache.redis.pool.testOnBorrow", false);

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		config.setTestOnBorrow(testOnBorrowtestOnBorrow);

		jedisConnectionFactory = new JedisConnectionFactory(hostname, port, password, timeout, config);
		jedisCacheFactory = new JedisCacheFactory(cacheNamePrefix, jedisConnectionFactory);
	}

	@Override
	protected void doInit() throws LifeCycleException {
	}

	public JedisConnectionFactory getJedisConnectionFactory() {
		return jedisConnectionFactory;
	}

	public JedisCacheFactory getCacheFactory() {
		return jedisCacheFactory;
	}

	@Override
	protected void doStart() throws LifeCycleException {

	}

	@Override
	protected void doStop() throws LifeCycleException {

	}

}
