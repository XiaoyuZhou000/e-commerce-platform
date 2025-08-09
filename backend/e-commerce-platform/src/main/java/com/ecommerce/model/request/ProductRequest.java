package com.ecommerce.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductRequest {
    @JsonProperty("productID")
    private Integer productID;
    
    public ProductRequest() {}
    
    public ProductRequest(Integer productID) {
        this.productID = productID;
    }
    
    @Override
    public String toString() {
        return "ProductRequest{" +
                "productID=" + productID +
                '}';
    }
} 