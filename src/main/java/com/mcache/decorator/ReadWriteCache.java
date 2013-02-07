package com.mcache.decorator;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.mcache.Cache;
import com.mcache.CasOperation;

public class ReadWriteCache extends CacheDecorator {

    private final ReadWriteLock _readWriteLock;
    
    public ReadWriteCache(Cache cache) {
        this(cache, new ReentrantReadWriteLock());
    }
    
    public ReadWriteCache(Cache cache, ReadWriteLock readWriteLock) {
        super(cache);
        if (readWriteLock == null) {
            throw new NullPointerException("readWriteLock");
        }
        _readWriteLock = readWriteLock;
    }
    
    @Override
    public void startup() {
        lockWrite();
        try {
            getDelegateCache().startup();
        } finally {
            unLockWrite();
        }
    }

    @Override
    public void shutdown() {
        lockWrite();
        try {
            getDelegateCache().shutdown();
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public String getId() {
        lockRead();
        try {
            return getDelegateCache().getId();
        } finally {
            unLockRead();
        }
    }

    @Override
    public boolean isInitialized() {
        lockRead();
        try {
            return getDelegateCache().isInitialized();
        } finally {
            unLockRead();
        }
    }
    
    @Override
    public boolean containsKey(String key) {
        lockRead();
        try {
            return getDelegateCache().containsKey(key);
        } finally {
            unLockRead();
        }
    }

    @Override
    public boolean put(String key, Object value) {
        lockWrite();
        try {
            return getDelegateCache().put(key, value);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value) {
        lockWrite();
        try {
            return getDelegateCache().asyncPut(key, value);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public boolean put(String key, Object value, long expiredTime) {
        lockWrite();
        try {
            return getDelegateCache().put(key, value, expiredTime);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
        lockWrite();
        try {
            return getDelegateCache().asyncPut(key, value, expiredTime);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate) {
        lockWrite();
        try {
            return getDelegateCache().put(key, value, expiredDate);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
        lockWrite();
        try {
            return getDelegateCache().asyncPut(key, value, expiredDate);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public boolean put(String key, Object value, CasOperation<Object> operation) {
        lockWrite();
        try {
            return getDelegateCache().put(key, value, operation);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
        lockWrite();
        try {
            return getDelegateCache().asyncPut(key, value, operation);
        } finally {
            unLockWrite();
        }
    }

    @Override
    public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        lockWrite();
        try {
            return getDelegateCache().put(key, value, expiredTime, operation);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        lockWrite();
        try {
            return getDelegateCache().asyncPut(key, value, expiredTime, operation);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        lockWrite();
        try {
            return getDelegateCache().put(key, value, expiredDate, operation);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
        lockWrite();
        try {
            return getDelegateCache().asyncPut(key, value, expiredDate, operation);
        } finally {
            unLockWrite();
        }
    }

    @Override
    public <T> T get(String key) {
        lockRead();
        try {
            return getDelegateCache().get(key);
        } finally {
            unLockRead();
        }
    }

    @Override
    public <T> Map<String, T> get(String[] keys) {
        lockRead();
        try {
            return getDelegateCache().get(keys);
        } finally {
            unLockRead();
        }
    }

    @Override
    public <T> T remove(String key) {
        lockWrite();
        try {
            return getDelegateCache().remove(key);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public <T> Future<T> asyncRemove(String key) {
        lockWrite();
        try {
            return getDelegateCache().asyncRemove(key);
        } finally {
            unLockWrite();
        }
    }

    @Override
    public <T> List<T> remove(String[] keys) {
        lockWrite();
        try {
            return getDelegateCache().remove(keys);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public <T> Future<List<T>> asyncRemove(String[] keys) {
        lockWrite();
        try {
            return getDelegateCache().asyncRemove(keys);
        } finally {
            unLockWrite();
        }
    }

    @Override
    public boolean clear() {
        lockWrite();
        try {
            return getDelegateCache().clear();
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Boolean> asyncClear() {
        lockWrite();
        try {
            return getDelegateCache().asyncClear();
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public long getNumber(String key) {
        lockRead();
        try {
            return getDelegateCache().getNumber(key);
        } finally {
            unLockRead();
        }
    }
    
    @Override
    public long increase(String key, long value) {
        lockWrite();
        try {
            return getDelegateCache().increase(key, value);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Long> asyncIncrease(String key, long value) {
        lockWrite();
        try {
            return getDelegateCache().asyncIncrease(key, value);
        } finally {
            unLockWrite();
        }
    }

    @Override
    public long decrease(String key, long value) {
        lockWrite();
        try {
            return getDelegateCache().decrease(key, value);
        } finally {
            unLockWrite();
        }
    }
    
    @Override
    public Future<Long> asyncDecrease(String key, long value) {
        lockWrite();
        try {
            return getDelegateCache().asyncDecrease(key, value);
        } finally {
            unLockWrite();
        }
    }

    // ---- private methods
    private void lockRead() {
        _readWriteLock.readLock().lock();
    }
    
    private void unLockRead() {
        _readWriteLock.readLock().unlock();
    }
    
    private void lockWrite() {
        _readWriteLock.writeLock().lock();
    }
    
    private void unLockWrite() {
        _readWriteLock.writeLock().unlock();
    }
    
}