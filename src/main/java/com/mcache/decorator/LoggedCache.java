package com.mcache.decorator;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.mcache.Cache;
import com.mcache.CasOperation;
import com.mlogger.Loggers;
import com.mtoolkit.util.DateUtil;

public class LoggedCache extends CacheDecorator {

	private static final Loggers LOGGER = Loggers.getLoggers(Cache.class);
	
	public LoggedCache(Cache cache) {
		super(cache);
	}
	
	@Override
	public void startup() {
		if (isInitialized()) {
			LOGGER.warn("Cache:[id={0}] has been initialized.", getId());
		}
		
		getDelegateCache().startup();
		
		LOGGER.info("Cache:[id={0}] startup succeed.", getId());
	}
	
	@Override
	public void shutdown() {
		if (!isInitialized()) {
			LOGGER.warn("Cache:[id={0}] has not been initialized.", getId());
		}
		
		getDelegateCache().shutdown();
		
		LOGGER.info("Cache:[id={0}] shutdown succeed.", getId());
	}
	
	@Override
	public String getId() {
		return getDelegateCache().getId();
	}

	@Override
	public boolean isInitialized() {
		return getDelegateCache().isInitialized();
	}
	
	@Override
	public boolean containsKey(String key) {
		boolean result = getDelegateCache().containsKey(key);
		LOGGER.debug("Cache contains key test - Argument:[key={0}] - Result:[{1}].", 
		    key, Boolean.valueOf(result));
		return result;
	}

	@Override
	public boolean put(String key, Object value) {
		boolean result = getDelegateCache().put(key, value);
		LOGGER.debug("Put value into cache - Argument:[key={0}, value={1}] - Result:[{2}].", 
		    key, value, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value);
		LOGGER.debug("Async put value into cache - Argument:[key={0}, value={1}].", 
		    key, value);
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime) {
		boolean result = getDelegateCache().put(key, value, expiredTime);
		LOGGER.debug("Put value into cache - Argument:[key={0}, value={1}, expiredTime={2}] - Result:[{3}].", 
		    key, value, Long.valueOf(expiredTime), Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredTime);
		LOGGER.debug("Async put value into cache - Argument:[key={0}, value={1}, expiredTime[{2}].", 
		    key, value, Long.valueOf(expiredTime));
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		boolean result = getDelegateCache().put(key, value, expiredDate);
		LOGGER.debug("Put value into cache - Argument:[key={0}, value={1}, expiredDate={2}] - Result:[{3}].", 
		    key, value, dateFormat(expiredDate), Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredDate);
		LOGGER.debug("Async put value into cache - Argument:[key={0}, value={1}, expiredDate={2}].", 
		    key, value, dateFormat(expiredDate));
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		boolean result = getDelegateCache().put(key, value, operation);
		LOGGER.debug("Put value into cache - Argument:[key={0}, value={1}, casOperation={2}] - Result:[{3}].", 
		    key, value, operation, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, operation);
		LOGGER.debug("Async put value into cache - Argument:[key={0}, value={1}, casOperation={2}].", 
		    key, value, operation);
		return result;
	}

	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		boolean result = getDelegateCache().put(key, value, expiredTime, operation);
		LOGGER.debug("Put value into cache - Argument:[key={0}, value={1}, expiredTime={2}, casOperation={3}] - Result:[{4}].", 
			key, value, Long.valueOf(expiredTime), operation, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredTime, operation);
		LOGGER.debug("Async put value into cache - Argument:[key={0}, value={1}, expiredTime={2}, casOperation={3}].", 
			key, value, Long.valueOf(expiredTime), operation);
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		boolean result = getDelegateCache().put(key, value, expiredDate, operation);
		LOGGER.debug("Put value into cache - Argument:[key={0}, value={1}, expiredDate={2}, casOperation={3}] - Result:[{4}].", 
			key, value, dateFormat(expiredDate), operation, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredDate, operation);
		LOGGER.debug("Async put value into cache - Argument:[key={0}, value={1}, expiredDate={2}, casOperation={3}].", 
		    key, value, dateFormat(expiredDate), operation);
		return result;
	}

	@Override
	public <T> T get(String key) {
		T result =  getDelegateCache().get(key);
		LOGGER.debug("Get value from cache - Argument:[key={0}] - Result:[{1}].", 
		    key, result);
		return result;
	}

	@Override
	public <T> Map<String, T> get(String[] keys) {
		Map<String, T> results = getDelegateCache().get(keys);
		LOGGER.debug("Batch get value from cache - Argument:[key={0}] - Result:[{1}].", 
		    Arrays.toString(keys), results);
		return results;
	}

	@Override
	public <T> T remove(String key) {
		T result = getDelegateCache().remove(key);
		LOGGER.debug("Remove value from cache - Argument:[key={0}] - Result:[{1}].", 
		    key, result);
		return result;
	}
	
	@Override
	public <T> Future<T> asyncRemove(String key) {
		Future<T> result =  getDelegateCache().asyncRemove(key);
		LOGGER.debug("Async remove value from cache - Argument:[key={0}].", 
		    key);
		return result;
	}

	@Override
	public <T> List<T> remove(String[] keys) {
		List<T> results = getDelegateCache().remove(keys);
		LOGGER.debug("Batch remove value from cache - Argument:[key={0}] - Result:[{1}].", 
		    Arrays.toString(keys), results);
		return results;
	}
	
	@Override
	public <T> Future<List<T>> asyncRemove(String[] keys) {
		Future<List<T>> results = getDelegateCache().asyncRemove(keys);
		LOGGER.debug("Async batch remove value from cache - Argument:[key={0}].", 
		    Arrays.toString(keys));
		return results;
	}

	@Override
	public boolean clear() {
		boolean result = getDelegateCache().clear();
		LOGGER.debug("Clear cache - Result:[{0}].", 
		    result);
		return result;
	}
	
	@Override
	public Future<Boolean> asyncClear() {
		Future<Boolean> result = getDelegateCache().asyncClear();
		LOGGER.debug("Async clear cache.");
		return result;
	}
	
	@Override
	public long getNumber(String key) {
		long result = getDelegateCache().getNumber(key);
		LOGGER.debug("Get number from cache - Argument:[key={0}] - Result:[{1}].", 
		    key, Long.valueOf(result));
		return result;
	}
	
	@Override
	public long increase(String key, long value) {
		long result = getDelegateCache().increase(key, value);
		LOGGER.debug("Increase number into cache - Argument:[key={0}, value={1}] - Result:[{2}].", 
		    key, Long.valueOf(value), result);
		return result;
	}
	
	@Override
	public Future<Long> asyncIncrease(String key, long value) {
		Future<Long> result = getDelegateCache().asyncIncrease(key, value);
		LOGGER.debug("Async increase number into cache - Argument:[key={0}, value={1}].", 
		    key, Long.valueOf(value));
		return result;
	}

	@Override
	public long decrease(String key, long value) {
		long result = getDelegateCache().decrease(key, value);
		LOGGER.debug("Decrease number into cache - Argument:[key={0}, value={1}] - Result:[{2}].", 
		    key, Long.valueOf(value), result);
		return result;
	}
	
	@Override
	public Future<Long> asyncDecrease(String key, long value) {
		Future<Long> result = getDelegateCache().asyncDecrease(key, value);
		LOGGER.debug("Async decrease number into cache - Argument:[key={0}, value={1}].", 
		    key, Long.valueOf(value));
		return result;
	}
	
	// ---- private classes
	private String dateFormat(Date date) {
		return DateUtil.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
	}
	
}
