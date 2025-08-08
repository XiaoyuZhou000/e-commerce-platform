package com.ecommerce.service;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving products", e);
        }
    }

    public Product getProductById(Integer productID) {
        try {
            return productDAO.getProductById(productID);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving product with ID: " + productID, e);
        }
    }

    public boolean updateProductQuantity(Integer productID, Integer quantity) {
        try {
            return productDAO.updateProductQuantity(productID, quantity);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product quantity for productID: " + productID, e);
        }
    }

    public Integer getProductQuantity(Integer productID) {
        try {
            return productDAO.getProductQuantity(productID);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving product quantity for productID: " + productID, e);
        }
    }

    public boolean isProductAvailable(Integer productID, Integer requestedQuantity) {
        Integer availableQuantity = getProductQuantity(productID);
        boolean available = availableQuantity != null && availableQuantity >= requestedQuantity;
        
        return available;
    }

    public boolean reserveProduct(Integer productID, Integer quantity) {
        Integer currentQuantity = getProductQuantity(productID);
        if (currentQuantity != null && currentQuantity >= quantity) {
            Integer newQuantity = currentQuantity - quantity;
            return updateProductQuantity(productID, newQuantity);
        }
        return false;
    }
} 