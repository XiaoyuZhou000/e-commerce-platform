package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {
    @JsonProperty("productID")
    private Integer productID;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("price")
    private Double price;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("quantityAvailable")
    private Integer quantityAvailable;
    
    public Product() {}
    
    public Product(Integer productID, String name, String description, Double price, String category, Integer quantityAvailable) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantityAvailable = quantityAvailable;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", quantityAvailable=" + quantityAvailable +
                '}';
    }
} 