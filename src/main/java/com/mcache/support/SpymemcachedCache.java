package com.mcache.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

import com.mcache.CasOperation;

/**
 * Spymemcached client driver implementation.
 * <p>
 * See &lt;a htrf="http://code.google.com/p/spymemcached/"&gt;xmemcached&lt;/a&gt;
 */
public class SpymemcachedCache extends AbstractCache {

    /** pool configuration file */
    private Properties _props;
    
    public static final String DEF_CACHE_ID = SpymemcachedCache.class.getName();
    public static final String DEF_SPYMEMCACHED_CONFIG = "/spymemcached.properties";
    
    // ---- constructors
    public SpymemcachedCache(Properties props) {
        this(DEF_CACHE_ID, props);
    }
    
    public SpymemcachedCache(String id, Properties props) {
        this(id, props, DEF_ASYNC_THREAD_POOL_SIZE);
    }
    
    public SpymemcachedCache(String id, Properties props, int asyncThreadPoolSize) {
        super(id);
        _props = props;
        setAsyncThreadPoolSize(asyncThreadPoolSize);
    }
    
    public SpymemcachedCache(File file) throws IOException {
        this(DEF_CACHE_ID, file);
    }
    
    public SpymemcachedCache(String id, File file) throws IOException {
        this(id, file, DEF_ASYNC_THREAD_POOL_SIZE);
    }
    
    public SpymemcachedCache(String id, File file, int asyncThreadPoolSize) throws IOException {
        this(id, new FileInputStream(file), asyncThreadPoolSize);
    }
    
    public SpymemcachedCache(String classpath) throws IOException {
        this(DEF_CACHE_ID, classpath);
    }
    
    public SpymemcachedCache(String id, String classpath) throws IOException {
        this(id, classpath, DEF_ASYNC_THREAD_POOL_SIZE);
    }
    
    public SpymemcachedCache(String id, String classpath, int asyncThreadPoolSize) throws IOException {
        this(id, SpymemcachedCache.class.getResourceAsStream(classpath), asyncThreadPoolSize); // FIXME:
    }
    
    public SpymemcachedCache(InputStream input) throws IOException {
        this(DEF_CACHE_ID, input);
    }
    
    public SpymemcachedCache(String id, InputStream input) throws IOException {
        this(id, input, DEF_ASYNC_THREAD_POOL_SIZE);
    }
    
    public SpymemcachedCache(String id, InputStream input, int asyncThreadPoolSize) throws IOException {
        this(id, getProperties(input), asyncThreadPoolSize);
    }

    // ---- implement methods
	@Override
	protected void doInitialize() {
		// TODO Auto-generated method stub
	    _props.entrySet();
	}

	@Override
	protected void doDestroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T remove(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Future<T> asyncRemove(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ---- private methods
	
	// ---- inner classes

}
