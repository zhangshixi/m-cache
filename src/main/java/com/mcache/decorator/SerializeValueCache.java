package com.mcache.decorator;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;
import com.mtoolkit.serializer.Serializer;
import com.mtoolkit.serializer.support.JdkSerializer;

public class SerializeValueCache extends CacheDecorator {

	private Serializer _serializer;
	
    public SerializeValueCache(Cache cache) {
        this(cache, new JdkSerializer());
    }
    
    public SerializeValueCache(Cache cache, Serializer serializer) {
    	super(cache);
    	_serializer = serializer;
    }

	@Override
	public boolean put(String key, Object value) {
		return getDelegateCache().put(key, serialize(value));
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value) {
		return getDelegateCache().asyncPut(key, serialize(value));
	}

	@Override
	public boolean put(String key, Object value, long expiredTime) {
		return getDelegateCache().put(key, serialize(value), expiredTime);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		return getDelegateCache().asyncPut(key, serialize(value), expiredTime);
	}

	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		return getDelegateCache().put(key, serialize(value), expiredDate);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		return getDelegateCache().asyncPut(key, serialize(value), expiredDate);
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		return getDelegateCache().put(key, serialize(value), operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		return getDelegateCache().asyncPut(key, serialize(value), operation);
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		return getDelegateCache().put(key, serialize(value), expiredTime, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		return getDelegateCache().asyncPut(key, serialize(value), expiredTime, operation);
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return getDelegateCache().put(key, serialize(value), expiredDate, operation);
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		return getDelegateCache().asyncPut(key, serialize(value), expiredDate, operation);
	}
	
	@Override
	public <T> T get(String key) {
		return deserialize((byte[]) getDelegateCache().get(key));
	}

	@Override
	public <T> Map<String, T> get(String[] keys) {
		if (keys == null || keys.length == 0) {
			return getDelegateCache().get(keys);
		}

		Map<String, byte[]> values = getDelegateCache().get(keys);
		if (values == null || values.isEmpty()) {
			return Collections.emptyMap();
		}

		final Map<String, T> resultMap = new HashMap<String, T>(values.size(), 1F);
		T value = null;
		for (Entry<String, byte[]> entry : values.entrySet()) {
			value = deserialize((byte[]) entry.getValue());
			resultMap.put(entry.getKey(), value);
		}

		return Collections.unmodifiableMap(resultMap);
	}

    // ---- private methods ------------------------------------------------------------------------------
    private byte[] serialize(Object target) {
    	try {
			return _serializer.serialize(target);
		} catch (IOException e) {
			throw new DecorateCacheException("Serialize object to byte array exception", e);
		}
    }
    
    private <T> T deserialize(byte[] bytes) {
    	try {
			return _serializer.deserialize(bytes);
    	} catch (IOException e) {
    		throw new DecorateCacheException("Desrialize byte array to object exception", e);
		} catch (ClassNotFoundException e) {
			throw new DecorateCacheException("Desrialize not found target object class exception", e);
		}
    }
    
}
