package com.pft.communicate.cache.redis.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.cache.common.ReusableObjectPool;
import com.pft.communicate.cache.impl.CacheImpl;
import com.pft.communicate.cache.redis.cache.RedisOp.RedisOperation;
import com.pft.communicate.cache.redis.connection.JedisConnectionFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public abstract class JedisCacheImpl extends CacheImpl {

	private static final Logger logger = LoggerFactory.getLogger(JedisCacheImpl.class);

	protected static final Charset charset = Charset.forName("UTF8");

	protected static final String OK = "ok";

	protected static final byte[] nilBytes = "nil".getBytes();

	protected static final byte[] serializeBytes = { -84, -19, 0, 5 };

	/**
	 * add by zongtf3@yonyou.com <2016年4月25日> 是否自动回收，默认自动回收
	 */
	protected boolean autoRecycle = true;

	private JedisConnectionFactory connectionFactory;

	protected byte[] cacheFullNameBytes;

	@SuppressWarnings("rawtypes")
	protected ReusableObjectPool objectPool;

	public JedisCacheImpl(final String namePrefix, final String cacheName,
			final JedisConnectionFactory connectionFactory,
			@SuppressWarnings("rawtypes") ReusableObjectPool objectPool) {
		super(namePrefix, cacheName);
		this.connectionFactory = connectionFactory;
		this.objectPool = objectPool;
	}

	public boolean isAutoRecycle() {
		return autoRecycle;
	}

	public void setAutoRecycle(boolean autoRecycle) {
		this.autoRecycle = autoRecycle;
	}

	@Override
	public void reinit(String id) {
		super.reinit(id);
		cacheFullNameBytes = null;
	}

	protected byte[] getCacheFullNameBytes() {
		if (cacheFullNameBytes == null) {
			cacheFullNameBytes = getCacheFullName().getBytes(charset);
		}
		return cacheFullNameBytes;
	}

	public JedisConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public Jedis getResource() {
		return getConnectionFactory().getResource();
	}

	@Override
	public int removeCache() {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.del(getCacheFullName()), RedisOperation.del) > 0 ? 1 : 0;
		} finally {
			this.autoRecycle();
		}
	}

	@Override
	public boolean exists() {
		try {
			return functionHandlerWithOp(2, jedis -> jedis.exists(getCacheFullName()), RedisOperation.exists);
		} finally {
			this.autoRecycle();
		}
	}

	protected <T extends Serializable> byte[] objectToBytes(final T obj) {

		if (obj instanceof byte[]) {
			return (byte[]) obj;
		}

		if (obj instanceof String) {
			return ((String) obj).getBytes(charset);
		}

		if (obj instanceof Long || obj instanceof Integer) {
			return (String.valueOf(obj)).getBytes(charset);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			logger.error("IOException when serial object", e);
			throw new RuntimeException("exception when serilize object", e);
		} finally {
			try {
				oos.close();
				bos.close();
			} catch (Exception e) {
				logger.error("exception when close stream", e);
			}
		}
	}

	protected <T extends Serializable> byte[][] arrayToBytes(@SuppressWarnings("unchecked") final T... objs) {
		byte[][] bytes = new byte[objs.length][];
		for (int i = 0; i < objs.length; i++) {
			T obj = objs[i];
			bytes[i] = objectToBytes(obj);
		}
		return bytes;
	}

	protected <K extends Serializable, V extends Serializable> Map<byte[], byte[]> mapToBytes(final Map<K, V> map) {
		Map<byte[], byte[]> byteMap = new HashMap<byte[], byte[]>(map.size());
		map.forEach((k, v) -> byteMap.put(objectToBytes(k), objectToBytes(v)));
		return byteMap;
	}

	protected <T extends Serializable> List<T> toObjectsList(final Collection<byte[]> bytes) {
		List<T> result = new ArrayList<T>(bytes.size());
		bytes.forEach(b -> result.add(toObject(b)));
		return result;
	}

	protected <T extends Serializable> Set<T> toObjectsSet(final Collection<byte[]> bytes) {
		Set<T> result = new HashSet<T>(bytes.size());
		bytes.forEach(b -> result.add(toObject(b)));
		return result;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Serializable> T toObject(final byte[] bytes) {

		if (bytes == null || bytes.equals(nilBytes)) {
			return null;
		}

		if (!Arrays.equals(Arrays.copyOfRange(bytes, 0, 4), serializeBytes)) {
			return (T) new String(bytes, charset);
		}

		T obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = (T) ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			logger.error("IOException when deserial object", ex);
		} catch (ClassNotFoundException ex) {
			logger.error("ClassNotFoundException when deserial object", ex);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void recyle() {
		if (this.isRecycled()) {
			IllegalStateException illegalStateException = new IllegalStateException(
					"Object has been already recycled.");
			logger.error("Object has been already recycled.", illegalStateException);
			throw illegalStateException;
		}
		if (objectPool != null) {
			this.setRecycled(true);
			objectPool.returnObject(this);
		}
	}

	public boolean autoRecycle() {
		if (this.autoRecycle) {
			recyle();
			return true;
		}
		return false;
	}

	protected void trySleep(int retryTimes) {
		if (retryTimes == THREAD_SLEEP_FLAG) {
			sleep();
		}
	}

	/**
	 * 默认休眠500毫秒
	 */
	protected void sleep() {
		sleep(500L);
	}

	/**
	 * 缓存操作失败，进行第二次重试前，让线程休眠一段时间
	 * 
	 * @param time
	 */
	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}
	}

	public <T> T functionHandlerWithOp(int retryTimes, Function<Jedis, T> redisFunction, RedisOperation operation) {

		try {
			T result = functionHandler(retryTimes, redisFunction);
			return result;
		} catch (Exception e) {
			throw e;
		}

	}

	public <T> T functionHandler(int retryTimes, Function<Jedis, T> redisFunction) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			T result = redisFunction.apply(jedis);
			return result;
		} catch (JedisConnectionException e) {
			logger.warn("codis exception  retry {} , message {}", retryTimes, e.getMessage());
			if (retryTimes > 0) {
				trySleep(retryTimes);
				return functionHandler(retryTimes - 1, redisFunction);
			}
			throw e;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
