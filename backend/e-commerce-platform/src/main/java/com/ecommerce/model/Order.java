package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import java.util.List;

@Data
public class Order {
    @JsonProperty("orderID")
    private Integer orderID;
    
    @JsonProperty("orderDate")
    private String orderDate;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("grandTotal")
    private Double grandTotal;
    
    @JsonProperty("items")
    private List<CartItem> items;
    
    public Order() {}
    
    public Order(Integer orderID, String orderDate, String status, Double grandTotal, List<CartItem> items) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.grandTotal = grandTotal;
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", grandTotal=" + grandTotal +
                ", items=" + items +
                '}';
    }
} 