package com.mcache.decorator;

import com.mcache.Cache;

public class WeakCache extends CacheDecorator {

    public WeakCache(Cache cache) {
        super(cache);
    }
    
}
