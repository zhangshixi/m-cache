package com.mcache.decorator;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;

/**
 * Weak reference cache decorator.
 */
public class WeakCache extends CacheDecorator {

    private int _hardLinksNumber;
    private LinkedList<Object> _avoidGcHardLinks;
    private ReferenceQueue<Object> _gcEntryQueue;
    
    public static final int DEF_SIZE = 256;
    
    public WeakCache(Cache cache) {
        this(cache, DEF_SIZE);
    }
    
    public WeakCache(Cache cache, int size) {
        super(cache);
        _hardLinksNumber = size;
        _avoidGcHardLinks = new LinkedList<Object>();
        _gcEntryQueue = new ReferenceQueue<Object>();
    }
    
    public int getSize() {
        return _hardLinksNumber;
    }
    
    public void setSize(int size) {
        _hardLinksNumber = size;
    }

    @Override
    public boolean put(String key, Object value) {
        gcWeakEntries(false);
        return getDelegateCache().put(key, new WeakEntry(key, value, _gcEntryQueue));
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value) {
        gcWeakEntries(true);
        return getDelegateCache().asyncPut(key, new WeakEntry(key, value, _gcEntryQueue));
    }
    
    @Override
    public boolean put(String key, Object value, long expiredTime) {
        gcWeakEntries(false);
        return getDelegateCache().put(key, new WeakEntry(key, value, _gcEntryQueue), expiredTime);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
        gcWeakEntries(true);
        return getDelegateCache().asyncPut(key, new WeakEntry(key, value, _gcEntryQueue), expiredTime);
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate) {
        gcWeakEntries(false);
        return getDelegateCache().put(key, new WeakEntry(key, value, _gcEntryQueue), expiredDate);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
        gcWeakEntries(true);
        return getDelegateCache().asyncPut(key, new WeakEntry(key, value, _gcEntryQueue), expiredDate);
    }
    
    @Override
    public boolean put(String key, Object value, CasOperation<Object> operation) {
        gcWeakEntries(false);
        return getDelegateCache().put(key, new WeakEntry(key, value, _gcEntryQueue), operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
        gcWeakEntries(true);
        return getDelegateCache().asyncPut(key, new WeakEntry(key, value, _gcEntryQueue), operation);
    }

    @Override
    public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        gcWeakEntries(false);
        return getDelegateCache().put(key, new WeakEntry(key, value, _gcEntryQueue), expiredTime, operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        gcWeakEntries(true);
        return getDelegateCache().asyncPut(key, new WeakEntry(key, value, _gcEntryQueue), expiredTime, operation);
    }

    @Override
    public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        gcWeakEntries(false);
        return getDelegateCache().put(key, new WeakEntry(key, value, _gcEntryQueue), expiredDate, operation);
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        gcWeakEntries(true);
        return getDelegateCache().asyncPut(key, new WeakEntry(key, value, _gcEntryQueue), expiredDate, operation);
    }

    @Override
    public <T> T get(String key) {
        WeakReference<T> waekReference = getDelegateCache().get(key);
        if (waekReference == null) {
            return null;
        }
        
        T value = waekReference.get();
        if (value == null) {
            getDelegateCache().asyncRemove(key);
        } else {
            _avoidGcHardLinks.addFirst(value);
            if (_avoidGcHardLinks.size() > _hardLinksNumber) {
                _avoidGcHardLinks.removeLast();
            }
        }
        
        return value;
    }

    @Override
    public <T> Map<String, T> get(String[] keys) {
        Map<String, WeakReference<T>> weakReferenceMap = getDelegateCache().get(keys);
        if (weakReferenceMap == null || weakReferenceMap.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Set<String> removeKeySet = new HashSet<String>(); 
        Map<String, T> resultMap = new HashMap<String, T>();
        for (Entry<String, WeakReference<T>> entry : weakReferenceMap.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            
            T value = entry.getValue().get();
            if (value == null) {
                removeKeySet.add(entry.getKey());
            } else {
                resultMap.put(entry.getKey(), value);
            }
        }
        
        getDelegateCache().asyncRemove(removeKeySet.toArray(new String[0]));
        
        return resultMap;
    }

    @Override
    public <T> T remove(String key) {
        gcWeakEntries(false);
        return getDelegateCache().remove(key);
    }
    
    @Override
    public <T> Future<T> asyncRemove(String key) {
        gcWeakEntries(true);
        return getDelegateCache().asyncRemove(key);
    }
    
    @Override
    public <T> List<T> remove(String[] keys) {
        gcWeakEntries(false);
        return getDelegateCache().remove(keys);
    }
    
    @Override
    public <T> Future<List<T>> asyncRemove(String[] keys) {
        gcWeakEntries(true);
        return getDelegateCache().asyncRemove(keys);
    }

    @Override
    public boolean clear() {
        gcWeakEntries(false);
        _avoidGcHardLinks.clear();
        
        return super.clear();
    }
    
    @Override
    public Future<Boolean> asyncClear() {
        gcWeakEntries(true);
        _avoidGcHardLinks.clear();
        
        return super.asyncClear();
    }
    
    @Override
    public long getNumber(String key) {
        // weak reference unsupported
        return getDelegateCache().getNumber(key);
    }

    @Override
    public long increase(String key, long value) {
        // weak reference unsupported
        return getDelegateCache().increase(key, value);
    }
    
    @Override
    public Future<Long> asyncIncrease(String key, long value) {
        // weak reference unsupported
        return getDelegateCache().asyncIncrease(key, value);
    }
    
    @Override
    public long decrease(String key, long value) {
        // weak reference unsupported
        return getDelegateCache().decrease(key, value);
    }
    
    // ---- private methods
    private void gcWeakEntries(boolean async) {
        WeakEntry gcEntry = null;
        List<String> gcKeyList = new LinkedList<String>();
        
        while ((gcEntry = (WeakEntry) _gcEntryQueue.poll()) != null) {
            gcKeyList.add(gcEntry._key);
        }
        
        String[] removeKeys = gcKeyList.toArray(new String[0]);
        if (async) {
            asyncRemove(removeKeys);
        } else {
            remove(removeKeys);
        }
    }
    
    // ---- inner classes
    private static class WeakEntry extends WeakReference<Object> {
        
        private final String _key;
        
        private WeakEntry(String key, Object value, ReferenceQueue<Object> gcQueue) {
            super(value, gcQueue);
            _key = key;
        }
       
    }
    
}
