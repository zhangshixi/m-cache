package com.mcache.callback;

import java.util.Map;

public interface KeyBatchGenerator<T> {
    
    public Map<String, T> generateKeys(T[] params);
    
}
