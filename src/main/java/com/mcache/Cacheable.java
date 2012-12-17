package com.mcache;

/**
 * A <code>Cacheable</code> is a source that can be cached.
 * The <code>setCacheDriver</code> method is invoked to set 
 * a cache driver instance that resources to repository.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 15/09/2011
 * @since 	JDK1.5
 * 
 * @see		com.mcache.Cache
 */
public interface Cacheable {

	/**
	 * Sets the specified cache repository engine.
	 * 
	 * @param cache cache repository engine.
	 */
	public void setCacheDriver(Cache cache);

}
