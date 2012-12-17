package com.mcache.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Future;

import com.mcache.CasOperation;

/**
 * Memcached repository engine implementation.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 3/12/2012
 * @since 	JDK1.5
 */
public class MemcachedCache extends AbstractCache {

	/** pool configuration file */
	private Properties _props;
	
	// ---- constructors ---------------------------------------------------------
	public MemcachedCache(String id, URL url) throws IOException {
		this(id, url.openStream());
	}
	
	public MemcachedCache(String id, File file) throws IOException {
		this(id, new FileInputStream(file));
	}
	
	public MemcachedCache(String id, String classpath) throws IOException {
		this(id, MemcachedCache.class.getResourceAsStream(classpath));
	}
	
	public MemcachedCache(String id, InputStream input) throws IOException {
		super(id);
		_props = new Properties();
		try {
			_props.load(input);
		} finally {
			input.close();
		}
	}
	
	public MemcachedCache(String id, Properties props) {
		super(id);
		_props = props;
	}

	@Override
	protected void doInitialize() {
		// TODO Auto-generated method stub
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

}
