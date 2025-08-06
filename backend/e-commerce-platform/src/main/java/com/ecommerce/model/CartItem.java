package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartItem {
    @JsonProperty("productID")
    private Integer productID;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    public CartItem() {}
    
    public CartItem(Integer productID, Integer quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "productID=" + productID +
                ", quantity=" + quantity +
                '}';
    }
} 