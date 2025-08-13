package com.ecommerce.service;

import com.ecommerce.dao.CartDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private final CartDAO cartDAO;
    private final ProductService productService;
    
    public CartService() {
        this.cartDAO = new CartDAO();
        this.productService = new ProductService();
    }
    
    public List<CartItem> getCart() {
        logger.info("Retrieving cart contents");
        return cartDAO.getCart();
    }
    
    public boolean addToCart(Integer productId, Integer quantity) {
        logger.info("Adding to cart - productID: {}, quantity: {}", productId, quantity);
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            logger.warn("Product not found: {}", productId);
            return false;
        }
        
        if (!productService.isProductAvailable(productId, quantity)) {
            logger.warn("Insufficient stock for product: {} (requested: {}, available: {})", 
                       productId, quantity, product.getQuantityAvailable());
            return false;
        }
        
        return cartDAO.addToCart(productId, quantity);
    }
    
    public boolean updateCartItem(Integer productID, Integer quantity) {
        logger.info("Updating cart item - productID: {}, quantity: {}", productID, quantity);
        
        if (quantity <= 0) {
            logger.warn("Attempted to set non-positive quantity in cart for productID: {}", productID);
            return cartDAO.removeFromCart(productID);
        }
        
        Integer productId = productID;
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            logger.warn("Product not found: {}", productID);
            return false;
        }
        
        if (!productService.isProductAvailable(productId, quantity)) {
            logger.warn("Insufficient stock for product: {} (requested: {}, available: {})", 
                       productID, quantity, product.getQuantityAvailable());
            return false;
        }
        
        return cartDAO.updateCartItem(productId, quantity);
    }
    
    public boolean removeFromCart(Integer productID) {
        return cartDAO.removeFromCart(productID);
    }
    
    public boolean clearCart() {
        logger.info("Clearing cart");
        return cartDAO.clearCart();
    }
    
    public Double calculateCartTotal() {
        logger.info("Calculating cart total");
        List<CartItem> cartItems = getCart();
        Double total = 0.0;
        
        for (CartItem item : cartItems) {
            Integer productId = item.getProductID();
            Product product = productService.getProductById(productId);
            if (product != null) {
                total += product.getPrice() * item.getQuantity();
            }
        }
        
        logger.info("Cart total calculated: {}", total);
        return total;
    }
    
    public boolean validateCart() {
        logger.info("Validating cart contents");
        List<CartItem> cartItems = getCart();
        
        for (CartItem item : cartItems) {
            Integer productId = item.getProductID();
            Product product = productService.getProductById(productId);
            if (product == null) {
                logger.warn("Product not found: {}", item.getProductID());
                return false; 
            }
            
            if (!productService.isProductAvailable(productId, item.getQuantity())) {
                logger.warn("Insufficient stock for cart item: {} (requested: {}, available: {})", 
                           item.getProductID(), item.getQuantity(), product.getQuantityAvailable());
                return false; 
            }
        }
        
        logger.info("Cart validation successful");
        return true;
    }
    
    public Double calculateCartTotal(List<CartItem> cartItems) {
        logger.info("Calculating cart total from request items");
        Double total = 0.0;
        
        for (CartItem item : cartItems) {
            Integer productId = item.getProductID();
            Product product = productService.getProductById(productId);
            if (product != null) {
                total += product.getPrice() * item.getQuantity();
            }
        }
        
        logger.info("Cart total calculated: {}", total);
        return total;
    }
} 