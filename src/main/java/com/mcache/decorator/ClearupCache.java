package com.mcache.decorator;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;
import com.mcache.support.AbstractCache.SucceedFuture;

/**
 * Auto clear cache decorator. 
 */
public class ClearupCache extends CacheDecorator {
    
    private long _clearInterval;
    private long _lastClearTime;
    
    public static final long DEF_CLEAR_INTERVAL = 60 * 60 * 1000L;
    
    public ClearupCache(Cache cache) {
        this(cache, DEF_CLEAR_INTERVAL);
    }
    
    public ClearupCache(Cache cache, long clearInterval) {
        super(cache);
        _clearInterval = clearInterval;
    }
    
    @Override
    public void startup() {
        _lastClearTime = System.currentTimeMillis();
        getDelegateCache().startup();
    }

    @Override
    public void shutdown() {
        getDelegateCache().shutdown();
    }
    
    @Override
    public String getId() {
        return getDelegateCache().getId();
    }

    @Override
    public boolean isInitialized() {
        return getDelegateCache().isInitialized();
    }
    
    @Override
    public boolean containsKey(String key) {
        if (clearWhenStale()) {
            return false;
        } else {
            return getDelegateCache().containsKey(key);
        }
    }

    @Override
    public boolean put(String key, Object value) {
        clearWhenStale();
        return getDelegateCache().put(key, value);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value) {
        clearWhenStale();
        return getDelegateCache().asyncPut(key, value);
    }
    
    @Override
    public boolean put(String key, Object value, long expiredTime) {
        clearWhenStale();
        return getDelegateCache().put(key, value, expiredTime);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
        clearWhenStale();
        return getDelegateCache().asyncPut(key, value, expiredTime);
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate) {
        clearWhenStale();
        return getDelegateCache().put(key, value, expiredDate);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
        clearWhenStale();
        return getDelegateCache().asyncPut(key, value, expiredDate);
    }
    
    @Override
    public boolean put(String key, Object value, CasOperation<Object> operation) {
        clearWhenStale();
        return getDelegateCache().put(key, value, operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
        clearWhenStale();
        return getDelegateCache().asyncPut(key, value, operation);
    }

    @Override
    public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        clearWhenStale();
        return getDelegateCache().put(key, value, expiredTime, operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        clearWhenStale();
        return getDelegateCache().asyncPut(key, value, expiredTime, operation);
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        clearWhenStale();
        return getDelegateCache().put(key, value, expiredDate, operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        clearWhenStale();
        return getDelegateCache().asyncPut(key, value, expiredDate, operation);
    }

    @Override
    public <T> T get(String key) {
        if (clearWhenStale()) {
            return null;
        } else {
            return getDelegateCache().get(key);
        }
    }

    @Override
    public <T> Map<String, T> get(String[] keys) {
        if (clearWhenStale()) {
            return Collections.emptyMap();
        } else {
            return getDelegateCache().get(keys);
        }
    }

    @Override
    public <T> T remove(String key) {
        if (clearWhenStale()) {
            return null;
        } else {
            return getDelegateCache().remove(key);
        }
    }
    
    @Override
    public <T> Future<T> asyncRemove(String key) {
        if (clearWhenStale()) {
            return new SucceedFuture<T>(null);
        } else {
            return getDelegateCache().asyncRemove(key);
        }
    }

    @Override
    public <T> List<T> remove(String[] keys) {
        if (clearWhenStale()) {
            return Collections.emptyList();
        } else {
            return getDelegateCache().remove(keys);
        }
    }
    
    @Override
    public <T> Future<List<T>> asyncRemove(String[] keys) {
        if (clearWhenStale()) {
            List<T> resultList = Collections.emptyList();
            return new SucceedFuture<List<T>>(resultList);
        } else {
            return getDelegateCache().asyncRemove(keys);
        }
    }

    @Override
    public boolean clear() {
        _lastClearTime = System.currentTimeMillis();
        return getDelegateCache().clear();
    }
    
    @Override
    public Future<Boolean> asyncClear() {
        _lastClearTime = System.currentTimeMillis();
        return getDelegateCache().asyncClear();
    }
    
    @Override
    public long getNumber(String key) {
        // clear unsupported
        return getDelegateCache().getNumber(key);
    }
    
    @Override
    public long increase(String key, long value) {
        // clear unsupported
        return getDelegateCache().increase(key, value);
    }
    
    @Override
    public Future<Long> asyncIncrease(String key, long value) {
        // clear unsupported
        return getDelegateCache().asyncIncrease(key, value);
    }

    @Override
    public long decrease(String key, long value) {
        // clear unsupported
        return getDelegateCache().decrease(key, value);
    }
    
    @Override
    public Future<Long> asyncDecrease(String key, long value) {
        // clear unsupported
        return getDelegateCache().asyncDecrease(key, value);
    }
    
    // ---- private methods
    private boolean clearWhenStale() {
        long idleTime = System.currentTimeMillis() - _lastClearTime;
        if (idleTime > _clearInterval) {
            clear();
            return true;
        } else {
            return false;
        }
    }
    
}
