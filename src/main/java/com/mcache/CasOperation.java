package com.mcache;

/**
 * Cache CAS operation.
 * 
 * @param	V cache value type.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 3/12/2012
 * @since 	JDK1.5
 */
public interface CasOperation<V> {
	
	/**
	 * Returns max try times when insert/update failed. 
	 * 
	 * @return max try times when insert/update failed.
	 */
    public int getMaxTries();

    /**
     * Returns new value when insert/update failed.
     * 
     * @param currentValue current cache value.
     * 
     * @return new value when insert/update failed.
     */
    public V getNewValue(V currentValue);
    
}