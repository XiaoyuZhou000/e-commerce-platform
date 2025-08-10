package com.ecommerce.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ecommerce.model.CartItem;
import java.util.List;
import lombok.Data;

@Data
public class OrderRequest {
    @JsonProperty("cartItems")
    private List<CartItem> cartItems;
    
    public OrderRequest() {}
    
    public OrderRequest(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    
    @Override
    public String toString() {
        return "OrderRequest{" +
                "cartItems=" + cartItems +
                '}';
    }
} 