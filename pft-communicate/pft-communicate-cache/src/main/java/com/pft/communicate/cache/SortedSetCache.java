package com.pft.communicate.cache;

import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Tuple;

/*****
 * sortSet类型缓存操作接口声明
 * 
 * @author majun@12301.cc
 *
 * @param <T>
 */
public interface SortedSetCache<T> extends Cache {

	/***
	 * 添加一个或多个member及sorce值
	 *
	 * @param memebers
	 * @return
	 */
	long zadd(final Map<String, Double> scoreMembers);

	long zadd(final T value, double score);

	long size();

	/***
	 * 返回sorce在min和max(包含min与max)之间的成员数量
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	long zcount(final double min, final double max);

	/***
	 * 给member的sorce增加增量increment
	 *
	 * @param increment
	 * @param member
	 * @return
	 */
	double zincrby(final double increment, final T member);

	/****
	 * 获取指定区间内的成员，成员位置按sorce递增(小到大)来排列,下标可以为负数, 表示倒序,，-1表示尾部第一个. 0 2 会得到0, 1,
	 * 2三个元素
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	Set<T> zrange(final long start, final long end);

	/****
	 * 获取指定区间内的成员和scores，成员位置按scores递增(小到大)来排列,下标可以为负数, 表示倒序,，-1表示尾部第一个. 0 2
	 * 会得到0, 1, 2三个元素
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	Set<Tuple> zrangeWithScores(final long start, final long end);

	/***
	 * 获取指定区间内的成员，成员位置按scores值递减(大到小)来排列，下标可以为负数, 表示倒序, -1表示尾部第一个. 0 2 会得到0, 1,
	 * 2三个元素
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	Set<T> zrevrange(final long start, final long end);

	/****
	 * 获取指定区间内的成员和scores，成员位置按scores值递减(大到小)来排列，下标可以为负数，表示倒序，-1表示尾部第一个. 0 2
	 * 会得到0, 1, 2三个元素
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	Set<Tuple> zrevrangeWithScores(final long start, final long end);

	/***
	 * 获取所有 成员，score (sorce值递增 从小到大)值介于 min 和 max 之间(包括等于 min 或 max )
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	Set<T> zrangeByScore(final double min, final double max);

	/****
	 * 获取所有成员和scores，score (sorce值递增 从小到大)值介于 min 和 max 之间(包括等于 min 或 max )
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	Set<Tuple> zrangeByScoreWithScores(final double min, final double max);

	/***
	 * 移除一个或多个成员
	 *
	 * @param members
	 * @return
	 */
	long zrem(@SuppressWarnings("unchecked") final T... members);

	/***
	 * 删除指定排名区间内的所有成员，可以为负数, 表示倒序, -1表示尾部第一个. 0 2 会得到0, 1, 2三个元素
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	long zremrangebyrank(final long start, final long end);

	/***
	 * 删除所有sorce介于min与max(包括min,max)之间的成员
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	long zremrangebysore(final double min, final double max);

	/***
	 * 返回成员member的sorce值
	 *
	 * @param member
	 * @return Double 如果缓存中member不存在，Double为空
	 */
	Double zscore(final T member);

	/**
	 * 是否是成员
	 *
	 * @param member
	 *            成员对象
	 * @return true 表示存在
	 */
	boolean isMember(final T member);

}
