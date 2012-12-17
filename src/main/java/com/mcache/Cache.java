package com.mcache;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * A common interface that provides operate cache in unity mode, it defines some 
 * common operations of cache, such as puts value to cache or gets value from cache.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 15/09/2011
 * @since 	JDK1.5
 * 
 * @see		com.mtoolkit.cache.CasOperation
 */
public interface Cache {

	/**
	 * Startup the cache engine with the specified properties configuration.
	 * 
	 */
	public void startup();

	/**
	 * Shutdown current cache engine.
	 */
	public void shutdown();
	
	/**
	 * Returns current cache driver id.
	 * 
	 * @return current cache driver id.
	 */
	public String getId();
	
	/**
	 * Tests whether this cache is initialized.
	 * 
	 * @return <code>true</code> if and only if this cache has initialized;
	 * 		   <code>false</code> otherwise.
	 */
	public boolean isInitialized();
	
	/**
	 * Tests whether this cache contains the specified key.
	 * 
	 * @param key cache key.
	 * 
	 * @return <code>true</code> if and only if this cache contains the key;
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @throws NullPointerException  if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public boolean containsKey(String key);

	/**
	 * Puts or updates the value into cache with the specified cache key.
	 * 
	 * @param key	cache key.
	 * @param value	cache value.
	 * 
	 * @return <code>true</code> if and only if put operation successful;
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public boolean put(String key, Object value);
	public Future<Boolean> asyncPut(String key, Object value);
	
	/**
	 * Puts or updates the value into cache with the specified cache key,
	 * it will expired at <code>expiredDate</code> time.
	 * 
	 * @param key		  cache key.
	 * @param value		  cache value.
	 * @param expiredDate cache value expired date time.
	 * 
	 * @return <code>true</code> if and only if put operation successful;
	 * 		   <code>false</code> otherwise.
	 * 
	 * @throws NullPointerException		
	 * 		   if the <code>key</code> or <code>expiredDate</code> is null.
	 * @throws IllegalArgumentException 
	 * 		   if the <code>expiredDate</code> is not after current time.
	 * @throws IllegalStateException	
	 * 		   if current cache engine is not started.
	 */
	public boolean put(String key, Object value, Date expiredDate);
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate);

	/**
	 * Puts or updates the value into cache with the specified cache key,
	 * it will expired after <code>expiredTime</code> milliseconds.
	 * 
	 * @param key		  cache key.
	 * @param value		  cache value.
	 * @param expiredTime cache value expired time.
	 * 
	 * @return <code>true</code> if and only if put operation successful;
	 * 		   <code>false</code> otherwise.
	 * 
	 * @throws NullPointerException		if the <code>key</code> is null.
	 * @throws IllegalArgumentException if the <code>expiredTime</code> is non-positive.
	 * @throws IllegalStateException	if current cache engine is not started.
	 */
	public boolean put(String key, Object value, long expiredTime);
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime);

	/**
	 * Puts or updates the value into cache with the specified cache key,
	 * if occurs concurrency conflict whiling updating, it will invokes 
	 * {@linkplain CasOperation}} to try update cache value again.
	 * 
	 * @param key		cache key.
	 * @param value		cache value.
	 * @param operation cas update operation.
	 * 
	 * @return <code>true</code> if and only if put operation successful;
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public boolean put(String key, Object value, CasOperation<Object> operation);
	public Future<Boolean> asyncPut(String key, Object value, CasOperation<Object> operation);
	
	/**
	 * Puts or updates the value into cache with the specified cache key,
	 * it will expired at <code>expiredDate</code> time.
	 * if occurs concurrency conflict whiling updating, it will invokes 
	 * {@linkplain CasOperation}} to try update cache value again.
	 * 
	 * @param key		cache key.
	 * @param value		cache value.
	 * @param operation cas update operation.
	 * 
	 * @return <code>true</code> if and only if put operation successful;
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @throws NullPointerException		
	 * 		   if the <code>key</code> or <code>expiredDate</code> is null.
	 * @throws IllegalArgumentException 
	 * 		   if the <code>expiredDate</code> is not after current time.
	 * @throws IllegalStateException	
	 * 		   if current cache engine is not started.
	 */
	public boolean put(String key, Object value, Date expiredDate, CasOperation<Object> operation);
	public Future<Boolean> asyncPut(String key, Object value, Date expiredDate, CasOperation<Object> operation);

	/**
	 * Puts or updates the value into cache with the specified cache key,
	 * it will expired after <code>expiredTime</code> milliseconds.
	 * if occurs concurrency conflict whiling updating, it will invokes 
	 * {@linkplain CasOperation}} to try update cache value again.
	 * 
	 * @param key		cache key.
	 * @param value		cache value.
	 * @param operation cas update operation.
	 * 
	 * @return <code>true</code> if and only if put operation successful;
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @throws NullPointerException		if the <code>key</code> is null.
	 * @throws IllegalArgumentException if the <code>expiredTime</code> is non-positive.
	 * @throws IllegalStateException	if current cache engine is not started.
	 */
	public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation);
	public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation);

	/**
	 * Gets cache value with the specified cache key.
	 * 
	 * @param key cache key.
	 * 
	 * @return cache value with the specified cache key.
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public <T> T get(String key);

	/**
	 * Gets cache values map with the specified cache key array.
	 * 
	 * @param keys cache key array.
	 * 
	 * @return cache values map with the specified cache key array.
	 * 
	 * @throws NullPointerException	 if the <code>keys</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public <T> Map<String, T> get(String[] keys);

	/**
	 * Removes cache value with the specified cache key.
	 * 
	 * @param key remove cache key.
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 * 
	 * @return <code>true</code> if and only if remove operation successful;
     *         <code>false</code> otherwise. 
	 */
	public <T> T remove(String key);
	public <T> Future<T> asyncRemove(String key);

	/**
	 * Removes cache values with the specified cache key array.
	 * 
	 * @param keys remove cache key array.
	 * 
	 * @throws NullPointerException	 if the <code>keys</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 * 
	 * @return future result list;
	 *         <code>true</code> if and only if remove operation successful;
     *         <code>false</code> otherwise. 
	 */
	public <T> List<T> removes(String[] keys);
	public <T> Future<List<T>> asyncRemoves(String[] keys);

	/**
	 * Clears cache.
	 * 
	 * @return <code>true</code> if and only if clear operation successful;
     *         <code>false</code> otherwise. 
	 */
	public boolean clear();
	public Future<Boolean> asyncClear();
	
	/**
	 * Gets the number value with the specified key.
	 * 
	 * @param  key cache key.
	 * 
	 * @return cache number value;
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public long getNumber(String key);
	
	/**
	 * Increases the specified value into cache with the specified key.
	 * 
	 * @param  key cache key.
	 * @param  value increment value.
	 * 
	 * @return cache value after incremented with the specified key.
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public long increase(String key, long value);
	public Future<Long> asyncIncrease(String key, long value);
	
	/**
	 * Decreases the specified value into cache with the specified key.
	 * 
	 * @param  key cache key.
	 * @param  value decrement value.
	 * 
	 * @return cache value after decrement with the specified key.
	 * 
	 * @throws NullPointerException	 if the <code>key</code> is null.
	 * @throws IllegalStateException if current cache engine is not started.
	 */
	public long decrease(String key, long value);
	public Future<Long> asyncDecrease(String key, long value);
	
}
