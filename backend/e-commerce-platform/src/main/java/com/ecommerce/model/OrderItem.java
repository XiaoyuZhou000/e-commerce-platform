package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderItem {
    @JsonProperty("orderItemID")
    private Integer orderItemID;
    
    @JsonProperty("orderID")
    private Integer orderID;
    
    @JsonProperty("productID")
    private Integer productID;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    @JsonProperty("price")
    private Double price;
    
    public OrderItem() {}
    
    public OrderItem(Integer orderItemID, Integer orderID, Integer productID, Integer quantity, Double price) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemID=" + orderItemID +
                ", orderID=" + orderID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
} 