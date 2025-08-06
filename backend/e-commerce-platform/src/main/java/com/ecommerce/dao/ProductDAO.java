package com.ecommerce.dao;

import com.ecommerce.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT productID, name, description, price, category, quantityAvailable FROM Products";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductID(rs.getInt("productID"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setCategory(rs.getString("category"));
                product.setQuantityAvailable(rs.getInt("quantityAvailable"));
                products.add(product);
            }
        }
        
        return products;
    }
    
    public Product getProductById(Integer productID) throws SQLException {
        String sql = "SELECT productID, name, description, price, category, quantityAvailable FROM Products WHERE productID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setProductID(productID);
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setCategory(rs.getString("category"));
                    product.setQuantityAvailable(rs.getInt("quantityAvailable"));
                    return product;
                }
            }
        }
        
        return null;
    }
    
    public boolean updateProductQuantity(Integer productID, Integer quantity) throws SQLException {
        String sql = "UPDATE Products SET quantityAvailable = ? WHERE productID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, productID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public Integer getProductQuantity(Integer productID) throws SQLException {
        String sql = "SELECT quantityAvailable FROM Products WHERE productID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantityAvailable");
                }
            }
        }
        
        return null;
    }
} 