package com.mcache.decorator;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;

/**
 * FIFO (first in, first out) cache decorator
 */
public class FifoCache extends CacheDecorator {

	private int _size;
    private final LinkedList<String> _keyList;
    
    public static final int DEF_SIZE = 1024;

    public FifoCache(Cache cache) {
        this(cache, DEF_SIZE);
    }
    
    public FifoCache(Cache cache, int size) {
    	super(cache);
    	_size = size;
    	_keyList = new LinkedList<String>();
    }
    
    public int getSize() {
        return _size;
    }
    
    public void setSize(int size) {
        _size = size;
    }

    @Override
	public boolean put(String key, Object value) {
    	cycleKeyList(key, false);
    	return getDelegateCache().put(key, value);
    }
    
    @Override
	public Future<Boolean> asyncPut(String key, Object value) {
    	cycleKeyList(key, true);
		return getDelegateCache().asyncPut(key, value);
	}
    
    @Override
	public boolean put(String key, Object value, long expiredTime) {
    	cycleKeyList(key, false);
    	return getDelegateCache().put(key, value, expiredTime);
    }
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncPut(key, value, expiredTime);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		cycleKeyList(key, false);
		return getDelegateCache().put(key, value, expiredDate);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncPut(key, value, expiredDate);
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		cycleKeyList(key, false);
		return getDelegateCache().put(key, value, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncPut(key, value, operation);
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		cycleKeyList(key, false);
		return getDelegateCache().put(key, value, expiredTime, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncPut(key, value, expiredTime, operation);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		cycleKeyList(key, false);
		return getDelegateCache().put(key, value, expiredDate, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncPut(key, value, expiredDate, operation);
	}

	@Override
    public boolean clear() {
		boolean result = super.clear();
    	_keyList.clear();
    	return result;
	}
	
    @Override
    public Future<Boolean> asyncClear() {
    	Future<Boolean> result = super.asyncClear();
    	_keyList.clear();
    	return result;
    }
	
	@Override
	public long increase(String key, long value) {
		cycleKeyList(key, false);
		return getDelegateCache().increase(key, value);
	}
	
	@Override
	public Future<Long> asyncIncrease(String key, long value) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncIncrease(key, value);
	}

	@Override
	public long decrease(String key, long value) {
		cycleKeyList(key, false);
		return getDelegateCache().decrease(key, value);
	}
	
	@Override
	public Future<Long> asyncDecrease(String key, long value) {
		cycleKeyList(key, true);
		return getDelegateCache().asyncDecrease(key, value);
	}
    
    // ---- private methods
    private void cycleKeyList(String key, boolean async) {
        _keyList.addLast(key);
        if (_keyList.size() > _size) {
        	String oldestKey = _keyList.removeFirst();
        	if (async) {
        		asyncRemove(oldestKey);
        	} else {
        		remove(oldestKey);
        	}
        }
    }

}
