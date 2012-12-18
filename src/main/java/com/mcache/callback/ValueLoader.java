package com.mcache.callback;

public interface ValueLoader<T> {
	
	public T loadValue(Object param);
	
}
