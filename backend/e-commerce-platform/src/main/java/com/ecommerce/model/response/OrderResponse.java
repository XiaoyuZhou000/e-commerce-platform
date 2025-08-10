package com.ecommerce.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrderResponse {
    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;
    
    @JsonProperty("orderID")
    private Integer orderID;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("grandTotal")
    private Double grandTotal;
    
    public OrderResponse() {}
    
    public OrderResponse(Integer code, String message, Integer orderID, String status, Double grandTotal) {
        this.code = code;
        this.message = message;
        this.orderID = orderID;
        this.status = status;
        this.grandTotal = grandTotal;
    }
    
    @Override
    public String toString() {
        return "OrderResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", orderID=" + orderID +
                ", status='" + status + '\'' +
                ", grandTotal=" + grandTotal +
                '}';
    }
} 