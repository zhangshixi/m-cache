package com.mcache.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.mcache.CasOperation;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;

/**
 * Oscache repository engine driver implementation.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 2/1/2012
 * @since 	JDK1.5
 */
public class OsCache extends AbstractCache {
	
	/** pool configuration file */
	private Properties _props;
	
	/** OSCache general administrator */
	private GeneralCacheAdministrator _cache;
	
	// ---- constructors --------------------------------------------------
	public OsCache(String id, URL url) throws IOException {
		this(id, url.openStream());
	}
	
	public OsCache(String id, File file) throws IOException {
		this(id, new FileInputStream(file));
	}
	
	public OsCache(String id, String classpath) throws IOException {
		this(id, OsCache.class.getResourceAsStream(classpath));
	}
	
	public OsCache(String id, InputStream input) throws IOException {
		super(id);
		_props = new Properties();
		try {
			_props.load(input);
		} finally {
			input.close();
		}
	}
	
	public OsCache(String id, Properties props) {
		super(id);
		_props = props;
	}
	
	// ---- methods implementation --------------------------------------------------
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
            throw new IllegalArgumentException("expiredTime should non-positive: " + expiredTime);
        }
        
        _cache.putInCache(key, value, new ExpiresRefreshPolicy((int) expiredTime / 1000));
        
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
			getLogger().error(e.getMessage(), e);
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
	
	@Override
	public Future<Long> asyncIncrease(String key, long value) {
		return super.asyncIncrease(key, value);
	}

	@Override
	public Future<Long> asyncDecrease(String key, long value) {
		return super.asyncDecrease(key, value);
	}

}