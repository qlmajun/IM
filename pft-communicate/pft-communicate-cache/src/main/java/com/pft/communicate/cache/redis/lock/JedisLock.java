package com.pft.communicate.cache.redis.lock;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

public class JedisLock implements Lock {

	private static final Logger logger = LoggerFactory.getLogger(JedisLock.class);

	private JedisConnectionFactory jedisConnectionFactory;

	/** 加锁标志 */
	public static final byte[] LOCKED = "1".getBytes();

	public static final byte[] NXXX = "NX".getBytes(Charset.forName(Protocol.CHARSET));

	public static final byte[] EXXX = "EX".getBytes(Charset.forName(Protocol.CHARSET));

	public static final String OK = "OK";

	public static final Random RANDOM = new Random();

	public static final int EXPIRE_DEFAULT = 2;

	/** 锁的超时时间（秒），过期删除 */
	private int EXPIRE = EXPIRE_DEFAULT;

	/** 锁的键值 **/
	private byte[] lockName;

	/** 锁状态标志 **/
	private boolean locked = false;

	/**
	 * redis缓存操作的锁
	 *
	 * @param cacheName 缓存的名称
	 * @param key 缓存的键值
	 */
	public JedisLock(JedisConnectionFactory jedisConnectionFactory, String cacheName, Object key) {
		this(jedisConnectionFactory, cacheName, key, EXPIRE_DEFAULT);
	}

	public JedisLock(JedisConnectionFactory jedisConnectionFactory, String cacheName, Object key, int expireSecond) {
		String keyName;
		if (key != null && key instanceof String) {
			keyName = (String) key;
		}
		keyName = String.valueOf(key.hashCode());

		this.lockName = (cacheName + "::::" + keyName + "::::lock").getBytes();

		this.jedisConnectionFactory = jedisConnectionFactory;
		this.EXPIRE = expireSecond;
	}

	@Override
	public void lock() {
		try {
			this.tryLock(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("InterruptedException when try to lock", e);
		}
	}

	@Override
	public void unlock() {
		if (this.locked) {
			Jedis jedis = jedisConnectionFactory.getResource();
			try {
				jedis.del(this.lockName);
				this.locked = false;
			} finally {
				jedis.close();
			}
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		tryLock(Integer.MAX_VALUE, TimeUnit.SECONDS, true);
	}

	@Override
	public boolean tryLock() {
		Jedis jedis;
		try {
			jedis = jedisConnectionFactory.getResource();
		} catch (Exception e) {
			/**
			 * 发生异常时直接返回true, 使服务器运行不至于中断
			 */
			logger.error("Exception when try to lock", e);
			return true;
		}

		try {
			if (OK.equals(jedis.set(lockName, LOCKED, NXXX, EXXX, EXPIRE))) {
				this.locked = true;
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.warn("lock failed", e);
			return false;
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
		return tryLock(timeout, unit, false);
	}

	private boolean tryLock(long timeout, TimeUnit unit, boolean interruptibly) throws InterruptedException {

		long nano = System.nanoTime();
		try {
			while ((System.nanoTime() - nano) < unit.toNanos(timeout)) {

				if (interruptibly && Thread.interrupted()) {
					throw new InterruptedException();
				}

				if (tryLock()) {
					return true;
				}
				// 短暂休眠，避免出现活锁
				Thread.sleep(50, RANDOM.nextInt(5000));
			}
		} catch (Exception e) {
			logger.error("Exception when try to lock", e);
			throw new RuntimeException("Locking error", e);
		}
		return false;
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}
}
