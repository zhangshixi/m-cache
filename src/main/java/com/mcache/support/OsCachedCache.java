package com.mcache.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.mcache.CasOperation;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;

/**
 * Oscache repository engine driver implementation.
 */
public class OsCachedCache extends AbstractCache {
	
	/** pool configuration file */
	private Properties _props;
	/** OSCache general administrator */
	private GeneralCacheAdministrator _cache;
	
	private static final String DEF_CACHE_ID = OsCachedCache.class.getName();
	
	// ---- constructors
    public OsCachedCache(Properties props) {
        this(DEF_CACHE_ID ,props);
    }
    
    public OsCachedCache(String id, Properties props) {
        super(id);
        _props = props;
    }
    
    public OsCachedCache(String id, Properties props, int asyncThreadPoolSize) {
        super(id);
        _props = props;
        setAsyncThreadPoolSize(asyncThreadPoolSize);
    }
    
	public OsCachedCache(File file) throws IOException {
		this(DEF_CACHE_ID, new FileInputStream(file));
	}
	
	public OsCachedCache(String id, File file) throws IOException {
	    this(id, file, DEF_ASYNC_THREAD_POOL_SIZE);
	}
	
	public OsCachedCache(String id, File file, int asyncThreadPoolSize) throws IOException {
	    this(id, new FileInputStream(file), asyncThreadPoolSize);
	}
	
	public OsCachedCache(String classpath) throws IOException {
		this(DEF_CACHE_ID, OsCachedCache.class.getResourceAsStream(classpath));
	}
	
	public OsCachedCache(String id, String classpath) throws IOException {
	    this(id, classpath, DEF_ASYNC_THREAD_POOL_SIZE);
	}
	
	public OsCachedCache(String id, String classpath, int asyncThreadPoolSize) throws IOException {
	    this(id, OsCachedCache.class.getResourceAsStream(classpath), asyncThreadPoolSize);
	}
	
	public OsCachedCache(InputStream input) throws IOException {
		this(DEF_CACHE_ID, input);
	}
	
	public OsCachedCache(String id, InputStream input) throws IOException {
	    this(id, input, DEF_ASYNC_THREAD_POOL_SIZE);
	}
	
	public OsCachedCache(String id, InputStream input, int asyncThreadPoolSize) throws IOException {
	    this(id, getProperties(input), asyncThreadPoolSize);
	}
	
    // ---- implement methods
	@Override
	protected void doInitialize() {
		if (_props == null) {
			_cache = new GeneralCacheAdministrator();
		} else {
			_cache = new GeneralCacheAdministrator(_props);
		}
	}

	@Override
	protected void doDestroy() {
		_cache.destroy();
	}

	@Override
	public boolean put(String key, Object value) {
		checkStates();
		checkKey(key);
		if (key.isEmpty()) {
			return false;
		}

		_cache.putInCache(key, value);
		
		return true;
	}
	
	@Override
	public Future<Boolean> asyncPut(final String key, final Object value) {
		return getThreadPoolManager().submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return Boolean.valueOf(put(key, value));
			}
			
		});
	}
	
	@Override
    public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
	    checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return false;
        } else if (expiredTime <= 0) {
            throw new IllegalArgumentException("ExpiredTime should non-positive: " + expiredTime);
        }
        
        _cache.putInCache(key, value, new ExpiresRefreshPolicy((int) (expiredTime / 1000)));
        
        return true;
    }

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		checkStates();
		checkKey(key);
		
		try {
			return (T) _cache.getFromCache(key);
		} catch (NeedsRefreshException e) {
			_cache.cancelUpdate(key);
			return null;
		}
	}

	@Override
	public <T> T remove(String key) {
		checkStates();
		checkKey(key);
		if (key.isEmpty()) {
			return null;
		}
		
		T value = get(key);
		_cache.removeEntry(key);
		
		return value;
	}

	@Override
	public boolean clear() {
		checkStates();
		
		_cache.flushAll();
		
		return true;
	}

}