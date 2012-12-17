package com.mcache.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.mcache.Cache;
import com.mcache.CasOperation;
import com.mcache.util.ThreadPoolManager;
import com.mlogger.Loggers;

public abstract class AbstractCache implements Cache {

	/** cache id flag */
	private final String _id;
	/** cache initialize flag */
	private volatile boolean _initialized;
	/** thread pool manager */
	private ThreadPoolManager _threadPoolManager;
	/** thread pool size */
    private int _threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
    
    /** default thread pool size */
    protected static final int DEFAULT_THREAD_POOL_SIZE = 10;
    /** default expired time, in milliseconds */
    private static final long DEFAULT_EXPIRED_TIME = Integer.MAX_VALUE * 1000L;
    
    private static final Loggers LOGGER = Loggers.getLoggers(Cache.class);
    
	public AbstractCache(String id) {
		_id = id;
	}
	
	/**
	 * Initialize the cache driver.
	 */
	protected abstract void doInitialize();
	
	/**
	 * Destroy current cache driver.
	 */
	protected abstract void doDestroy();
	
	@Override
	public void startup() {
		if (!_initialized) {
			doInitialize();
			_threadPoolManager = new ThreadPoolManager(getThreadPoolSize());
			_initialized = true;
		}
	}

	@Override
	public void shutdown() {
		if (_initialized) {
			doDestroy();
			_initialized = false;
		}
	}
	
	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public boolean isInitialized() {
		return _initialized;
	}

	@Override
	public boolean containsKey(String key) {
		checkStates();
		checkKey(key);
		if (key.isEmpty()) {
			return false;
		} else {
			return get(key) != null;
		}
		// maybe subclass need re-implemented
	}

	@Override
	public boolean put(String key, Object value) {
		return put(key, value, DEFAULT_EXPIRED_TIME);
		// maybe subclass need re-implemented
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value) {
		return asyncPut(key, value, DEFAULT_EXPIRED_TIME);
		// maybe subclass need re-implemented
	}

	@Override
	public boolean put(String key, Object value, Date expiredDate) {
	    return put(key, value, checkExpiredDate(expiredDate));
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		return asyncPut(key, value, checkExpiredDate(expiredDate));
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime) {
	    return put(key, value, expiredTime, null);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		return asyncPut(key, value, expiredTime, null);
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		return put(key, value, DEFAULT_EXPIRED_TIME, operation);
		// maybe subclass need re-implemented
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		return asyncPut(key, value, DEFAULT_EXPIRED_TIME, operation);
		// maybe subclass need re-implemented
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
	    return put(key, value, checkExpiredDate(expiredDate), operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return asyncPut(key, value, checkExpiredDate(expiredDate), operation);
	}
	
//	@Override
//	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
//	    return false;
//	}
	
	@Override
	public Future<Boolean> asyncPut(final String key, final Object value, final long expiredTime, final CasOperation<Object> operation) {
		return getThreadPoolManager().submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return Boolean.valueOf(put(key, value, expiredTime, operation));
			}
			
		});
		// maybe subclass need re-implemented
	}
	
//	@Override
//	public <T> T get(String key) {
//		return null;
//	}

	@Override
	public <T> Map<String, T> get(String[] keys) {
		checkStates();
		if (keys == null) {
			throw new NullPointerException("keys");
		} else if (keys.length == 0) {
			return Collections.emptyMap();
		}
		
		T value = null;
		final Map<String, T> resultMap = new HashMap<String, T>(keys.length, 1F);
		
		for (String key : keys) {
			if (key != null && !key.isEmpty()) {
				value = get(key);
				if (value != null) {
					resultMap.put(key, value);
				}
			}
		}
		
		return Collections.unmodifiableMap(resultMap);
	}

