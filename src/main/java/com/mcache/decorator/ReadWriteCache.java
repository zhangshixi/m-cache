package com.mcache.decorator;

import com.mcache.Cache;

public class ReadWriteCache extends CacheDecorator {

    public ReadWriteCache(Cache cache) {
        super(cache);
    }
    

}
