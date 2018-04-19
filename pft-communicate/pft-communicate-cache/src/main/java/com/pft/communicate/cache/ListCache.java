package com.pft.communicate.cache;

import java.io.Serializable;
import java.util.List;
import redis.clients.jedis.BinaryClient.LIST_POSITION;

/***
 * List集合类型缓存操作接口声明
 * 
 * @author majun@12301.cc
 *
 * @param <T>
 */
public interface ListCache<T extends Serializable> extends Cache {

	@SuppressWarnings("unchecked")
	long rpush(final T... vals);

	long lpush(@SuppressWarnings("unchecked") final T... vals);

	T lpop();

	Long linsert(final byte[] key, final LIST_POSITION where, final byte[] pivot, final byte[] value);

	T rpop();

	long size();

	T lIndex(final long index);

	boolean lset(final long index, final T value);

	long lrem(final long count, final T value);

	/**
	 * 可以为负数, 表示倒序, -1表示尾部第一个. 0 2 会得到0, 1, 2三个元素
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	List<T> lrange(final long start, final long end);

	T rpoplpush(final String destCacheName);

}
