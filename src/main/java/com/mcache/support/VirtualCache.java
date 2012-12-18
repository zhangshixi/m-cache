package com.mcache.support;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.mcache.CasOperation;

/**
 * A virtual cache engine driver implementation.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 */
public class VirtualCache extends AbstractCache {

	public VirtualCache() {
		this(VirtualCache.class.getName());
	}

	public VirtualCache(String id) {
	    super(id);
	    setThreadPoolSize(1);
	}
	
	@Override
	protected void doInitialize() {
	}

	@Override
	protected void doDestroy() {
	}

	@Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public boolean put(String key, Object value) {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value) {
    	return SucceedFuture.BOOLEAN_TRUE;
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate) {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
    	return SucceedFuture.BOOLEAN_TRUE;
    }

    @Override
    public boolean put(String key, Object value, long expiredTime) {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
    	return SucceedFuture.BOOLEAN_TRUE;
    }
    
    @Override
    public boolean put(String key, Object value, CasOperation<Object> operation) {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
    	return SucceedFuture.BOOLEAN_TRUE;
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
    	return SucceedFuture.BOOLEAN_TRUE;
    }
    
    @Override
    public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
    	return SucceedFuture.BOOLEAN_TRUE;
    }
    
    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public <T> Map<String, T> get(String[] keys) {
        return Collections.emptyMap();
    }

    @Override
    public <T> T remove(String key) {
        return null;
    }
    
    @Override
    public <T> Future<T> asyncRemove(String key) {
    	return null;
    }
    
    @Override
    public <T> List<T> remove(String[] keys) {
        return Collections.emptyList();
    }
    
    @Override
    public <T> Future<List<T>> asyncRemove(String[] keys) {
    	List<T> result = Collections.emptyList();
    	return new SucceedFuture<List<T>>(result);
    }
    
    @Override
    public boolean clear() {
        return true;
    }
    
    @Override
    public Future<Boolean> asyncClear() {
    	return SucceedFuture.BOOLEAN_TRUE;
    }
    
    @Override
    public long getNumber(String key) {
        return 0L;
    }

    @Override
    public long increase(final String key, final long value) {
        return 0L;
    }
    
    @Override
    public Future<Long> asyncIncrease(final String key, final long value) {
    	return SucceedFuture.LONG_ZORE;
    }
    
    @Override
    public long decrease(final String key, final long value) {
        return 0L;
    }
	
    @Override
    public Future<Long> asyncDecrease(final String key, final long value) {
    	return SucceedFuture.LONG_ZORE;
    }
    
}