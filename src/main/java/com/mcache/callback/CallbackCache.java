package com.mcache.callback;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.decorator.CacheDecorator;
import com.mcache.support.AbstractCache.SucceedFuture;

public class CallbackCache extends CacheDecorator {

	private String _version;

	public static final String DEFAULT_CACHE_VERSION = "1.0";
	
	// ---- constructors
	public CallbackCache(Cache cache) {
		this(cache, DEFAULT_CACHE_VERSION);
	}
	
	public CallbackCache(Cache cache, String version) {
	    super(cache);
	    _version = version;
	}

	// ---- put methods
	public boolean put(Object param, KeyGenerator key, ValueLoader<Object> value) {
	    return put(generateKey(param, key), loadValue(param, value));
	}

	// ---- get methods
	public <V> V get(Object param, KeyGenerator key, ValueLoader<V> value) {
		String cacheKey = generateKey(param, key);
		V cacheValue = get(cacheKey);
		if (cacheValue == null) {
			cacheValue = loadValue(param, value);
			asyncPut(cacheKey, cacheValue);
		}
		return cacheValue;
	}
	
	public <K, V> Map<K, V> gets(K[] params, KeyBatchGenerator<K> key, ValueBatchLoader<K, V> value) {
	    Map<String, K> keyMap = generateKeys(params, key);
	    if (keyMap == null || keyMap.isEmpty()) {
	        return Collections.emptyMap();
	    }
	    
	    String[] queryKeys = keyMap.keySet().toArray(new String[0]);
	    Map<K, String> unCachedKeys = new HashMap<K, String>();
	    
	    // query from cache
	    Map<String, V> cachedValues = get(queryKeys);
	    for (String k : queryKeys) {
	        if (!cachedValues.containsKey(k)) {
	            unCachedKeys.put(keyMap.get(k), k);
	        }
	    }
	    
	    // query from loader
	    Map<K, V> loadedValues = null;
	    if (!unCachedKeys.isEmpty()) {
	        @SuppressWarnings("unchecked")
            K[] loadKeys = (K[]) unCachedKeys.keySet().toArray();
	        loadedValues = value.loadValues(loadKeys);
	        
	        // put to cache
	        for (Entry<K, V> entry : loadedValues.entrySet()) {
                asyncPut(unCachedKeys.get(entry.getKey()), entry.getValue());
            }
	    }
	    
	    // build result
	    Map<K, V> resultMap = new HashMap<K, V>();
	    for (Entry<String, V> entry : cachedValues.entrySet()) {
	        resultMap.put(keyMap.get(entry.getKey()), entry.getValue());
	    }
	    if (loadedValues != null) {
	        for (Entry<K, V> entry : loadedValues.entrySet()) {
	            resultMap.put(entry.getKey(), entry.getValue());
	        }
	    }
	    
	    return resultMap;
	}
	
	// ---- remove methods
	public <T> T remove(Object param, KeyGenerator key) {
		return remove(generateKey(param, key));
	}
	
	public <T> Future<T> asyncRemove(Object param, KeyGenerator key) {
	    return asyncRemove(generateKey(param, key));
	}
	
	public <T> List<T> removes(Object[] params, KeyBatchGenerator<Object> key) {
	    Map<String, Object> keyMap = generateKeys(params, key);
	    if (keyMap == null || keyMap.isEmpty()) {
	        return Collections.emptyList();
	    } else {
	        String[] cacheKeys = keyMap.keySet().toArray(new String[0]);
	        return remove(cacheKeys);
	    }
	}
	
	public <T> Future<List<T>> asyncRemoves(Object[] params, KeyBatchGenerator<Object> key) {
	    Map<String, Object> keyMap = generateKeys(params, key);
	    if (keyMap == null || keyMap.isEmpty()) {
	        List<T> result = Collections.emptyList();
            return new SucceedFuture<List<T>>(result);
        } else {
            String[] cacheKeys = keyMap.keySet().toArray(new String[0]);
            return asyncRemove(cacheKeys);
        }
	}
	
    // --- number operation
    public long getNumber(Object param, KeyGenerator key) {
        return getNumber(generateKey(param, key));
    }
    
    public long increase(Object param, KeyGenerator key, long value) {
        return increase(generateKey(param, key), value);
    }
    
    public long increase(Object param, KeyGenerator key, ValueLoader<Long> value) {
        return increase(generateKey(param, key), loadValue(param, value).longValue());
    }
    
    public Future<Long> asyncIncrease(Object param, KeyGenerator key, long value) {
        return asyncIncrease(generateKey(param, key), value);
    }
    
    public Future<Long> asyncIncrease(Object param, KeyGenerator key, ValueLoader<Long> value) {
        return asyncIncrease(generateKey(param, key), loadValue(param, value).longValue());
    }

    public long decrease(Object param, KeyGenerator key, long value) {
        return decrease(generateKey(param, key), value);
    }
    
    public long decrease(Object param, KeyGenerator key, ValueLoader<Long> value) {
        return decrease(generateKey(param, key), loadValue(param, value).longValue());
    }
    
    public Future<Long> asyncDecrease(Object param, KeyGenerator key, long value) {
        return asyncDecrease(generateKey(param, key), value);
    }
    
    public Future<Long> asyncDecrease(Object param, KeyGenerator key, ValueLoader<Long> value) {
        return asyncDecrease(generateKey(param, key), loadValue(param, value).longValue());
    }
    
    // ---- reload methods
    public boolean reload() {
        return false;
    }
    
    public boolean asyncReload() {
        return false;
    }
    
	// ---- private methods
	private String generateKey(Object param, KeyGenerator key) {
        return buildVersionCacheKey(key.generateKey(param));
    }
	
	private <T> Map<String, T> generateKeys(T[] param, KeyBatchGenerator<T> key) {
	    Map<String, T> keyMap = key.generateKeys(param);
	    if (keyMap == null || keyMap.isEmpty()) {
	        return Collections.emptyMap();
	    } else {
	        Map<String, T> resultMap = new HashMap<String, T>(keyMap.size(), 1F);
	        for (Entry<String, T> entry : keyMap.entrySet()) {
	            resultMap.put(buildVersionCacheKey(entry.getKey()), entry.getValue());
	        }
	        return resultMap;
	    }
	}
	
	private String buildVersionCacheKey(String key) {
	    return _version + "-" + key;
	}
	
	private <T> T loadValue(Object param, ValueLoader<T> value) {
	    return value.loadValue(param);
	}
	
}