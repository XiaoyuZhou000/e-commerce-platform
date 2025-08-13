package com.ecommerce.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CartRequest {
    @JsonProperty("productID")
    private Integer productID;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    public CartRequest() {}
    
    public CartRequest(Integer productID, Integer quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "CartRequest{" +
                "productID=" + productID +
                ", quantity=" + quantity +
                '}';
    }
} 