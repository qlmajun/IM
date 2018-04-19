package com.pft.communicate.cache.redis.cache.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.pft.communicate.cache.SetCache;
import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.cache.JedisCacheImpl;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

public class JedisSetCache<T extends Serializable> extends JedisCacheImpl implements SetCache<T> {

	public JedisSetCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory) {
		this(namePrefix, cacheName, connectionFactory, null);
		reinit(cacheName);
	}

	public JedisSetCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory,
			ReusableObjectPool<JedisSetCache<T>> objectPool) {
		super(namePrefix, cacheName, connectionFactory, objectPool);
	}

	@Override
	public long addToSet(@SuppressWarnings("unchecked") T... members) {
		if (members == null || members.length == 0) {
			return 0;
		}
		try {
			return functionHandlerWithOp(2, jedis -> jedis.sadd(getCacheFullNameBytes(), arrayToBytes(members)), RedisOperation.sadd);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<T> getMembers() {
		try {
			return toObjectsSet(functionHandlerWithOp(2, jedis -> jedis.smembers(getCacheFullNameBytes()), RedisOperation.smembers));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long removeFromSet(@SuppressWarnings("unchecked") T... members) {
		if (members == null || members.length == 0) {
			return 0;
		}
		try {
			return functionHandlerWithOp(2, jedis -> jedis.srem(getCacheFullNameBytes(), arrayToBytes(members)), RedisOperation.srem);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long size() {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.scard(getCacheFullNameBytes()), RedisOperation.scard);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean isMember(T member) {
		return exists(member);
	}

	@Override
	public boolean exists(T member) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.sismember(getCacheFullNameBytes(), objectToBytes(member)),
					RedisOperation.sismember);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public T getRandomMember() {
		try {
			return toObject(functionHandlerWithOp(2, jedis -> jedis.srandmember(getCacheFullNameBytes()), RedisOperation.srandmember));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public List<T> getRandomMembers(int size) {
		try {
			return toObjectsList(
					functionHandlerWithOp(2, jedis -> jedis.srandmember(getCacheFullNameBytes(), size), RedisOperation.srandmembers));
		} finally {
			this.autoRecycle();
		}
	}

}
