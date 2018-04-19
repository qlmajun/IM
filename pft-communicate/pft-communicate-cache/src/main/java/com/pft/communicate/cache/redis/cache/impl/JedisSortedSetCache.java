package com.pft.communicate.cache.redis.cache.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.pft.communicate.cache.SortedSetCache;
import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.redis.cache.JedisCacheImpl;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

import redis.clients.jedis.Tuple;

/****
 * sortedSet类型redis缓存
 *
 * @author majun5@yonyou.com
 *
 * @param <T>
 */
public class JedisSortedSetCache<T extends Serializable> extends JedisCacheImpl implements SortedSetCache<T> {

	public JedisSortedSetCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory) {
		this(namePrefix, cacheName, connectionFactory, null);
		reinit(cacheName);
	}

	public JedisSortedSetCache(String namePrefix, String cacheName, JedisConnectionFactory connectionFactory,
			ReusableObjectPool<JedisSortedSetCache<T>> objectPool) {
		super(namePrefix, cacheName, connectionFactory, objectPool);
	}

	@Override
	public long zadd(T value, double score) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zadd(getCacheFullNameBytes(), score, objectToBytes(value)), RedisOperation.zadd);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long zadd(Map<String, Double> scoreMembers) {
		if (scoreMembers == null || scoreMembers.size() == 0) {
			return 0L;
		}
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zadd(getCacheFullName(), scoreMembers), RedisOperation.zaddMulti);
		} finally {
			this.autoRecycle();
		}

	}

	@Override
	public long size() {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zcard(getCacheFullNameBytes()), RedisOperation.zcard);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long zcount(double min, double max) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zcount(getCacheFullNameBytes(), min, max), RedisOperation.zcard);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public double zincrby(double increment, T member) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zincrby(getCacheFullNameBytes(), increment, objectToBytes(member)),
					RedisOperation.zincrby);
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<T> zrange(long start, long end) {
		try {
			return (Set<T>) functionHandlerWithOp(2, jedis -> jedis.zrange(getCacheFullName(), start, end), RedisOperation.zrange);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<Tuple> zrangeWithScores(long start, long end) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zrangeWithScores(getCacheFullName(), start, end),
					RedisOperation.zrangeWithScore);
		} finally {
			this.autoRecycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<T> zrevrange(long start, long end) {
		try {
			return (Set<T>) functionHandlerWithOp(2, jedis -> jedis.zrevrange(getCacheFullName(), start, end), RedisOperation.zrevrange);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(long start, long end) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zrevrangeWithScores(getCacheFullName(), start, end),
					RedisOperation.zrevrangeWithScores);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<T> zrangeByScore(double min, double max) {
		try {
			return toObjectsSet(functionHandlerWithOp(2, jedis -> jedis.zrangeByScore(getCacheFullNameBytes(), min, max),
					RedisOperation.zrangeByScore));
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(final double min, final double max) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zrangeByScoreWithScores(getCacheFullName(), min, max),
					RedisOperation.zrangeByScoreWithScores);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long zrem(@SuppressWarnings("unchecked") T... members) {
		if (members == null || members.length == 0) {
			return 0;
		}
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zrem(getCacheFullNameBytes(), arrayToBytes(members)), RedisOperation.zrem);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long zremrangebyrank(long start, long end) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zremrangeByRank(getCacheFullNameBytes(), start, end),
					RedisOperation.zremrangeByRank);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public long zremrangebysore(double min, double max) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zremrangeByScore(getCacheFullNameBytes(), min, max),
					RedisOperation.zremrangeByScore);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public Double zscore(T member) {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.zscore(getCacheFullNameBytes(), objectToBytes(member)), RedisOperation.zscore);
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean isMember(T member) {
		return zscore(member) != null;
	}

}
