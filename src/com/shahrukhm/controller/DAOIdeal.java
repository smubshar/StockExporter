package com.shahrukhm.controller;

import com.shahrukhm.model.Authentication;
import com.shahrukhm.model.ObservableProperties;
import com.shahrukhm.model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class of objects that interface between database and caller.
 */
public class DAOIdeal extends DAO {

    private static final String DB_URL = "dburl";
    private static final String QUERY = "SELECT * FROM PRODUCTLOCATION";
    private static final String PART_NUMBER_COL = "PARTNUMBER";
    private static final String ON_HAND_COL = "ONHANDAVAILABLEQUANTITY";

    /**
     * Creates a new DAOIdeal object.
     * @throws IOException - If the properties file source is invalid.
     * @throws SQLException - If unable to access database, might be due to invalid credentials.
     */
    public DAOIdeal(ObservableProperties observableProperties, Authentication authentication) throws IOException, SQLException {
        super(authentication, observableProperties);
        observableProperties.load();
    }

    @Override
    public void connect() throws SQLException {
        String dburl = observableProperties.getProperty(DB_URL);
        String user = authentication.getUser();
        String password = new String(authentication.getPassword());
        myConn = DriverManager.getConnection(dburl, user, password);
        isConnected = true;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        // 1. Declare and initialize array list of type Product.
        List<Product> productArrayList = new ArrayList<>();
        Statement myStmt = null;
        ResultSet myRs = null;
        try {
            // 2. Create statement and execute query.
            myStmt = myConn.createStatement();
            myRs = myStmt.executeQuery(QUERY);

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

    @Override
    protected Product convertRowToProduct(ResultSet myRs) throws SQLException {
        // 1. Get Product data from result set row.
        String partNumber = myRs.getString(PART_NUMBER_COL);
        BigDecimal quantity = myRs.getBigDecimal(ON_HAND_COL);

        // 2. Generate product.
        Product tempProduct = new Product(partNumber, quantity);
        return tempProduct;
    }
}
