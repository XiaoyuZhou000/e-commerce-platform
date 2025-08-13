package com.ecommerce.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.request.CartRequest;
import com.ecommerce.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private final ObjectMapper objectMapper;
    
    public CartController() {
        this.cartService = new CartService();
        this.objectMapper = new ObjectMapper();
    }
    
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String path = request.getPath();
            String httpMethod = request.getHttpMethod();
            
            logger.info("CartController: {} {}", httpMethod, path);
            
            if ("GET".equals(httpMethod) && "/api/cart/get".equals(path)) {
                return getCart();
            } else if ("POST".equals(httpMethod) && "/api/cart/add".equals(path)) {
                return addToCart(request);
            } else if ("POST".equals(httpMethod) && "/api/cart/revise".equals(path)) {
                return updateCartItem(request);
            } else if ("POST".equals(httpMethod) && "/api/cart/remove".equals(path)) {
                return removeFromCart(request);
            } else if ("DELETE".equals(httpMethod) && "/api/cart/clear".equals(path)) {
                return clearCart();
            }
            
            return createErrorResponse(404, "Endpoint not found");
            
        } catch (Exception e) {
            logger.error("Error in CartController", e);
            return createErrorResponse(500, "Internal server error");
        }
    }
    
    private APIGatewayProxyResponseEvent getCart() {
        try {
            List<CartItem> cartItems = cartService.getCart();
            String responseBody = objectMapper.writeValueAsString(cartItems);
            
            return createSuccessResponse(200, responseBody);
        } catch (Exception e) {
            logger.error("Error retrieving cart", e);
            return createErrorResponse(500, "Error retrieving cart");
        }
    }
    
    private APIGatewayProxyResponseEvent addToCart(APIGatewayProxyRequestEvent request) {
        try {
            String requestBody = request.getBody();
            if (requestBody == null || requestBody.trim().isEmpty()) {
                return createErrorResponse(400, "Request body is required");
            }
            
            CartRequest cartRequest = objectMapper.readValue(requestBody, CartRequest.class);
            
            if (cartRequest.getProductID() == null || cartRequest.getQuantity() == null) {
                return createErrorResponse(400, "Product ID and quantity are required");
            }
            
            boolean success = cartService.addToCart(cartRequest.getProductID(), cartRequest.getQuantity());
            
            if (success) {
                return createSuccessResponse(200, "Item added to cart successfully");
            } else {
                return createErrorResponse(400, "Failed to add item to cart");
            }
            
        } catch (Exception e) {
            logger.error("Error adding to cart", e);
            return createErrorResponse(500, "Error adding to cart");
        }
    }
    
    private APIGatewayProxyResponseEvent updateCartItem(APIGatewayProxyRequestEvent request) {
        try {
            String requestBody = request.getBody();
            if (requestBody == null || requestBody.trim().isEmpty()) {
                return createErrorResponse(400, "Request body is required");
            }
            
            CartRequest cartRequest = objectMapper.readValue(requestBody, CartRequest.class);
            
            if (cartRequest.getProductID() == null || cartRequest.getQuantity() == null) {
                return createErrorResponse(400, "Product ID and quantity are required");
            }
            
            boolean success = cartService.updateCartItem(cartRequest.getProductID(), cartRequest.getQuantity());
            
            if (success) {
                return createSuccessResponse(200, "Item added to cart successfully");
            } else {
                return createErrorResponse(400, "Failed to update cart item");
            }
            
        } catch (Exception e) {
            logger.error("Error updating cart item", e);
            return createErrorResponse(500, "Error updating cart item");
        }
    }
    
    private APIGatewayProxyResponseEvent removeFromCart(APIGatewayProxyRequestEvent request) {
        try {
            String requestBody = request.getBody();
            if (requestBody == null || requestBody.trim().isEmpty()) {
                return createErrorResponse(400, "Request body is required");
            }
            
            CartRequest cartRequest = objectMapper.readValue(requestBody, CartRequest.class);
            
            if (cartRequest.getProductID() == null) {
                return createErrorResponse(400, "Product ID is required");
            }
            
            boolean success = cartService.removeFromCart(cartRequest.getProductID());
            
            if (success) {
                return createSuccessResponse(200, "Item removed from cart successfully");
            } else {
                return createErrorResponse(400, "Failed to remove item from cart");
            }
            
        } catch (Exception e) {
            logger.error("Error removing from cart", e);
            return createErrorResponse(500, "Error removing from cart");
        }
    }
    
    private APIGatewayProxyResponseEvent clearCart() {
        try {
            boolean success = cartService.clearCart();
            
            if (success) {
                return createSuccessResponse(200, "Cart cleared successfully");
            } else {
                return createErrorResponse(400, "Failed to clear cart");
            }
            
        } catch (Exception e) {
            logger.error("Error clearing cart", e);
            return createErrorResponse(500, "Error clearing cart");
        }
    }
    
    // Generated by AI
    private APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(body);
    }
    
    // Generated by AI
    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        
        try {
            String body = objectMapper.writeValueAsString(errorResponse);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(headers)
                    .withBody(body);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(headers)
                    .withBody("{\"error\":\"" + message + "\"}");
        }
    }
} 