package com.pft.communicate.cache.common;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 对象管理池
 * 
 * @author majun@12301.cc
 *
 * @param <T>
 */

public abstract class ReusableObjectPool<T extends Reusable> extends BasePooledObjectFactory<T> {

	private static final Logger logger = LoggerFactory.getLogger(ReusableObjectPool.class);

	public static final int OBJECT_POOL_MAX_TOTAL = 1000;
	public static final int OBJECT_POOL_MAX_IDLE = 500;
	public static final int OBJECT_POOL_MIN_IDLE = 10;

	public static final int OBJECT_POOL_MAX_WAIT_MILLS = 300;

	/** 缓存具体缓存条目中的操作类, 通过setCacheName替换操作对象 **/
	private GenericObjectPool<T> pool;

	public ReusableObjectPool() {
		this(OBJECT_POOL_MAX_TOTAL, OBJECT_POOL_MAX_IDLE, OBJECT_POOL_MIN_IDLE);
	}

	public ReusableObjectPool(int maxTotal, int maxIdle, int minIdle) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(OBJECT_POOL_MAX_WAIT_MILLS);
		pool = new GenericObjectPool<T>(this, config);
	}

	/**
	 * 获取指定的缓存操作对象, 需要再使用完成后,通过{@code RedisFlatHashCache}的returnResource方法将该对象回收
	 *
	 * @param id
	 * @return
	 */
	public T getObject(final String id) {

		if (id == null || id.length() == 0) {
			throw new IllegalArgumentException("object id can not be empty");
		}

		try {
			T reuseObj = pool.borrowObject();
			reuseObj.reinit(id);
			return reuseObj;
		} catch (Exception e) {
			logger.error("Exception when borrow cache object with max " + pool.getMaxTotal() + " and current borrowed "
					+ pool.getBorrowedCount() + " and total create count " + pool.getCreatedCount()
					+ " and total destory count " + pool.getDestroyedCount() + " and idle object " + pool.getNumIdle()
					+ " and mean active time mills " + pool.getMeanActiveTimeMillis() + " MeanBorrowWaitTimeMillis "
					+ pool.getMeanBorrowWaitTimeMillis(), e);
			return null;
		}
	}

	public String getObjectPoolStatus() {
		return "Cache object with max " + pool.getMaxTotal() + ", current borrowed " + pool.getBorrowedCount()
				+ ", total create count " + pool.getCreatedCount() + ", total destory count " + pool.getDestroyedCount()
				+ ", idle object " + pool.getNumIdle() + ", mean active time mills " + pool.getMeanActiveTimeMillis()
				+ ", MeanBorrowWaitTimeMillis " + pool.getMeanBorrowWaitTimeMillis();
	}

	/**
	 * object被使用完成后, 回收改对象
	 *
	 * @param reusableObj
	 */
	public void returnObject(T reusableObj) {
		try {
			pool.returnObject(reusableObj);
		} catch (Exception e) {
			logger.error("Exception when return reusable object ", e);
		}
	}

	@Override
	public abstract T create() throws Exception;

	@Override
	public PooledObject<T> wrap(T obj) {
		DefaultPooledObject<T> defaultPooledObject = new DefaultPooledObject<T>(obj);
		defaultPooledObject.setLogAbandoned(true);
		return defaultPooledObject;
	}

}
