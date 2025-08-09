package com.ecommerce.dao;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public Integer createOrder(Double grandTotal) throws SQLException {
        String sql = "INSERT INTO Orders (grandTotal) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDouble(1, grandTotal);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        
        return null;
    }
    
    public boolean createOrderItem(Integer orderID, Integer productID, Integer quantity, Double price) throws SQLException {
        String sql = "INSERT INTO OrderItems (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderID);
            stmt.setInt(2, productID);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public Order getOrderById(Integer orderID) throws SQLException {
        String sql = "SELECT orderID, orderDate, status, grandTotal FROM Orders WHERE orderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderID(rs.getInt("orderID"));
                    order.setOrderDate(rs.getTimestamp("orderDate").toString());
                    order.setStatus(rs.getString("status"));
                    order.setGrandTotal(rs.getDouble("grandTotal"));
                    
                    List<OrderItem> orderItems = getOrderItems(orderID);
                    List<CartItem> cartItems = new ArrayList<>();
                    for (OrderItem item : orderItems) {
                        CartItem cartItem = new CartItem(item.getProductID(), item.getQuantity());
                        cartItems.add(cartItem);
                    }
                    order.setItems(cartItems);
                    
                    return order;
                }
            }
        }
        return null;
    }
    
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT orderID, orderDate, status, grandTotal FROM Orders ORDER BY orderDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderID(rs.getInt("orderID"));
                order.setOrderDate(rs.getTimestamp("orderDate").toString());
                order.setStatus(rs.getString("status"));
                order.setGrandTotal(rs.getDouble("grandTotal"));
                
                List<OrderItem> orderItems = getOrderItems(order.getOrderID());
                List<CartItem> cartItems = new ArrayList<>();
                for (OrderItem item : orderItems) {
                    CartItem cartItem = new CartItem(item.getProductID(), item.getQuantity());
                    cartItems.add(cartItem);
                }
                order.setItems(cartItems);
                
                orders.add(order);
            }
        }
        return orders;
    }
    
    private List<OrderItem> getOrderItems(Integer orderID) throws SQLException {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT orderItemID, orderID, productID, quantity, price FROM OrderItems WHERE orderID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemID(rs.getInt("orderItemID"));
                    orderItem.setOrderID(rs.getInt("orderID"));
                    orderItem.setProductID(rs.getInt("productID"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setPrice(rs.getDouble("price"));
                    orderItems.add(orderItem);
                }
            }
        }
        
        return orderItems;
    }
} 