package com.pft.communicate.cache.redis.cache.impl;

import java.io.Serializable;

import com.pft.communicate.cache.StringCache;
import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.cache.JedisCacheImpl;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;
import com.pft.communicate.cache.redis.lock.JedisLock;

/***
 * 字符串缓存操作实现
 *
 * @author majun5@yonyou.com
 *
 */
public class JedisStringCache<T extends Serializable> extends JedisCacheImpl implements StringCache<T> {

	public JedisStringCache(String namePrefix, String categoryName, JedisConnectionFactory connectionFactory) {
		this(namePrefix, categoryName, connectionFactory, null);
		reinit(categoryName);
	}

	public JedisStringCache(String namePrefix, String categoryName, JedisConnectionFactory connectionFactory,
			ReusableObjectPool<JedisStringCache<T>> objectPool) {
		super(namePrefix, categoryName, connectionFactory, objectPool);
	}

	@Override
	public T get() {
		try {
			return toObject(functionHandlerWithOp(2, jedis -> jedis.get(getCacheFullNameBytes()), RedisOperation.get));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public void set(T value) {
		try {
			functionHandlerWithOp(2, jedis -> jedis.set(getCacheFullNameBytes(), objectToBytes(value)), RedisOperation.set);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean setExpire(T value, int expireSecond) {
		try {
			return JedisLock.OK.equals(functionHandlerWithOp(2,
					jedis -> jedis.setex(getCacheFullNameBytes(), expireSecond, objectToBytes(value)), RedisOperation.setex));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long incr() {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.incr(getCacheFullNameBytes()), RedisOperation.incr);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long incrby(int increment) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.incrBy(getCacheFullNameBytes(), increment), RedisOperation.incrBy);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public void del() {
		try {
			functionHandlerWithOp(2, jedis -> jedis.del(getCacheFullNameBytes()), RedisOperation.del);
		} finally {
			this.autoRecycle();
		}
	}

}
