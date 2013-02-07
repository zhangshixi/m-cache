package com.mcache.support;

import java.util.concurrent.Future;

import com.mcache.CasOperation;

public class EhCachedCache extends AbstractCache {
	
	public EhCachedCache(String id) {
		super(id);
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
