package com.mcache.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.CommandFactory;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedSessionLocator;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import com.mcache.CacheException;
import com.mcache.CasOperation;
import com.mlogger.Loggers;

/**
 * XMemcached repository engine implementation.
 * <p>
 * See &lt;a htrf="http://code.google.com/p/xmemcached/"&gt;xmemcached&lt;/a&gt;
 * 
 * @author 	<a href="mailto:xishizhang@gmail.com">ZhangShixi</a>
 * @version 1.0, 3/12/2012
 * @since 	JDK1.5
 */
public class XMemcachedCache extends AbstractCache {

	/** pool configuration file */
	private Properties _props;
	/** xmemcached client driver */
    private MemcachedClient _cache;
    
    private Loggers LOGGER = Loggers.getLoggers(XMemcachedCache.class);
    
    public static final String DEF_CACHE_ID = XMemcachedCache.class.getName();
    public static final String DEF_XMEMCACHED_CONFIG = "/xmemcached.properties";
	
	// ---- constructors
    public XMemcachedCache(Properties props) {
        this(DEF_CACHE_ID, props);
    }
    
    public XMemcachedCache(String id, Properties props) {
        this(id, props, DEF_ASYNC_THREAD_POOL_SIZE);
    }
    
    public XMemcachedCache(String id, Properties props, int asyncThreadPoolSize) {
        super(id);
        _props = props;
        setAsyncThreadPoolSize(asyncThreadPoolSize);
    }
    
    public XMemcachedCache(File file) throws IOException {
        this(DEF_CACHE_ID, file);
    }
    
    public XMemcachedCache(String id, File file) throws IOException {
        this(id, file, DEF_ASYNC_THREAD_POOL_SIZE);
    }
    
    public XMemcachedCache(String id, File file, int asyncThreadPoolSize) throws IOException {
        this(id, new FileInputStream(file), asyncThreadPoolSize);
    }
    
	public XMemcachedCache(String classpath) throws IOException {
		this(DEF_CACHE_ID, classpath);
	}
	
	public XMemcachedCache(String id, String classpath) throws IOException {
	    this(id, classpath, DEF_ASYNC_THREAD_POOL_SIZE);
	}
	
	public XMemcachedCache(String id, String classpath, int asyncThreadPoolSize) throws IOException {
	    this(id, XMemcachedCache.class.getResourceAsStream(classpath), asyncThreadPoolSize); // FIXME:
	}
	
	public XMemcachedCache(InputStream input) throws IOException {
	    this(DEF_CACHE_ID, input);
	}
	
	public XMemcachedCache(String id, InputStream input) throws IOException {
	    this(id, input, DEF_ASYNC_THREAD_POOL_SIZE);
	}
	
	public XMemcachedCache(String id, InputStream input, int asyncThreadPoolSize) throws IOException {
	    this(id, getProperties(input), asyncThreadPoolSize);
	}
	
	// ---- implement methods
	@Override
	protected void doInitialize() {
	    Properties defaultProps = null;
	    try {
            defaultProps = getProperties(
                XMemcachedCache.class.getResource(DEF_XMEMCACHED_CONFIG).openStream());
        } catch (IOException e) {
            throw new IllegalArgumentException(
                "XMemcached could not load default configuration: " + DEF_XMEMCACHED_CONFIG, e);
        }
	    
	    final ConfigLoader config = new ConfigLoader().load(defaultProps).load(_props);
	    LOGGER.debug("XMemcachedCache configurations: \n{0}", config.toString());
	    
	    try {
            initXMemcachedClient(config);
        } catch (IOException e) {
            throw new CacheException("Startup XMemcached client driver error!", e);
        }
	}

	@Override
	protected void doDestroy() {
	    try {
            _cache.shutdown();
        } catch (IOException e) {
            throw new CacheException("Shutdown XMemcached client driver error!", e);
        }
	}
	
	@Override
    public boolean put(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return false;
        }
        
        if (operation == null) {
            try {
				_cache.set(key, (int) expiredTime, value);
			} catch (TimeoutException e) {
				throw new CacheException("XMemcached put operation timeout.", e);
            } catch (InterruptedException e) {
                throw new CacheException("XMemcached put operation thread interrupted.", e);
            } catch (MemcachedException e) {
                throw new CacheException("XMemcached exception.", e);
            }
        } else {
            try {
                _cache.cas(key, (int) expiredTime, new XMemcachedCASOperation(operation));
            } catch (TimeoutException e) {
                throw new CacheException("XMemcached cas put operation timeout.", e);
            } catch (InterruptedException e) {
                throw new CacheException("XMemcached cas put operation thread interrupted.", e);
            } catch (MemcachedException e) {
                throw new CacheException("XMemcached cas put operation exception.", e);
            }
        }

