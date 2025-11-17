package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CartDAO {
    private static final Logger logger = LoggerFactory.getLogger(CartDAO.class);
    private final JedisPool jedisPool;
    private static final String CART_KEY = "cart:global";
    
    public CartDAO() {
        this.jedisPool = RedisConnection.getJedisPool();
        if (this.jedisPool == null) {
            logger.error("JedisPool is null! Redis connection pool was not initialized properly.");
            throw new IllegalStateException("JedisPool is null. Redis connection pool initialization failed.");
        }
        logger.debug("CartDAO initialized with JedisPool: {}", jedisPool);
    }
    
    public List<CartItem> getCart() {
        if (jedisPool == null) {
            logger.error("Cannot get cart: JedisPool is null");
            throw new IllegalStateException("Redis connection pool is not available");
        }
        
        logger.debug("Attempting to get Redis connection from pool");
        try {
            Jedis jedis = jedisPool.getResource();
            logger.debug("Successfully obtained Redis connection, jedis object: {}", jedis);
            try (Jedis jedisResource = jedis) {
                Map<String, String> cartData = jedisResource.hgetAll(CART_KEY);
                
                List<CartItem> cartItems = new ArrayList<>();
                for (Map.Entry<String, String> entry : cartData.entrySet()) {
                    Integer productID = Integer.parseInt(entry.getKey());
                    Integer quantity = Integer.parseInt(entry.getValue());
                    cartItems.add(new CartItem(productID, quantity));
                }
                
                return cartItems;
            }
        } catch (Exception e) {
            logger.error("Error getting cart from Redis. Exception type: {}, Message: {}", 
                        e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to get cart from Redis", e);
        }
    }
    
    public boolean addToCart(Integer productID, Integer quantity) {
        if (jedisPool == null) {
            logger.error("Cannot add to cart: JedisPool is null");
            throw new IllegalStateException("Redis connection pool is not available");
        }
        
        try {
            Jedis jedis = jedisPool.getResource();
            logger.debug("Successfully obtained Redis connection for addToCart");
            try (Jedis jedisResource = jedis) {
                String productKey = productID.toString();
                
                String currentQuantityStr = jedisResource.hget(CART_KEY, productKey);
                Integer currentQuantity = currentQuantityStr != null ? Integer.parseInt(currentQuantityStr) : 0;
                
                Integer newQuantity = currentQuantity + quantity;
                
                jedisResource.hset(CART_KEY, productKey, newQuantity.toString());
                
                jedisResource.expire(CART_KEY, 86400);
                
                return true;
            }
        } catch (Exception e) {
            logger.error("Error adding to cart: productID={}, quantity={}. Exception type: {}, Message: {}", 
                        productID, quantity, e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to add to cart", e);
        }
    }
    
    public boolean updateCartItem(Integer productID, Integer quantity) {
        try (Jedis jedis = jedisPool.getResource()) {
            String productKey = productID.toString();
            
            if (quantity <= 0) {
                jedis.hdel(CART_KEY, productKey);
            } else {
                jedis.hset(CART_KEY, productKey, quantity.toString());
            }
            
            jedis.expire(CART_KEY, 86400);
            
            return true;
        }
    }
    
    public boolean removeFromCart(Integer productID) {
        try (Jedis jedis = jedisPool.getResource()) {
            String productKey = productID.toString();
            
            Long result = jedis.hdel(CART_KEY, productKey);
            return result > 0;
        }
    }
    
    public boolean clearCart() {
        try (Jedis jedis = jedisPool.getResource()) {
            Long result = jedis.del(CART_KEY);
            return result > 0;
        }
    }
    
    public Integer getCartItemQuantity(Integer productID) {
        try (Jedis jedis = jedisPool.getResource()) {
            String productKey = productID.toString();
            
            String quantityStr = jedis.hget(CART_KEY, productKey);
            return quantityStr != null ? Integer.parseInt(quantityStr) : 0;
        }
    }
    
    public boolean cartExists() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(CART_KEY);
        }
    }
} 