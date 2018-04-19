package com.pft.communicate.cache.redis.connection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis连接工厂
 *
 * @author majun@12301.cc
 *
 */
public class JedisConnectionFactory {

	private String hostname;
	private int port;

	/** redis密码，没有密码为null或者空字符串 **/
	private String password;

	/** 连接超时时间,默认1000ms **/
	private int timeout;

	private JedisPoolConfig config;

	/** jedis连接池 **/
	private JedisPool pool;

	public JedisConnectionFactory(String hostname, int port, String password) {
		this(hostname, port, password, 3000, null);
	}

	public JedisConnectionFactory(String hostname, int port, String password, int timeout, JedisPoolConfig config) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.password = password;
		this.config = config;
		this.timeout = timeout;
		init();
	}

	/**
	 * 获取Jedis连接, 请使用jedis.close()关闭连接
	 *
	 * @return
	 */
	public Jedis getResource() {
		return pool.getResource();
	}

	private void init() {
		if (config == null) {
			config = defaultConfig();
		}

		if (password == null || password.length() == 0) {
			pool = new JedisPool(config, hostname, port);
			return;
		}
		pool = new JedisPool(config, hostname, port, timeout, password);
	}

	private JedisPoolConfig defaultConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(4000);
		config.setMaxIdle(200);
		config.setMaxWaitMillis(100);
		config.setTimeBetweenEvictionRunsMillis(30000);
		config.setMinEvictableIdleTimeMillis(60000);
		config.setTestOnBorrow(false);
		config.setTestWhileIdle(true);

		return config;
	}

}