        return true;
    }
	
    @Override
    public Future<Boolean> asyncPut(String key, Object value, long expiredTime, CasOperation<Object> operation) {
        checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return SucceedFuture.BOOLEAN_FALSE;
        }
        
        if (operation == null) {
            try {
                _cache.setWithNoReply(key, (int) expiredTime, value);
            } catch (InterruptedException e) {
                throw new CacheException("XMemcached put operation thread interrupted.", e);
            } catch (MemcachedException e) {
                throw new CacheException("XMemcached exception.", e);
            }
        } else {
            try {
                _cache.casWithNoReply(key, (int) expiredTime, new XMemcachedCASOperation(operation));
            } catch (TimeoutException e) {
                throw new CacheException("XMemcached cas put operation timeout.", e);
            } catch (InterruptedException e) {
                throw new CacheException("XMemcached cas put operation thread interrupted.", e);
            } catch (MemcachedException e) {
                throw new CacheException("XMemcached cas put operation exception.", e);
            }
        }

        return SucceedFuture.BOOLEAN_TRUE;
    }

    @Override
    public <T> T get(String key) {
        checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return null;
        }
        
        try {
            return _cache.get(key);
        } catch (TimeoutException e) {
            throw new CacheException("XMemcached get operation timeout.", e);
        } catch (InterruptedException e) {
            throw new CacheException("XMemcached get operation thread interrupted.", e);
        } catch (MemcachedException e) {
            throw new CacheException("XMemcached get operation exception.", e);
        }
    }
    
    @Override
    public <T> Map<String, T> get(String[] keys) {
        checkStates();
        if (keys == null) {
            throw new NullPointerException("keys");
        } else if (keys.length == 0) {
            return Collections.emptyMap();
        }
        
        Map<String, T> resultMap = null;
        try {
            resultMap = _cache.get(Arrays.asList(keys));
        } catch (TimeoutException e) {
            throw new CacheException("XMemcached batch get operation timeout.", e);
        } catch (InterruptedException e) {
            throw new CacheException("XMemcached batch get operation thread interrupted.", e);
        } catch (MemcachedException e) {
            throw new CacheException("XMemcached batch get operation exception.", e);
        }
        
        return resultMap;
    }
    
    @Override
    public <T> T remove(String key) {
        checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return null;
        }
        
        T value = null;
        try {
        	value =_cache.get(key);
        	
			_cache.delete(key);
		} catch (TimeoutException e) {
			throw new CacheException("XMemcached delete operation timeout.", e);
        } catch (InterruptedException e) {
            throw new CacheException("XMemcached delete operation thread interrupted.", e);
        } catch (MemcachedException e) {
            throw new CacheException("XMemcached delete operation exception.", e);
        }
        
        return value;
    }

    @Override
    public boolean clear() {
        try {
            _cache.flushAll();
        } catch (TimeoutException e) {
            throw new CacheException("XMemcached clear operation timeout.", e);
        } catch (InterruptedException e) {
            throw new CacheException("XMemcached clear operation thread interrupted.", e);
        } catch (MemcachedException e) {
            throw new CacheException("XMemcached clear operation exception.", e);
        }
        
        return true;
    }
    
    @Override
    public long getNumber(String key) {
        String value = get(key);
        return value == null ? 0L : Long.parseLong(value);
    }

    @Override
    public long increase(String key, long value) {
        checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return 0L;
        }
        
        try {
            return _cache.incr(key, value, value);
        } catch (TimeoutException e) {
            throw new CacheException("XMemcached increase operation timeout.", e);
        } catch (InterruptedException e) {
            throw new CacheException("XMemcached increase operation thread interrupted.", e);
        } catch (MemcachedException e) {
            throw new CacheException("XMemcached increase operation exception.", e);
        }
    }

    @Override
    public long decrease(final String key, final long value) {
        checkStates();
        checkKey(key);
        if (key.isEmpty()) {
            return 0L;
        }
        
        try {
            return _cache.decr(key, value, (-1 * value));
        } catch (TimeoutException e) {
            throw new CacheException("XMemcached decrease operation timeout.", e);
        } catch (InterruptedException e) {
            throw new CacheException("XMemcached decrease operation thread interrupted.", e);
        } catch (MemcachedException e) {
            throw new CacheException("XMemcached decrease operation exception.", e);
        }
    }
    
    // ---- private methods
    private void initXMemcachedClient(ConfigLoader config) throws IOException {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
            config.getServers(), config.getWeigths());
        builder.setConnectTimeout(config.getConnectTimeout());
        builder.setConnectionPoolSize(config.getConnectionPoolSize());
        builder.setCommandFactory(config.getCommandFactory());
        builder.setSessionLocator(config.getSessionLocator());
        builder.setTranscoder(config.getTranscoder());
        builder.setFailureMode(config.isFailureMode());
        builder.setAuthInfoMap(config.getAuthInfoMap());
        
        _cache = builder.build();
        
        _cache.getTranscoder().setCompressionThreshold(config.getCompressionThreshold());
        _cache.getTranscoder().setPackZeros(config.isPackZeros());
        _cache.setSanitizeKeys(config.isSanitizeKeys());
    }
    
    
    // ---- inner classes
    private class XMemcachedCASOperation implements CASOperation<Object> {
        
        private CasOperation<Object> _operation;
        
        public XMemcachedCASOperation(CasOperation<Object> operation) {
            _operation = operation;
        }

        @Override
        public int getMaxTries() {
            return _operation.getMaxTries();
        }

        @Override
        public Object getNewValue(long currentCAS, Object currentValue) {
            return _operation.getNewValue(currentValue);
        }
        
    }
    
    public static final class ConfigLoader {
        
        public static final String PARAM_SERVERS               = "servers";
        public static final String PARAM_WEIGTHS               = "weigths";
        public static final String PARAM_CONNECT_TIMEOUT       = "connectTimeout";
        public static final String PARAM_CONNECTION_POOL_SIZE  = "connectionPoolSize";
        public static final String PARAM_COMMAND_FACTORY       = "commandFactory";
        public static final String PARAM_SESSION_LOCATOR       = "sessionLocator";
        public static final String PARAM_TRANSCODER            = "transcoder";
        public static final String PARAM_FAILURE_MODE          = "failureMode";
        public static final String PARAM_AUTH_INFO_MAP         = "authInfoMap";
        
        public static final String PARAM_COMPRESSION_THRESHOLD = "compressionThreshold";
        public static final String PARAM_PACK_ZEROS            = "packZeros";
        public static final String PARAM_SANITIZE_KEYS         = "sanitizeKeys";
        
        
        private List<InetSocketAddress>          _servers;
        private int[]                            _weigths;
        private long                             _connectTimeout;
        private Map<InetSocketAddress, AuthInfo> _authInfoMap; 
        private int                              _connectionPoolSize;
        private CommandFactory                   _commandFactory;
        private MemcachedSessionLocator          _sessionLocator;
        private Transcoder<?>                    _transcoder;
        private boolean                          _failureMode;
        
        private int                              _compressionThreshold;
        private boolean                          _packZeros;
        private boolean                          _sanitizeKeys;
        
        
        public ConfigLoader load(Properties props) {
            doLoadXMemcachedConfigs(props);
            return this;
        }
        
        public List<InetSocketAddress> getServers() {
            if (_servers == null || _servers.isEmpty()) {
                throw new CacheException("XMemcached servers must be set.");
            }
            return _servers;
        }
        
        public int[] getWeigths() {
            if (_weigths != null && (_weigths.length != getServers().size())) {
                throw new IllegalArgumentException(
                    "XMemcached weigths is not match servers configuration.");
            }
            return _weigths;
        }
        
        public long getConnectTimeout() {
            return _connectTimeout;
        }
        
        public int getConnectionPoolSize() {
            return _connectionPoolSize;
        }
        
        public CommandFactory getCommandFactory() {
            return _commandFactory;
        }
        
        public MemcachedSessionLocator getSessionLocator() {
            return _sessionLocator;
        }
        
        public Transcoder<?> getTranscoder() {
            return _transcoder;
        }

        public boolean isFailureMode() {
            return _failureMode;
        }
        
        public Map<InetSocketAddress, AuthInfo> getAuthInfoMap() {
            return _authInfoMap;
        }
        
        public int getCompressionThreshold() {
            return _compressionThreshold;
        }
        
        public boolean isPackZeros() {
            return _packZeros;
        }
        
        public boolean isSanitizeKeys() {
            return _sanitizeKeys;
        }
        
        private void doLoadXMemcachedConfigs(Properties props) {
            doLoadServers(props.getProperty(PARAM_SERVERS));
            doLoadWeigths(props.getProperty(PARAM_WEIGTHS));
            doLoadConnectTimeout(props.getProperty(PARAM_CONNECT_TIMEOUT));
            doLoadConnectionPoolSize(props.getProperty(PARAM_CONNECTION_POOL_SIZE));
            doLoadCommandFactory(props.getProperty(PARAM_COMMAND_FACTORY));
            doLoadSessionLocator(props.getProperty(PARAM_SESSION_LOCATOR));
            doLoadTranscoder(props.getProperty(PARAM_TRANSCODER));
            doLoadFailureMode(props.getProperty(PARAM_FAILURE_MODE));
            doLoadAuthInfoMap(props.getProperty(PARAM_AUTH_INFO_MAP));
            
            doLoadCompressionThreshold(props.getProperty(PARAM_COMPRESSION_THRESHOLD));
            doLoadPackZeros(props.getProperty(PARAM_PACK_ZEROS));
            doLoadSanitizeKeys(props.getProperty(PARAM_SANITIZE_KEYS));
        }
        
        private void doLoadServers(String serversValue) {
            if (serversValue != null && !serversValue.isEmpty()) {
                _servers = AddrUtil.getAddresses(serversValue.trim());
            }
        }
        
        public void doLoadWeigths(String weigthsValue) {
            if (weigthsValue != null && !weigthsValue.isEmpty()) {
                String[] weigthAry = weigthsValue.trim().split(",");
                int[] weigths = new int[weigthAry.length];
                for (int i = 0; i < weigthAry.length; i++) {
                    weigths[i] = Integer.parseInt(weigthAry[i]);
                }
                if (weigths.length != getServers().size()) {
                    throw new IllegalArgumentException("XMemcached weigths is not match servers configuration.");
                }
                _weigths = weigths;
            }
        }
        
        private void doLoadConnectTimeout(String connectTimeoutValue) {
            if (connectTimeoutValue != null && !connectTimeoutValue.isEmpty()) {
                long connectTimeout = Long.parseLong(connectTimeoutValue.trim());
                if (connectTimeout <= 0) {
                    throw new IllegalArgumentException(
                        "XMemcached connect timeout must be a positive number: " + connectTimeoutValue);
                }
                _connectTimeout = connectTimeout;
            }
        }
        
        private void doLoadConnectionPoolSize(String connectionPoolSizeValue) {
            if (connectionPoolSizeValue != null && !connectionPoolSizeValue.isEmpty()) {
                int connectionPoolSize = Integer.parseInt(connectionPoolSizeValue.trim());
                if (connectionPoolSize <= 0) {
                    throw new IllegalArgumentException(
                        "XMemcached connection pool size must be a positive number: " + connectionPoolSizeValue);
                }
                _connectionPoolSize = connectionPoolSize;
            }
        }
        
        private void doLoadCommandFactory(String commandFactoryValue) {
            if (commandFactoryValue != null && !commandFactoryValue.isEmpty()) {
                _commandFactory = (CommandFactory)  newInstance(commandFactoryValue.trim());
            }
        }
        
        private void doLoadSessionLocator(String sessionLocatorValue) {
            if (sessionLocatorValue != null && !sessionLocatorValue.isEmpty()) {
                _sessionLocator = (MemcachedSessionLocator) newInstance(sessionLocatorValue.trim());
            }
        }
        
        private void doLoadTranscoder(String transcoderValue) {
            if (transcoderValue != null && !transcoderValue.isEmpty()) {
                _transcoder = (Transcoder<?>) newInstance(transcoderValue.trim());
            }
        }
        
        private void doLoadFailureMode(String failureModeValue) {
            if (failureModeValue != null && !failureModeValue.isEmpty()) {
                _failureMode = Boolean.parseBoolean(failureModeValue.trim());
            }
        }

        public void doLoadAuthInfoMap(String authInfoMapValue) {
            if (authInfoMapValue != null && !authInfoMapValue.isEmpty()) {
                String[] authInfoAry = authInfoMapValue.trim().split(" ");
                if (authInfoAry.length < 2) {
                    throw new IllegalArgumentException("??");
                }
                
                String authDescriptor = authInfoAry[0].trim();
                Map<InetSocketAddress, AuthInfo> authInfoMap = new HashMap<InetSocketAddress, AuthInfo>(_servers.size(), 1F);
                
                for (int i = 1; i < authInfoAry.length; i++) {
                    String authInfoValue = authInfoAry[i];
                    String[] infoAry = authInfoValue.split(":");
                    String hostString = infoAry[0].trim();
                    String userName = infoAry[1].trim();
                    String password = infoAry[2].trim();
                    AuthInfo authInfo = null;
                    if ("plain".equalsIgnoreCase(authDescriptor)) {
                        authInfo = AuthInfo.plain(userName, password);
                    } else if ("cramMD5".equalsIgnoreCase(authDescriptor)) {
                        authInfo = AuthInfo.cramMD5(userName, password);
                    } else if ("typical".equalsIgnoreCase(authDescriptor)) {
                        authInfo = AuthInfo.typical(userName, password);
                    }
                    
                    for (InetSocketAddress server : getServers()) {
                        if (hostString.equals(server.getHostString())) {
                            authInfoMap.put(server, authInfo);
                            break;
                        }
                    }
                }
                
                _authInfoMap = authInfoMap;
            }
        }
        
        private void doLoadCompressionThreshold(String compressionThresholdValue) {
            if (compressionThresholdValue != null && !compressionThresholdValue.isEmpty()) {
                int compressionThreshold = Integer.parseInt(compressionThresholdValue.trim());
                if (compressionThreshold <= 0) {
                    throw new IllegalArgumentException(
                        "XMemcached transcoder compression threshold must be a positive number: " + compressionThresholdValue);
                }
                _compressionThreshold = compressionThreshold;
            }
        }
        
        private void doLoadPackZeros(String packZerosValue) {
            if (packZerosValue != null && !packZerosValue.isEmpty()) {
                _packZeros = Boolean.parseBoolean(packZerosValue.trim());
            }
        }
        
        private void doLoadSanitizeKeys(String sanitizeKeysValue) {
            if (sanitizeKeysValue != null && !sanitizeKeysValue.isEmpty()) {
                _sanitizeKeys = Boolean.parseBoolean(sanitizeKeysValue.trim());
            }
        }

        private Object newInstance(String className) {
            try {
                return Class.forName(className).newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Could not new instance: " + className, e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not access class: " + className, e);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Could not found class: " + className, e);
            }
        }
        
        @Override
        public String toString() {
            final String format = "    ";
            final String equals = " = ";
            final String enter = "\n";
            StringBuilder builder = new StringBuilder();
            builder.append(format).append(PARAM_SERVERS).append(equals).append(_servers).append(enter);
            builder.append(format).append(PARAM_WEIGTHS).append(equals).append(Arrays.toString(_weigths)).append(enter);
            builder.append(format).append(PARAM_CONNECT_TIMEOUT).append(equals).append(_connectTimeout).append(enter);
            builder.append(format).append(PARAM_CONNECTION_POOL_SIZE).append(equals).append(_connectionPoolSize).append(enter);
            builder.append(format).append(PARAM_COMMAND_FACTORY).append(equals).append(_commandFactory.getClass().getName()).append(enter);
            builder.append(format).append(PARAM_SESSION_LOCATOR).append(equals).append(_sessionLocator.getClass().getName()).append(enter);
            builder.append(format).append(PARAM_TRANSCODER).append(equals).append(_transcoder.getClass().getName()).append(enter);
            builder.append(format).append(PARAM_FAILURE_MODE).append(equals).append(_failureMode).append(enter);
            builder.append(format).append(PARAM_AUTH_INFO_MAP).append(equals).append(_authInfoMap).append(enter);
            builder.append(format).append(PARAM_COMPRESSION_THRESHOLD).append(equals).append(_compressionThreshold).append(enter);
            builder.append(format).append(PARAM_PACK_ZEROS).append(equals).append(_packZeros).append(enter);
            builder.append(format).append(PARAM_SANITIZE_KEYS).append(equals).append(_sanitizeKeys);
            
            return builder.toString();
        }
        
    }

}
