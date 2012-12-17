/**
 * f-club.cn
 * Copyright (c) 2009-2012 All Rights Reserved.
 */
package com.mcache;

/**
 * Cache driver runtime exception.
 * 
 * @author michael
 * @version $Id: CacheDriverException.java, v 0.1 2012-10-24 下午3:00:21 michael Exp $
 */
public class CacheException extends RuntimeException {

    /** serial version UID */
    private static final long serialVersionUID = 3199166308582871699L;

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
    
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
