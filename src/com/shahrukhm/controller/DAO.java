package com.shahrukhm.controller;

import com.shahrukhm.model.Product;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by shahrukhmubshar on 4/2/16.
 *
 * DAO: Serves as controller in MVC pattern.
 * Purpose: A simple data accessing object to bridge to communicate between application and Firebird database.
 */
public class DAO {
    // Database connection.
    private Connection myConn;

    /**
     * Creates a new DAO object with no parameters.
     * @throws IOException - If the properties file source is invalid.
     * @throws SQLException - If unable to access database, might be due to invalid credentials.
     */
    public DAO() throws IOException, SQLException {
        // 1. Get database credentials for properties file.
        Properties properties = new Properties();
        properties.load(new FileInputStream("SpeedwayDatabaseTool.properties"));

        String dburl = properties.getProperty("dburl");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        // 2. Attempt database connection.
        myConn = DriverManager.getConnection(dburl, user, password);
    }

    /**
     * Generates a list of products composed of all entries from the table PRODUCTLOCATION.
     * @return A list of type Product.
     * @throws SQLException - If the query is invalid or called on a closed result set.
     */
    public List<Product> getAllProducts() throws SQLException {
        // 1. Declare and initialize array list of type Product.
        List<Product> productArrayList = new ArrayList<>();
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            // 2. Create statement and execute query.
            myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery("SELECT * FROM PRODUCTLOCATION");

            // 3. Iterate through result set, create Product for all entries, and add products to list.
            while ( myRs.next() ) {
                Product tempProduct = convertRowToProduct(myRs);
                productArrayList.add(tempProduct);
            }
            return productArrayList;
        }
        finally {
            close(myStmt, myRs);
        }
    }

    /**
     * Convert a row from the result set into a product.
     * @param myRs - Result set obtained from getAllProducts() method.
     * @return the product generated from result set row.
     * @throws SQLException - If column labels are invalid or database access error occurs.
     */
    private Product convertRowToProduct(ResultSet myRs) throws SQLException {
        // 1. Get Product data from result set row.
        String partNumber = myRs.getString("PARTNUMBER");
        BigDecimal quantity = myRs.getBigDecimal("ONHANDAVAILABLEQUANTITY");

        // 2. Generate product.
        Product tempProduct = new Product(partNumber, quantity);
        return tempProduct;
    }


    private void close(Statement myStmt) throws  SQLException {
        close(null, myStmt, null);
    }

    private void close(Statement myStmt, ResultSet myRs) throws SQLException {
        close(null, myStmt, myRs);
    }

    private void close(Connection myConn, Statement myStmt, ResultSet myRs) throws SQLException {
        if(myRs != null) {
            myRs.close();
        }

        if(myStmt != null) {

        }

        if(myConn != null) {
            myConn.close();
        }
    }
}
