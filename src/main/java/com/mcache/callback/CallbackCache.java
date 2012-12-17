package com.mcache.callback;

import com.mcache.Cache;

public abstract class CallbackCache {

	private Cache _cache;
	private int _version;

	private String generateKey(KeyGenerator key) {
		return _version + "." + key.generateKey();
	}

	public CallbackCache(Cache cache, int version) {
		_cache = cache;
		_version = version;
	}

	public <T> void put(String key, Object value) {
		_cache.asyncPut(key, value);
	}

	public <T> T get(KeyGenerator key, ValueLoader value) {
		String k = generateKey(key);
		T result = _cache.get(k);
		if (result == null) {
			result = value.loadValue();
			_cache.asyncPut(k, result);
		}
		return result;
	}

	public void remove(String key) {
		_cache.asyncRemove(key);
	}

	public void remove(KeyGenerator key) {
		_cache.asyncRemove(key.generateKey());
	}

}