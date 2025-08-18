package com.ecommerce;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.ecommerce.handler.LambdaHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class LocalTestRunner {
    private static final int PORT = 18080;
    private static final LambdaHandler lambdaHandler = new LambdaHandler();
    
    public static void main(String[] args) throws IOException {
        System.setProperty("DB_URL", "jdbc:mysql://ecommerceplatformmysql.crs6q8004cg8.us-east-2.rds.amazonaws.com:3306/ecommerce?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        System.setProperty("DB_USER", "ecommercemysql");
        System.setProperty("DB_PASSWORD", "123456789");
        System.setProperty("REDIS_HOST", "cart-lspkld.serverless.use2.cache.amazonaws.com");
        System.setProperty("REDIS_PORT", "6379");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new ApiHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        server.start();
    }
    
    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                APIGatewayProxyRequestEvent request = createApiGatewayRequest(exchange);
                Context context = createMockContext();
                APIGatewayProxyResponseEvent response = lambdaHandler.handleRequest(request, context);
                sendResponse(exchange, response);                
            } catch (Exception e) {
                e.printStackTrace();
                String errorBody = "error: Internal server error";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(500, errorBody.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorBody.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        
        private APIGatewayProxyRequestEvent createApiGatewayRequest(HttpExchange exchange) throws IOException {
            APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
            request.setHttpMethod(exchange.getRequestMethod());
            URI uri = exchange.getRequestURI();
            request.setPath(uri.getPath());
            Map<String, String> headers = new HashMap<>();
            exchange.getRequestHeaders().forEach((key, values) -> {
                if (!values.isEmpty()) {
                    headers.put(key, values.get(0));
                }
            });
            request.setHeaders(headers);
            String query = uri.getQuery();
            Map<String, String> queryParams = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        queryParams.put(keyValue[0], keyValue[1]);
                    }
                }
            }
            request.setQueryStringParameters(queryParams.isEmpty() ? null : queryParams);
            
            String body = null;
            if ("POST".equals(exchange.getRequestMethod()) || "PUT".equals(exchange.getRequestMethod())) {
                try (InputStream is = exchange.getRequestBody();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    StringBuilder bodyBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        bodyBuilder.append(line);
                    }
                    body = bodyBuilder.toString();
                }
            }
            request.setBody(body);            
            return request;
        }
        
        private Context createMockContext() {
            return new Context() {
                @Override
                public String getAwsRequestId() { return "local-request-" + System.currentTimeMillis(); }
                
                @Override
                public String getLogGroupName() { return "local-log-group"; }
                
                @Override
                public String getLogStreamName() { return "local-log-stream"; }
                
                @Override
                public String getFunctionName() { return "local-ecommerce-function"; }
                
                @Override
                public String getFunctionVersion() { return "1.0"; }
                
                @Override
                public String getInvokedFunctionArn() { return "local-function-arn"; }
                
                @Override
                public CognitoIdentity getIdentity() { return null; }
                
                @Override
                public ClientContext getClientContext() { return null; }
                
                @Override
                public int getRemainingTimeInMillis() { return 30000; }
                
                @Override
                public int getMemoryLimitInMB() { return 512; }
                
                @Override
                public LambdaLogger getLogger() {
                    return new LambdaLogger() {
                        @Override
                        public void log(String message) {
                            System.out.println("[LAMBDA] " + message);
                        }
                        
                        @Override
                        public void log(byte[] message) {
                            System.out.println("[LAMBDA] " + new String(message));
                        }
                    };
                }
            };
        }
        
        private void sendResponse(HttpExchange exchange, APIGatewayProxyResponseEvent response) throws IOException {
            if (response.getHeaders() != null) {
                response.getHeaders().forEach((key, value) -> {
                    exchange.getResponseHeaders().set(key, value);
                });
            }
            
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            
            if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
            }
            
            String responseBody = response.getBody() != null ? response.getBody() : "";
            byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            
            exchange.sendResponseHeaders(response.getStatusCode(), responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}