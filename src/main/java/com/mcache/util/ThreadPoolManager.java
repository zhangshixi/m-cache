package com.mcache.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.mlogger.Loggers;

/**
 * Thread pool manager.
 */
public class ThreadPoolManager {
    
    private ExecutorService _threadPool;
    private volatile boolean _initialized;
    
    public static final int DEFAULT_THREAD_NUMBER = 1;
    
    private static final Loggers LOGGER = Loggers.getLoggers(ThreadPoolManager.class);
    
    public ThreadPoolManager() {
        this(DEFAULT_THREAD_NUMBER);
    }
    
    public ThreadPoolManager(int threadNumber) {
        chechThreadNumber(threadNumber);
        doInitThreadPool(threadNumber);
    }
    
    public boolean isInitialized() {
        return _initialized;
    }
    
    public boolean isTerminated() {
        return _threadPool.isTerminated();
    }
    
    public Future<?> submit(Runnable task) {
        checkThreadPoolStatus();
        return _threadPool.submit(task);
    }
    
    public <T> Future<T> submit(Callable<T> task) {
        checkThreadPoolStatus();
        return _threadPool.submit(task);
    }

    public void resize(int threadNumber) {
        chechThreadNumber(threadNumber);
        destroy();
        doInitThreadPool(threadNumber);
    }
    
    public void destroy() {
        if (!isInitialized()) {
            LOGGER.warn("ThreadPoolManager has not initialized.");
            return;
        }
        
        _threadPool.shutdown();
        try {
            if (!_threadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                _threadPool.shutdownNow();
                
                if (!_threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    LOGGER.error("Could not terminate thread pool manager!");
                }
            }
        } catch (InterruptedException e) {
            _threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        _initialized = false;
        
        LOGGER.info("ThreadPoolManager has destroyed.");
    }
    
    private void checkThreadPoolStatus() {
        if (!isInitialized()) {
            throw new IllegalStateException("ThreadPoolManager is not initialized.");
        }
    }

    private void doInitThreadPool(int threadNumber) {
        _threadPool = Executors.newFixedThreadPool(threadNumber);
        _initialized = true;
        
        LOGGER.info("ThreadPoolManager has initialized with [{0}] thread number.", threadNumber);
    }
    
    private void chechThreadNumber(int threadNumber) {
        if (threadNumber <= 0) {
            throw new IllegalArgumentException("Thread number must be a positive number: " + threadNumber);
        }
    }
    
}