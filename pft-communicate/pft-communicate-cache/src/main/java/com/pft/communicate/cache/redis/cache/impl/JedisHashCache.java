package com.pft.communicate.cache.redis.cache.impl;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pft.communicate.cache.HashCache;
import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.cache.JedisCacheImpl;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;
import com.pft.communicate.cache.redis.lock.JedisLock;

import redis.clients.jedis.Jedis;

public class JedisHashCache<K extends Serializable, V extends Serializable> extends JedisCacheImpl implements HashCache<K, V> {

	public JedisHashCache(final String namePrefix, final String cacheName, final JedisConnectionFactory connectionFactory) {
		this(namePrefix, cacheName, connectionFactory, null);
		reinit(cacheName);
	}

	public JedisHashCache(final String namePrefix, final String cacheName, final JedisConnectionFactory connectionFactory,
			final ReusableObjectPool<JedisHashCache<K, V>> objectPool) {
		super(namePrefix, cacheName, connectionFactory, objectPool);
	}

	@Override
	public String setItemName(final String itemName) {
		String cacheFullName = super.setItemName(itemName);
		cacheFullNameBytes = getCacheFullName().getBytes(charset);
		return cacheFullName;
	}

	public JedisLock getLock(final String key) {
		try {
			return new JedisLock(getConnectionFactory(), getCacheFullName(), key);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean exists(final K key) {
		try {
			return functionHandlerWithOp(2, (Jedis jedis) -> jedis.hexists(getCacheFullNameBytes(), objectToBytes(key)),RedisOperation.hexists);
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		try {
			byte[] hval = functionHandlerWithOp(2, jedis -> jedis.hget(getCacheFullNameBytes(), objectToBytes(key)),RedisOperation.hget);
			return (V) toObject(hval);
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<K, V> getAll() {
		try {
			Map<byte[], byte[]> hvals = functionHandlerWithOp(2, jedis -> jedis.hgetAll(getCacheFullNameBytes()),RedisOperation.hgetall);
			Map<K, V> values = new HashMap<K, V>(hvals.size());
			hvals.forEach((k, v) -> {
				values.put((K) toObject(k), (V) toObject(v));
			});
			return values;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<K> getKeys() {
		try {
			Set<byte[]> hvals = functionHandlerWithOp(2, jedis -> jedis.hkeys(getCacheFullNameBytes()),RedisOperation.hkeys);
			return toObjectsSet(hvals);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<V> getValueSet() {
		try {
			List<byte[]> hvals = functionHandlerWithOp(2, jedis -> jedis.hvals(getCacheFullNameBytes()),RedisOperation.hvals);
			return toObjectsSet(hvals);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public List<V> getValues() {
		try {
			List<byte[]> hvals = functionHandlerWithOp(2, jedis -> jedis.hvals(getCacheFullNameBytes()),RedisOperation.hvals);
			return toObjectsList(hvals);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean put(K key, V value) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.hset(getCacheFullNameBytes(), objectToBytes(key), objectToBytes(value)),RedisOperation.hset) == 0;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean putAll(Map<K, V> map) {
		if (map == null || map.size() == 0) {
			return false;
		}
		try {
			return functionHandlerWithOp(2,jedis->jedis.hmset(getCacheFullNameBytes(), mapToBytes(map)),RedisOperation.hmset).equals(OK);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean remove(@SuppressWarnings("unchecked") final K... key) {
		try {
			return functionHandlerWithOp(2,jedis->jedis.hdel(getCacheFullNameBytes(), arrayToBytes(key)),RedisOperation.hdel)>0L;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean putNx(final K key, final V value) {
		try {
			return functionHandlerWithOp(2,jedis->jedis.hsetnx(getCacheFullNameBytes(), objectToBytes(key), objectToBytes(value)),RedisOperation.hsetnx)==1L;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long size() {
		try {
			return functionHandlerWithOp(2,jedis->jedis.hlen(getCacheFullNameBytes()),RedisOperation.hlen);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long clear() {
		return removeCache();
	}

	@Override
	public Map<K, V> getMulti(Collection<K> collect, Class<K> clazz) {
		if (collect == null || collect.size() == 0) {
			return Collections.emptyMap();
		}
		try {
			@SuppressWarnings("unchecked")
			K[] keys = collect.toArray((K[]) Array.newInstance(clazz, collect.size()));
			List<byte[]> byteList = functionHandlerWithOp(2, jedis -> jedis.hmget(getCacheFullNameBytes(), arrayToBytes(keys)),RedisOperation.hmget);
			List<V> valueList = toObjectsList(byteList);
			Map<K, V> map = new HashMap<K, V>(collect.size());
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], valueList.get(i));
			}
			return map;
		} finally {
			this.autoRecycle();
		}
	}

}
