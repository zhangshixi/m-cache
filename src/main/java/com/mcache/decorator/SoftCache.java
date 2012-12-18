package com.mcache.decorator;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;

/**
 * Soft Reference cache decorator.
 */
public class SoftCache extends CacheDecorator {

	private int _numberOfHardLinks;
	private LinkedList<Object> _hardLinksToAvoidGarbageCollection;
	private ReferenceQueue<Object> _gcQueue;
	
	public static final int DEFAULT_SIZE = 256;
	
    public SoftCache(Cache cache) {
        this(cache, DEFAULT_SIZE);
    }
    
    public SoftCache(Cache cache, int size) {
    	super(cache);
    	_numberOfHardLinks = size;
    	_hardLinksToAvoidGarbageCollection = new LinkedList<Object>();
    	_gcQueue = new ReferenceQueue<Object>();
    }
    
    public int getSize() {
        return _numberOfHardLinks;
    }
    
    public void setSize(int size) {
    	_numberOfHardLinks = size;
    }

    @Override
	public boolean put(String key, Object value) {
    	gcSoftEntries(false);
		return getDelegateCache().put(key, new SoftEntry<Object>(key, value, _gcQueue));
	}
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value) {
    	gcSoftEntries(true);
    	return getDelegateCache().asyncPut(key, new SoftEntry<Object>(key, value, _gcQueue));
    }
	
	@Override
	public boolean put(String key, Object value, long expiredTime) {
		gcSoftEntries(false);
		return getDelegateCache().put(key, new SoftEntry<Object>(key, value, _gcQueue), expiredTime);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		gcSoftEntries(true);
		return getDelegateCache().asyncPut(key, new SoftEntry<Object>(key, value, _gcQueue), expiredTime);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		gcSoftEntries(false);
		return getDelegateCache().put(key, new SoftEntry<Object>(key, value, _gcQueue), expiredDate);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		gcSoftEntries(true);
		return getDelegateCache().asyncPut(key, new SoftEntry<Object>(key, value, _gcQueue), expiredDate);
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		gcSoftEntries(false);
		return getDelegateCache().put(key, new SoftEntry<Object>(key, value, _gcQueue), operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		gcSoftEntries(true);
		return getDelegateCache().asyncPut(key, new SoftEntry<Object>(key, value, _gcQueue), operation);
	}

	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		gcSoftEntries(false);
		return getDelegateCache().put(key, new SoftEntry<Object>(key, value, _gcQueue), expiredTime, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		gcSoftEntries(true);
		return getDelegateCache().asyncPut(key, new SoftEntry<Object>(key, value, _gcQueue), expiredTime, operation);
	}

	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		gcSoftEntries(false);
		return getDelegateCache().put(key, new SoftEntry<Object>(key, value, _gcQueue), expiredDate, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		gcSoftEntries(true);
		return getDelegateCache().asyncPut(key, new SoftEntry<Object>(key, value, _gcQueue), expiredDate, operation);
	}

    @Override
    public <T> T get(String key) {
    	T value = null;
    	SoftEntry<T> softReference = getDelegateCache().get(key);
    	if (softReference != null) {
    		value = softReference.get();
    		if (value == null) {
    			getDelegateCache().asyncRemove(key);
    		} else {
    			_hardLinksToAvoidGarbageCollection.addFirst(value);
    	        if (_hardLinksToAvoidGarbageCollection.size() > _numberOfHardLinks) {
    	        	_hardLinksToAvoidGarbageCollection.removeLast();
    	        }
    		}
    	}
    	
        return value;
    }

    @Override
    public Map<String, Object> get(String[] keys) {
    	// TODO:
        return null;
    }

    @Override
    public <T> T remove(String key) {
    	gcSoftEntries(false);
    	return getDelegateCache().remove(key);
    }
    
    @Override
    public <T> Future<T> asyncRemove(String key) {
    	gcSoftEntries(true);
    	return getDelegateCache().asyncRemove(key);
    }
    
    @Override
    public <T> List<T> remove(String[] keys) {
    	// TODO:
    	return getDelegateCache().remove(keys);
    }
    
    @Override
    public <T> Future<List<T>> asyncRemove(String[] keys) {
    	// TODO:
    	return getDelegateCache().asyncRemove(keys);
    }

    @Override
    public boolean clear() {
    	boolean result = super.clear();
    	_hardLinksToAvoidGarbageCollection.clear();
        gcSoftEntries(false);
        
        return result;
    }
    
    @Override
    public Future<Boolean> asyncClear() {
    	Future<Boolean> result = super.asyncClear();
    	_hardLinksToAvoidGarbageCollection.clear();
    	gcSoftEntries(true);
    	
    	return result;
    }
    
    @Override
    public long getNumber(String key) {
    	// TODO:
        return getDelegateCache().getNumber(key);
    }

    @Override
    public long increase(String key, long value) {
    	// TODO:
        return getDelegateCache().increase(key, value);
    }
    
    @Override
    public Future<Long> asyncIncrease(String key, long value) {
    	// TODO:
    	return getDelegateCache().asyncIncrease(key, value);
    }
    
    @Override
    public long decrease(String key, long value) {
    	// TODO:
    	return getDelegateCache().decrease(key, value);
    }
    
    // ---- private methods --------------------------------------------------------
    private void gcSoftEntries(boolean async) {
    	SoftEntry<?> entry;
        while ((entry = (SoftEntry<?>) _gcQueue.poll()) != null) {
        	if (async) {
        		asyncRemove(entry._key);
        	} else {
        		remove(entry._key);
        	}
        }
    }
    
    private static class SoftEntry<T> extends SoftReference<T> {
        private final String _key;

        private SoftEntry(String key, T value, ReferenceQueue<Object> gcQueue) {
        	super(value, gcQueue);
        	_key = key;
        }
    }

}
