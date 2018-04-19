package com.pft.communicate.cache.redis.statistic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存命中情况统计类
 *
 * @author majun@12301.cc
 *
 */
public class CacheStatistic {

	/** 命中缓存的总次数 **/
	private AtomicLong hits = new AtomicLong();

	/** 没有命中缓存的总次数 **/
	private AtomicLong misses = new AtomicLong();

	public void hit() {
		hits.incrementAndGet();
	}

	public void miss() {
		misses.incrementAndGet();
	}

	public long getCacheHits() {
		return hits.get();
	}

	public long getCacheMisses() {
		return misses.get();
	}

	public long getTotalQuerys() {
		return hits.get() + misses.get();
	}

}
