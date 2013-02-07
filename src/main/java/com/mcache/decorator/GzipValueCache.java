package com.mcache.decorator;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;
import com.mtoolkit.util.ConversionUtil;
import com.mtoolkit.util.GzipUtil;

/**
 * A decorate cache repository engine that provides use gzip algorithm to compress the 
 * cache value before put into the cache repository and uncompress it before return to user.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * 
 * @see		com.mtoolkit.util.GzipUtil
 */
public class GzipValueCache extends CacheDecorator {

	public GzipValueCache(Cache cache) {
		super(cache);
	}
	
	@Override
	public boolean put(String key, Object value) {
		return getDelegateCache().put(key, gzip(value));
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value) {
		return getDelegateCache().asyncPut(key, gzip(value));
	}

	@Override
	public boolean put(String key, Object value, long expiredTime) {
		return getDelegateCache().put(key, gzip(value), expiredTime);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		return getDelegateCache().asyncPut(key, gzip(value), expiredTime);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		return getDelegateCache().put(key, gzip(value), expiredDate);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		return getDelegateCache().asyncPut(key, gzip(value), expiredDate);
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		return getDelegateCache().put(key, gzip(value), operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		return getDelegateCache().asyncPut(key, gzip(value), operation);
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		return getDelegateCache().put(key, gzip(value), expiredTime, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		return getDelegateCache().asyncPut(key, gzip(value), expiredTime, operation);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return getDelegateCache().put(key, gzip(value), expiredDate, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return getDelegateCache().asyncPut(key, gzip(value), expiredDate, operation);
	}
	
	@Override
	public <T> T get(String key) {
		return unGzip((byte[]) getDelegateCache().get(key));
	}
	
	@Override
	public <T> Map<String, T> get(String[] keys) {
		if (keys == null || keys.length == 0) {
			return getDelegateCache().get(keys);
		}

		Map<String, byte[]> values = getDelegateCache().get(keys);
		if (values == null || values.isEmpty()) {
			return Collections.emptyMap();
		}

		final Map<String, T> resultMap = new HashMap<String, T>(values.size(), 1F);
		T value = null;
		for (Entry<String, byte[]> entry : values.entrySet()) {
			value = unGzip(entry.getValue());
			resultMap.put(entry.getKey(), value);
		}

		return Collections.unmodifiableMap(resultMap);
	}
	
	// ---- private methods
	private byte[] gzip(Object value) {
		if (value == null) {
			return null;
		}

		byte[] byteDatas = null;
		try {
			byteDatas = ConversionUtil.object2Bytes(value);
		} catch (IOException ex) {
			throw new DecorateCacheException("Convert object to byte array exception", ex);
		}

		try {
			return GzipUtil.gzip(byteDatas);
		} catch (IOException ex) {
			throw new DecorateCacheException("Gzip byte datas exception", ex);
		}
	}

	private <T> T unGzip(byte[] gzipDatas) {
		if (gzipDatas == null) {
			return null;
		}

		byte[] byteDatas = null;
		try {
			byteDatas = GzipUtil.unGzip(gzipDatas);
		} catch (IOException e) {
			throw new DecorateCacheException("Ungzip byte datas exception", e);
		}

		try {
			return ConversionUtil.bytes2Object(byteDatas);
		} catch (IOException e) {
			throw new DecorateCacheException("Ungzip byte array to object exception", e);
		} catch (ClassNotFoundException ex) {
			throw new DecorateCacheException("Ungizp not found target object class exception", ex);
		}
	}

}

