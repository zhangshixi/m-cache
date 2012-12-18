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
			LOGGER.warn("Cache[{0}] has been initialized.", getId());
		}
		
		getDelegateCache().startup();
		
		LOGGER.info("Cache[{0}] startup succeed.", getId());
	}
	
	@Override
	public void shutdown() {
		if (!isInitialized()) {
			LOGGER.warn("Cache[{0}] has not been initialized.", getId());
		}
		
		getDelegateCache().shutdown();
		
		LOGGER.info("Cache[{0}] shutdown succeed.", getId());
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
		LOGGER.debug("Test whether cache contains key={0}, result={1}", 
				key, Boolean.valueOf(result));
		return result;
	}

	@Override
	public boolean put(String key, Object value) {
		boolean result = getDelegateCache().put(key, value);
		LOGGER.debug("Put value={0} into cache with key={1}, result={2}.", 
				value, key, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value);
		LOGGER.debug("Async put value={0} into cache with key={1}.", value, key);
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, long expiredTime) {
		boolean result = getDelegateCache().put(key, value, expiredTime);
		LOGGER.debug("Put value={0} into cache with key={1} expiredTime={2}, result={3}.", 
				value, key, Long.valueOf(expiredTime), Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredTime);
		LOGGER.debug("Async put value={0} into cache with key={1} expiredTime={2}.", 
				value, key, Long.valueOf(expiredTime));
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate) {
		boolean result = getDelegateCache().put(key, value, expiredDate);
		LOGGER.debug("Put value={0} into cache with key={1} expiredDate={2}, result={3}.", 
				value, key, dateFormat(expiredDate), Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredDate);
		LOGGER.debug("Async put value={0} into cache with key={1} expiredDate={2}.", 
				value, key, dateFormat(expiredDate));
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, CasOperation<Object> operation) {
		boolean result = getDelegateCache().put(key, value, operation);
		LOGGER.debug("Put value={0} into cache with key={1} casOperation={2}, result={3}.", 
				value, key, operation, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, operation);
		LOGGER.debug("Async put value={0} into cache with key={1} casOperation={2}.", 
				value, key, operation);
		return result;
	}

	@Override
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		boolean result = getDelegateCache().put(key, value, expiredTime, operation);
		LOGGER.debug("Put value={0} into cache with key={1} expiredTime={2} casOperation={3}, result={4}.", 
				value, key, Long.valueOf(expiredTime), operation, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredTime, operation);
		LOGGER.debug("Async put value={0} into cache with key={1} expiredTime={2} casOperation={3}.", 
				value, key, Long.valueOf(expiredTime), operation);
		return result;
	}
	
	@Override
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		boolean result = getDelegateCache().put(key, value, expiredDate, operation);
		LOGGER.debug("Put value={0} into cache with key={1} expiredDate={2} casOperation={3}, result={4}.", 
				value, key, dateFormat(expiredDate), operation, Boolean.valueOf(result));
		return result;
	}
	
	@Override
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation) {
		Future<Boolean> result = getDelegateCache().asyncPut(key, value, expiredDate, operation);
		LOGGER.debug("Async put value={0} into cache with key={1} expiredDate={2} casOperation={3}.", 
				value, key, dateFormat(expiredDate), operation);
		return result;
	}

	@Override
	public <T> T get(String key) {
		T result =  getDelegateCache().get(key);
		LOGGER.debug("Get value from cache with key={0}, result={1}.", key, result);
		return result;
	}

	@Override
	public <T> Map<String, T> get(String[] keys) {
		Map<String, T> results = getDelegateCache().get(keys);
		LOGGER.debug("Batch get values with keys={0}, results={1}.", Arrays.toString(keys), results);
		return results;
	}

	@Override
	public <T> T remove(String key) {
		T result = getDelegateCache().remove(key);
		LOGGER.debug("Remove value from cache with key={0}, result={1}.", key, result);
		return result;
	}
	
	@Override
	public <T> Future<T> asyncRemove(String key) {
		Future<T> result =  getDelegateCache().asyncRemove(key);
		LOGGER.debug("Async remove value from cache with key={0}.", key);
		return result;
	}

	@Override
	public <T> List<T> remove(String[] keys) {
		List<T> results = getDelegateCache().remove(keys);
		LOGGER.debug("Batch remove values from cache with keys={0}, results={1}.", Arrays.toString(keys), results);
		return results;
	}
	
	@Override
	public <T> Future<List<T>> asyncRemove(String[] keys) {
		Future<List<T>> results = getDelegateCache().asyncRemove(keys);
		LOGGER.debug("Async batch remove values from cache with keys={0}.", Arrays.toString(keys));
		return results;
	}

	@Override
	public boolean clear() {
		boolean result = getDelegateCache().clear();
		LOGGER.debug("Clear cache, result={0}.", result);
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
		LOGGER.debug("Get number from cache with key={0}, result={1}.", key, result);
		return result;
	}
	
	@Override
	public long increase(String key, long value) {
		long result = getDelegateCache().increase(key, value);
		LOGGER.debug("Increase value={0} into cache with key={1}, result={2}.", 
				Long.valueOf(value), key, result);
		return result;
	}
	
	@Override
	public Future<Long> asyncIncrease(String key, long value) {
		Future<Long> result = getDelegateCache().asyncIncrease(key, value);
		LOGGER.debug("Async increase value={0} into cache with key={1}", 
				Long.valueOf(value), key);
		return result;
	}

	@Override
	public long decrease(String key, long value) {
		long result = getDelegateCache().decrease(key, value);
		LOGGER.debug("Decrease value={0} into cache with key={1}, result={2}.", 
				Long.valueOf(value), key, result);
		return result;
	}
	
	@Override
	public Future<Long> asyncDecrease(String key, long value) {
		Future<Long> result = getDelegateCache().asyncDecrease(key, value);
		LOGGER.debug("Async decrease value={0} into cache with key={1}", 
				Long.valueOf(value), key);
		return result;
	}
	
	private String dateFormat(Date date) {
		return DateUtil.getDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
	}
	
}
