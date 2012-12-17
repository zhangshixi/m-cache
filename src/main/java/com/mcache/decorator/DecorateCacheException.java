package com.mcache.decorator;

/**
 * Decorate cache exception.
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 2/1/2012
 * @since 	JDK1.5
 */
public class DecorateCacheException extends RuntimeException {

	/** serial version id */
	private static final long serialVersionUID = -4415213834820780922L;

	public DecorateCacheException() {
		super();
	}

	public DecorateCacheException(String message) {
		super(message);
	}

	public DecorateCacheException(Throwable cause) {
		super(cause);
	}

	public DecorateCacheException(String message, Throwable cause) {
		super(message, cause);
	}

}
