package com.shahrukhm.controller;

import com.shahrukhm.model.Authentication;
import com.shahrukhm.model.ObservableProperties;
import com.shahrukhm.model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class of objects that interface between destination database and caller.
 */
public class DAOMagento extends DAO {

    private static final String DB_URL = "serverdburl";
    private static final String QUERY = "SELECT * FROM catalog_product_entity";
    private static final String SKU = "sku";

    /**
     * Creates a new DAOIdeal object.
     * @throws IOException - If the properties file source is invalid.
     * @throws SQLException - If unable to access database, might be due to invalid credentials.
     */
    public DAOMagento(ObservableProperties observableProperties, Authentication authentication) throws IOException, SQLException {
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
        String partNumber = myRs.getString(SKU);

        // 2. Generate product.
        Product tempProduct = new Product(partNumber, BigDecimal.ZERO);
        return tempProduct;
    }
}
