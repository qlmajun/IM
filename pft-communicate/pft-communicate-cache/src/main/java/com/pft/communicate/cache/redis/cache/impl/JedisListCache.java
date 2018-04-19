package com.pft.communicate.cache.redis.cache.impl;

import java.io.Serializable;
import java.util.List;

import com.pft.communicate.cache.ListCache;
import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.cache.JedisCacheImpl;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

import redis.clients.jedis.BinaryClient.LIST_POSITION;

public class JedisListCache<T extends Serializable> extends JedisCacheImpl implements ListCache<T> {

	public JedisListCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory) {
		this(namePrefix, cacheName, connectionFactory, null);
		reinit(cacheName);
	}

	public JedisListCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory,
			final ReusableObjectPool<JedisListCache<T>> objectPool) {
		super(namePrefix, cacheName, connectionFactory, objectPool);
	}

	@Override
	public long rpush(@SuppressWarnings("unchecked") final T... vals) {
		if (vals == null || vals.length == 0) {
			return 0;
		}
		try {
			return functionHandlerWithOp(2, jedis -> jedis.rpush(getCacheFullNameBytes(), arrayToBytes(vals)), RedisOperation.rpush);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long lpush(@SuppressWarnings("unchecked") final T... vals) {
		if (vals == null || vals.length == 0) {
			return 0;
		}
		try {
			return functionHandlerWithOp(2, jedis -> jedis.lpush(getCacheFullNameBytes(), arrayToBytes(vals)), RedisOperation.lpush);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean lset(long index, T value) {
		try {
			functionHandlerWithOp(2, jedis -> jedis.lset(getCacheFullNameBytes(), index, objectToBytes(value)), RedisOperation.lset);
			return true;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long lrem(long count, T value) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.lrem(getCacheFullNameBytes(), count, objectToBytes(value)), RedisOperation.lrem);
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T lpop() {
		try {
			return (T) toObject(functionHandlerWithOp(2, jedis -> jedis.lpop(getCacheFullNameBytes()), RedisOperation.lpop));
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T rpop() {
		try {
			return (T) toObject(functionHandlerWithOp(2, jedis -> jedis.rpop(getCacheFullNameBytes()), RedisOperation.rpop));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long size() {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.llen(getCacheFullNameBytes()), RedisOperation.llen);
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T lIndex(long index) {
		try {
			return (T) toObject(functionHandlerWithOp(2, jedis -> jedis.lindex(getCacheFullNameBytes(), index), RedisOperation.lindex));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public List<T> lrange(long start, long end) {
		try {
			List<byte[]> lrangeResult = functionHandlerWithOp(2, jedis -> jedis.lrange(getCacheFullNameBytes(), start, end),
					RedisOperation.lrange);
			List<T> list = toObjectsList(lrangeResult);
			return list;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.linsert(getCacheFullNameBytes(), where, pivot, value), RedisOperation.linsert);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public T rpoplpush(String destCacheName) {
		try {
			return toObject(functionHandlerWithOp(2,
					jedis -> jedis.rpoplpush(getCacheFullNameBytes(), destCacheName.getBytes(JedisListCache.charset)),
					RedisOperation.rpoplpush));
		} finally {
			this.autoRecycle();
		}
	}

}