//	@Override
//	public <T> T remove(String key) {
//		return null;
//	}
	
	@Override
	public <T> Future<T> asyncRemove(final String key) {
		return getThreadPoolManager().submit(new Callable<T>() {

			@Override
			public T call() throws Exception {
				return remove(key);
			}
			
		});
		// maybe subclass need re-implemented
	}
	
	@Override
	public <T> List<T> removes(String[] keys) {
	    checkStates();
        if (keys == null) {
            throw new NullPointerException("keys");
        }
        if (keys.length == 0) {
            return Collections.emptyList();
        }
        
        final List<T> futureList = new ArrayList<T>(keys.length);
        T value = null;
        for (String key : keys) {
            if (key == null || key.isEmpty()) {
                continue;
            }
            value = remove(key);
            futureList.add(value);
        }
        
        return futureList;
	}
	
	@Override
	public <T> Future<List<T>> asyncRemoves(final String[] keys) {
		checkStates();
		return getThreadPoolManager().submit(new Callable<List<T>>() {

			@Override
			public List<T> call() throws Exception {
				return removes(keys);
			}
			
		});
		// maybe subclass need re-implemented
	}
	
	@Override
	public boolean clear() {
	    return false;
	    // maybe subclass need re-implemented
	}
	
	@Override
	public Future<Boolean> asyncClear() {
		return getThreadPoolManager().submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return Boolean.valueOf(clear());
			}
			
		});
		// maybe subclass need re-implemented
	}
	
	@Override
	public long getNumber(String key) {
		Object value = get(key);
		return value == null ? 0L : ((Long) value).longValue();
		// maybe subclass need re-implemented
	}

	@Override
	public long increase(final String key, final long value) {
        Long cacheValue = get(key);
        long oldValue = cacheValue == null ? 0L : cacheValue.longValue();
        
        Long newValue = Long.valueOf(oldValue + value);
        put(key, newValue);
        
        return newValue;
	    // maybe subclass need re-implemented
	}
	
	@Override
	public Future<Long> asyncIncrease(final String key, final long value) {
		Long cacheValue = get(key);
		long oldValue = cacheValue == null ? 0L : cacheValue.longValue();
		
		Long newValue = Long.valueOf(oldValue + value);
		asyncPut(key, newValue);
		
		return new SucceedFuture<Long>(newValue);
		// maybe subclass need re-implemented
	}
	
	@Override
	public long decrease(final String key, final long value) {
        Long cacheValue = get(key);
        long oldValue = cacheValue == null ? 0L : cacheValue.longValue();
        
        Long newValue = Long.valueOf(oldValue - value);
        put(key, newValue);
        
        return newValue;
	    // maybe subclass need re-implemented
	}
	
	@Override
	public Future<Long> asyncDecrease(final String key, final long value) {
		Long cacheValue = get(key);
		long oldValue = cacheValue == null ? 0L : cacheValue.longValue();
		
		Long newValue = Long.valueOf(oldValue - value);
		asyncPut(key, newValue);
		
		return new SucceedFuture<Long>(newValue);
		// maybe subclass need re-implemented
	}
	
	// ---- protected methods --------------------------------------------------------------
	protected int getThreadPoolSize() {
        return _threadPoolSize;
    }
    
    protected void setThreadPoolSize(int threadPoolSize) {
        if (threadPoolSize <= 0) {
            throw new IllegalArgumentException(
                "Thread pool size must be a positive number: " + threadPoolSize);
        }
        _threadPoolSize = threadPoolSize;
    }
    
	protected Loggers getLogger() {
		return LOGGER;
	}
	
	protected ThreadPoolManager getThreadPoolManager() {
	    return _threadPoolManager;
	}
	
	protected void checkStates() {
		if (!_initialized) {
			throw new IllegalStateException("Cache[" + _id + "] has not initialized.");
		}
	}
	
	protected void checkKey(String key) {
		if (key == null) {
			throw new NullPointerException("key");
		}
	}
	
    protected static Properties getProperties(InputStream input) throws IOException {
        final Properties props = new Properties();
        try {
            props.load(input);
        } finally {
            input.close();
        }
        return props;
    }
	
	// ---- private methods -------------------------------------------------------------------------------
	private long checkExpiredDate(Date expiredDate) {
		Date currentDate = Calendar.getInstance().getTime();
		if (expiredDate.before(currentDate)) {
			throw new IllegalArgumentException("ExpiredDate should not after current date: " + expiredDate);
		}

		return expiredDate.getTime() - currentDate.getTime();
	}

	// ---- inner classes ---------------------------------------------------------------------------------
	public static class SucceedFuture<T> implements Future<T> {
		
		public static SucceedFuture<Boolean> BOOLEAN_TRUE  = new SucceedFuture<Boolean>(Boolean.TRUE);
		public static SucceedFuture<Boolean> BOOLEAN_FALSE = new SucceedFuture<Boolean>(Boolean.FALSE);
		
		public static SucceedFuture<Long>    LONG_ZORE     = new SucceedFuture<Long>(Long.valueOf(0L));
		
		private T _value;
		
		public SucceedFuture(T value) {
			_value = value;
		}
		
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public T get() throws InterruptedException, ExecutionException {
			return _value;
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return _value;
		}
		
	}
}
