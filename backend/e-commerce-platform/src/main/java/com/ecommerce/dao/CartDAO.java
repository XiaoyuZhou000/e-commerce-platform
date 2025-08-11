package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

public class CartDAO {
    private final JedisPool jedisPool;
    private static final String CART_KEY = "cart:global";
    
    public CartDAO() {
        this.jedisPool = RedisConnection.getJedisPool();
    }
    
    public List<CartItem> getCart() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> cartData = jedis.hgetAll(CART_KEY);
            
            List<CartItem> cartItems = new ArrayList<>();
            for (Map.Entry<String, String> entry : cartData.entrySet()) {
                Integer productID = Integer.parseInt(entry.getKey());
                Integer quantity = Integer.parseInt(entry.getValue());
                cartItems.add(new CartItem(productID, quantity));
            }
            
            return cartItems;
        }
    }
    
    public boolean addToCart(Integer productID, Integer quantity) {
        try (Jedis jedis = jedisPool.getResource()) {
            String productKey = productID.toString();
            
            String currentQuantityStr = jedis.hget(CART_KEY, productKey);
            Integer currentQuantity = currentQuantityStr != null ? Integer.parseInt(currentQuantityStr) : 0;
            
            Integer newQuantity = currentQuantity + quantity;
            
            jedis.hset(CART_KEY, productKey, newQuantity.toString());
            
            jedis.expire(CART_KEY, 86400);
            
            return true;
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