package com.mcache.decorator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;

/**
 * Abstract decorate cache class, children can extends this class
 * and decorate the given repository resources.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 2012-2-1
 * @since 	JDK1.5
 */
public abstract class CacheDecorator implements Cache {

	/** cache engine */
	private final Cache _cache;
	
	/**
	 * Creates a decorate cache repository engine with the specified cache.
	 * 
	 * @param cache cache repository engine.
	 */
	public CacheDecorator(Cache cache) {
		_cache = cache;
	}
	
	/**
	 * Return the current cache repository engine.
	 * 
	 * @return current cache repository engine.
	 */
	protected Cache getDelegateCache() {
		return _cache;
	}
	
	@Override
	public void startup() {
		_cache.startup();
	}

	@Override
	public void shutdown() {
		_cache.shutdown();
	}
	
	@Override
	public String getId() {
		return _cache.getId();
	}

	@Override
	public boolean isInitialized() {
		return _cache.isInitialized();
	}
	
	@Override
	public boolean containsKey(String key) {
		return _cache.containsKey(key);
	}

	@Override
	public boolean put(String key, Object value) {
		return _cache.put(key, value);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value) {
		return _cache.asyncPut(key, value);
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime) {
		return _cache.put(key, value, expiredTime);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		return _cache.asyncPut(key, value, expiredTime);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		return _cache.put(key, value, expiredDate);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		return _cache.asyncPut(key, value, expiredDate);
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		return _cache.put(key, value, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		return _cache.asyncPut(key, value, operation);
	}

	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		return _cache.put(key, value, expiredTime, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		return _cache.asyncPut(key, value, expiredTime, operation);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return _cache.put(key, value, expiredDate, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return _cache.asyncPut(key, value, expiredDate, operation);
	}

	@Override
	public <T> T get(String key) {
		return _cache.get(key);
	}

	@Override
	public <T> Map<String, T> get(String[] keys) {
		return _cache.get(keys);
	}

	@Override
	public <T> T remove(String key) {
		return _cache.remove(key);
	}
	
	@Override
	public <T> Future<T> asyncRemove(String key) {
		return _cache.asyncRemove(key);
	}

	@Override
	public <T> List<T> removes(String[] keys) {
		return _cache.removes(keys);
	}
	
	@Override
	public <T> Future<List<T>> asyncRemoves(String[] keys) {
		return _cache.asyncRemoves(keys);
	}

	@Override
	public boolean clear() {
		return _cache.clear();
	}
	
	@Override
	public Future<Boolean> asyncClear() {
		return _cache.asyncClear();
	}
	
	@Override
	public long getNumber(String key) {
		return _cache.getNumber(key);
	}
	
	@Override
	public long increase(String key, long value) {
		return _cache.increase(key, value);
	}
	
	@Override
	public Future<Long> asyncIncrease(String key, long value) {
		return _cache.asyncIncrease(key, value);
	}

	@Override
	public long decrease(String key, long value) {
		return _cache.decrease(key, value);
	}
	
	@Override
	public Future<Long> asyncDecrease(String key, long value) {
		return _cache.asyncDecrease(key, value);
	}

}