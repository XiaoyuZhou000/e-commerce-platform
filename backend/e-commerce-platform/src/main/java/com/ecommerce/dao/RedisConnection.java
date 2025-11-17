package com.ecommerce.dao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisConnection {
    private static final Logger logger = LoggerFactory.getLogger(RedisConnection.class);
    private static JedisPool jedisPool;
    
    private static final String REDIS_HOST = getProperty("REDIS_HOST", "cart-lspkld.serverless.use2.cache.amazonaws.com");
    private static final int REDIS_PORT = Integer.parseInt(getProperty("REDIS_PORT", "6379"));
    
    private static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        return defaultValue;
    }

    static {
        try {
            // Creates a new configuration object that will hold all the settings for the Redis connection pool.
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            // Sets the maximum number of connections that can be created by the pool.
            poolConfig.setMaxTotal(20);
            // Sets the maximum number of idle (unused but ready)
            poolConfig.setMaxIdle(10);
            // Sets the minimum number of idle connections
            poolConfig.setMinIdle(5);
            // Enables connection validation when a connection is borrowed from the pool
            poolConfig.setTestOnBorrow(true);
            // Enables connection validation when a connection is returned to the pool
            poolConfig.setTestOnReturn(true);
            // Enables periodic testing of idle connections in the pool
            poolConfig.setTestWhileIdle(true);
            
            // Connection timeout in milliseconds (10 seconds for AWS ElastiCache)
            int connectionTimeout = 10000;
            
            logger.info("Initializing Redis connection pool to {}:{} with {}ms timeout", REDIS_HOST, REDIS_PORT, connectionTimeout);
            jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, connectionTimeout);
            
            // Verify the pool was created
            if (jedisPool == null) {
                throw new RuntimeException("JedisPool creation returned null");
            }
            
            logger.info("Redis connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Redis connection pool", e);
            throw new RuntimeException("Failed to initialize Redis connection pool", e);
        }
    }

    public static Jedis getConnection() {
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            logger.error("Failed to get Redis connection", e);
            throw new RuntimeException("Failed to get Redis connection", e);
        }
    }

    public static void closeConnection(Jedis jedis) {
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error("Error closing Redis connection", e);
            }
        }
    }

    public static void shutdown() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
    
    public static JedisPool getJedisPool() {
        return jedisPool;
    }
} 