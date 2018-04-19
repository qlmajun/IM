package com.pft.communicate.cache.impl;

import com.pft.communicate.cache.Cache;
import com.pft.communicate.cache.common.Reusable;

public abstract class CacheImpl implements Cache, Reusable {

	public static final String NAME_SPLIT = "::";

	private String namePrefix;

	private String cacheCategoryName;

	private String itemName;

	private String cacheFullName;

	/** 线程休眠的标志，当重试机会剩余一次的时候，线程休眠 */
	protected static final int THREAD_SLEEP_FLAG = 1;

	/**
	 * 是否已经被回收
	 */
	private boolean recycled = false;

	public CacheImpl(final String namePrefix, final String cacheName) {
		super();
		this.namePrefix = namePrefix;
		this.cacheCategoryName = cacheName;
	}

	@Override
	public void reinit(String id) {
		setItemName(id);
		setRecycled(false);
	}

	public boolean isRecycled() {
		return recycled;
	}

	public void setRecycled(boolean recycled) {
		this.recycled = recycled;
	}

	/**
	 * 设置Item的名称, 保证可以重用
	 *
	 * @param itemName
	 * @return
	 */
	public String setItemName(final String itemName) {
		StringBuilder builder = new StringBuilder(namePrefix);
		builder.append(NAME_SPLIT);
		builder.append(cacheCategoryName);
		builder.append(NAME_SPLIT);
		builder.append(itemName);
		cacheFullName = builder.toString();
		return cacheFullName;
	}

	@Override
	public String getNamePrefix() {
		return namePrefix;
	}

	@Override
	public String getCacheCategoryName() {
		return cacheCategoryName;
	}

	@Override
	public String getCacheItemName() {
		return itemName;
	}

	@Override
	public String getCacheFullName() {
		return cacheFullName;
	}

}
