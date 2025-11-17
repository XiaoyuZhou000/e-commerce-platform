package com.ecommerce.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.ecommerce.controller.CartController;
import com.ecommerce.controller.OrderController;
import com.ecommerce.controller.ProductController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);
    
    private final ProductController productController;
    private final CartController cartController;
    private final OrderController orderController;
    
    public LambdaHandler() {
        this.productController = new ProductController();
        this.cartController = new CartController();
        this.orderController = new OrderController();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String path = request.getPath();
            String httpMethod = request.getHttpMethod();
            
            logger.info("LambdaHandler: {} {}", httpMethod, path);
            
            if ("OPTIONS".equals(httpMethod)) {
                return handleCorsPreflight();
            }
            
            if (path.startsWith("/api/products") || path.startsWith("/api/product/")) {
                return productController.handleRequest(request, context);
            } else if (path.startsWith("/api/cart/")) {
                return cartController.handleRequest(request, context);
            } else if (path.startsWith("/api/orders/")) {
                return orderController.handleRequest(request, context);
            } else {
                return createErrorResponse(404, "Endpoint not found");
            }
            
        } catch (Exception e) {
            logger.error("Error in LambdaHandler", e);
            return createErrorResponse(500, "Internal server error");
        }
    }
    
    private APIGatewayProxyResponseEvent handleCorsPreflight() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-User-ID");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        headers.put("Access-Control-Max-Age", "86400");
        
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(headers)
                .withBody("");
    }
    
    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-User-ID");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody("{\"error\":\"" + message + "\"}");
    }
} 