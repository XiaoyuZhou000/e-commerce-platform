package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    
    // Database configuration - these would be environment variables in production
    private static final String DB_URL = System.getenv("DB_URL") != null ? 
        System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/ecommerce";
    private static final String DB_USER = System.getenv("DB_USER") != null ? 
        System.getenv("DB_USER") : "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? 
        System.getenv("DB_PASSWORD") : "password";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
} 