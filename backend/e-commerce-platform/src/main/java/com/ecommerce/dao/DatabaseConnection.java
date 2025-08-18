package com.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    
    // private static final String DB_URL = getProperty("DB_URL", "jdbc:mysql://ecommerceplatformmysql.crs6q8004cg8.us-east-2.rds.amazonaws.com:3306/ecommerce");
    private static final String DB_URL = getProperty("DB_URL", "ecommerceplatformmysql.crs6q8004cg8.us-east-2.rds.amazonaws.com:3306/ecommerce");
    private static final String DB_USER = getProperty("DB_USER", "ecommercemysql");
    private static final String DB_PASSWORD = getProperty("DB_PASSWORD", "123456789");
    
    private static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        return defaultValue;
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