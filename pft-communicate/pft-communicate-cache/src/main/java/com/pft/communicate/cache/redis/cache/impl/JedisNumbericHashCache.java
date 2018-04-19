package com.pft.communicate.cache.redis.cache.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pft.communicate.cache.NumbericHashCache;
import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

public class JedisNumbericHashCache extends JedisHashCache<String, Long> implements NumbericHashCache {

	public JedisNumbericHashCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory) {
		this(namePrefix, cacheName, connectionFactory, null);
		reinit(cacheName);
	}

	public JedisNumbericHashCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory,
			ReusableObjectPool<JedisHashCache<String, Long>> objectPool) {
		super(namePrefix, cacheName, connectionFactory, objectPool);
	}

	@Override
	public boolean exists(final String key) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.hexists(getCacheFullName(), key),RedisOperation.hexists);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long getNumber(final String key) {
		try {
			String hval = functionHandlerWithOp(2, jedis -> jedis.hget(getCacheFullName(), key),RedisOperation.hget);
			if (hval == null) {
				return -1L;
			}
			return Long.parseLong(hval);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long setNumber(String key, long number) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.hset(getCacheFullName(), key, String.valueOf(number)),RedisOperation.hset);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long increase(final String key) {
		return increase(key, 1);
	}

	@Override
	public long increase(final String key, final long increase) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.hincrBy(getCacheFullName(), key, increase),RedisOperation.hincrBy);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<String> getKeys() {
		return super.getKeys();
	}

	@Override
	public Map<String, Long> getNumbers(Collection<String> keys) {
		if (keys == null || keys.size() == 0) {
			return Collections.emptyMap();
		}
		try {
			String[] keyArray = keys.toArray(new String[] {});
			List<byte[]> byteList = functionHandlerWithOp(2, jedis -> jedis.hmget(getCacheFullNameBytes(), arrayToBytes(keyArray)),RedisOperation.hmget);
			List<String> valueList = toObjectsList(byteList);
			Map<String, Long> map = new HashMap<String, Long>(keys.size());
			for (int i = 0; i < keyArray.length; i++) {
				String value = valueList.get(i);
				if (value != null) {
					map.put(keyArray[i], Long.parseLong(value));
				} else {
					map.put(keyArray[i], 0L);
				}
			}
			return map;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean putNumbers(Map<String, Long> map) {
		if (map == null || map.size() == 0) {
			return false;
		}
		try {
			Map<String, String> numberMap = new HashMap<String, String>(map.size());
			map.forEach((k, v) -> {
				numberMap.put(k, String.valueOf(v));
			});
			return functionHandlerWithOp(2, jedis -> jedis.hmset(getCacheFullNameBytes(), mapToBytes(numberMap)),RedisOperation.hmset).equals(OK);
		} finally {
			this.autoRecycle();
		}
	}

}
