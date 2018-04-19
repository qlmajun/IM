package com.pft.communicate.cache.redis.cache;

/***
 * redis所有类型缓存操作定义
 * 
 * @author majun@12301.cc
 *
 */
public class RedisOp {
	public static enum RedisOperation {
		
		keys,
		get,
		set,
		setex,
		incr,
		incrBy,
		del,
		exists,
		
		hget,
		hgetall,
		hkeys,
		hvals,
		hset,
		hsetnx,
		hdel,
		hexists,
		hlen,
		hincrBy,
		hmget,
		hmset,
		
		sadd,
		smembers,
		srem,
		scard,
		sismember,
		srandmember,
		srandmembers,
		
		
		rpush,
		lpush,
		lset,
		lrem,
		lpop,
		rpop,
		llen,
		lindex,
		lrange,
		linsert,
		rpoplpush,
		
		zadd,
		zaddMulti,
		zcard,
		zrem,
		zcount,
		zincrby,
		zrange,
		zrangeWithScore,
		zrevrange,
		zrevrangeWithScores,
		zrangeByScore,
		zrangeByScoreWithScores,
		zremrangeByRank,
		zremrangeByScore,
		zscore,
		
		expire
	}
}
