package com.ecommerce.service;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.model.response.OrderResponse;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO;
    private final ProductService productService;
    
    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productService = new ProductService();
    }
    
    // No rollback handling for now
    public OrderResponse placeOrder(List<CartItem> items) {
        try {
            for (CartItem item : items) {
                Integer productId = item.getProductID();
                if (!productService.isProductAvailable(productId, item.getQuantity())) {
                    return new OrderResponse(1, "INSUFFICIENT_STOCK", null, "Failed", 0.0);
                }
            }
            
            Double grandTotal = 0.0;
            for (CartItem item : items) {
                Product product = productService.getProductById(item.getProductID());
                if (product != null) {
                    grandTotal += product.getPrice() * item.getQuantity();
                }
            }
            
            Integer orderID = orderDAO.createOrder(grandTotal);
            if (orderID == null) {
                return new OrderResponse(1, "ORDER_CREATION_FAILED", null, "Failed", 0.0);
            }
            
            for (CartItem item : items) {
                Product product = productService.getProductById(item.getProductID());
                if (product != null) {
                    boolean orderItemCreated = orderDAO.createOrderItem(orderID, item.getProductID(), item.getQuantity(), product.getPrice());
                    if (!orderItemCreated) {
                        return new OrderResponse(1, "ORDER_ITEM_CREATION_FAILED", null, "Failed", 0.0);
                    }
                    
                    boolean productReserved = productService.reserveProduct(item.getProductID(), item.getQuantity());
                    if (!productReserved) {
                        return new OrderResponse(1, "PRODUCT_RESERVATION_FAILED", null, "Failed", 0.0);
                    }
                }
            }
            
            return new OrderResponse(0, "SUCCESS", orderID, "Processing", grandTotal);
            
        } catch (Exception e) {
            return new OrderResponse(1,"SYSTEM_ERROR", null, "Failed", 0.0);
        }
    }
    
    public Order getOrderById(Integer orderID) {
        try {
            return orderDAO.getOrderById(orderID);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order with ID: " + orderID, e);
        }
    }
    
    public List<Order> getAllOrders() {
        try {
            return orderDAO.getAllOrders();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all orders", e);
        }
    }
} 