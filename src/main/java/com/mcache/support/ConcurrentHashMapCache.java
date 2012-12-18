package com.mcache.support;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.mcache.CasOperation;

/**
 * ConcurrentHashMap cache engine driver implementation.
 */
public class ConcurrentHashMapCache extends AbstractCache {

private static final long UN_EXPIRED_TIME = -1L;
    
    private final Map<String, CacheEntry> _cache = new ConcurrentHashMap<String, CacheEntry>(100, 0.75F, 16);
    
    // ---- constructors ------------------------------------------------------------------------------------
    public ConcurrentHashMapCache() {
        this(ConcurrentHashMapCache.class.getName());
    }
    
    public ConcurrentHashMapCache(String id) {
        super(id);
    }
    
    public ConcurrentHashMapCache(String id, int threadPoolSize) {
        super(id);
        setThreadPoolSize(threadPoolSize);
    }
    
    @Override
    protected void doInitialize() {
    }

    @Override
    protected void doDestroy() {
    }
    
    @Override
    public boolean containsKey(String key) {
        final CacheEntry cacheEntry = _cache.get(key);
        return cacheEntry != null && !cacheEntry.isExpired();
    }

    @Override
    public boolean put(String key, Object value) {
        return put(key, value, UN_EXPIRED_TIME);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value) {
    	return asyncPut(key, value, UN_EXPIRED_TIME);
    }

    @Override
    public boolean put(String key, Object value, CasOperation<Object> operation) {
        return put(key, value, UN_EXPIRED_TIME, operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
    	return asyncPut(key, value, UN_EXPIRED_TIME, operation);
    }
    
    @Override
    public boolean put(final String key, final Object value, final long expiredTime, CasOperation<Object> operation) {
        _cache.put(key, new CacheEntry(key, value, expiredTime));
        return true;
    }
    
    @Override
	@SuppressWarnings("unchecked")
    public <T> T get(String key) {
        CacheEntry cacheEntry =  _cache.get(key);
        if (cacheEntry == null || cacheEntry.isExpired()) {
            return null;
        } else {
            return (T) cacheEntry.getValue();
        }
    }

	@Override
	@SuppressWarnings("unchecked")
    public <T> T remove(String key) {
        CacheEntry cacheEntry = _cache.remove(key);
        return (T) cacheEntry.getValue();
    }
    
    @Override
    public boolean clear() {
        _cache.clear();
        return true;
    }
    
    @Override
    public long getNumber(String key) {
        CacheEntry cacheEntry = _cache.get(key);
        if (cacheEntry == null || cacheEntry.isExpired()) {
            return 0L;
        } else {
            return ((Long) cacheEntry.getValue()).longValue();
        }
    }

    @Override
    public long increase(String key, long value) {
        CacheEntry cacheEntry = _cache.get(key);
        
        long oldValue = 0L;
        if (cacheEntry != null && !cacheEntry.isExpired()) {
            oldValue = ((Long) cacheEntry.getValue()).longValue();
        }

        Long newValue = Long.valueOf(oldValue + value);
        _cache.put(key, new CacheEntry(key, newValue, UN_EXPIRED_TIME));
        
        return newValue;
    }

    @Override
    public long decrease(String key, long value) {
        CacheEntry cacheEntry = _cache.get(key);
        
        long oldValue = 0L;
        if (cacheEntry != null && !cacheEntry.isExpired()) {
            oldValue = ((Long) cacheEntry.getValue()).longValue();
        }
        
        Long newValue = Long.valueOf(oldValue - value);
        _cache.put(key, new CacheEntry(key, newValue, UN_EXPIRED_TIME));
        
        return newValue;
    }

    // ---- inner classes ----------------------------------------------------------------
    private class CacheEntry implements Serializable {

        private static final long serialVersionUID = 77803946779892716L;
        
        private String _key;
        private Object _value;
        private long _putTime;
        private long _expiredTime;
        
        public CacheEntry(String key, Object value, long expiredTime) {
            _key = key;
            _value = value;
            _putTime = System.currentTimeMillis();
            _expiredTime = expiredTime;
        }
        
        public Object getValue() {
            return _value;
        }
        
        public boolean isExpired() {
            if (_expiredTime <= 0) {
                return false;
            }
            
            boolean expired = System.currentTimeMillis() - _putTime > _expiredTime; 
            if (expired) {
                _cache.remove(_key); // remove expired value.
            }
            
            return expired;
        }
        
    }
    
}
