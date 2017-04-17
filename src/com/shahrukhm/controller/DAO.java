package com.shahrukhm.controller;

import com.shahrukhm.model.Authentication;
import com.shahrukhm.model.ObservableProperties;
import com.shahrukhm.model.Product;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Class that defines DAO behavior.
 */
public abstract class DAO implements Observer {
    final static Logger logger = Logger.getLogger(PasswordForm.class.getName());

    protected ObservableProperties observableProperties;
    protected Authentication authentication;
    protected boolean isConnected;
    protected Connection myConn;

    /**
     * Constructor to generate DAO object.
     * @param authentication Object with user credentials.
     * @param observableProperties Configurable properties.
     */
    public DAO(Authentication authentication, ObservableProperties observableProperties) {
        isConnected = false;
        this.authentication = authentication;
        this.observableProperties = observableProperties;
    }

    /**
     * Determine if DAOIdeal has a valid connection.
     * @return true if connected, otherwise false.
     */
    public boolean isConnected() {
        if (myConn == null) return  false;
        return isConnected;
    }

    /**
     * Connects the DAOIdeal object using initial credentials and properties file path.
     * @throws SQLException if url or authentication was invalid.
     */
    public abstract void connect() throws SQLException;

    /**
     * Generates a list of products composed of all entries from the table PRODUCTLOCATION.
     * @return A list of type Product.
     * @throws SQLException If the query is invalid or called on a closed result set.
     */
    public abstract List<Product> getAllProducts() throws SQLException;

    /**
     * Convert a row from the result set into a product.
     * @param myRs Result set obtained from getAllProducts() method.
     * @return the product generated from result set row.
     * @throws SQLException If column labels are invalid or database access error occurs.
     */
    protected abstract Product convertRowToProduct(ResultSet myRs) throws SQLException;

    protected void close(Statement myStmt) throws  SQLException {
        close(null, myStmt, null);
    }

    protected void close(Statement myStmt, ResultSet myRs) throws SQLException {
        close(null, myStmt, myRs);
    }

    /**
     * Close connection with database.
     * @param myConn is the active connection object.
     * @param myStmt
     * @param myRs is the active result set object.
     * @throws SQLException If a database error occurs.
     */
    protected void close(Connection myConn, Statement myStmt, ResultSet myRs) throws SQLException {
        if(myRs != null) {
            myRs.close();
        }

        if(myConn != null) {
            myConn.close();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            connect();
        } catch (SQLException e) {
            isConnected = false;
            logger.debug("DAOIdeal: Cannot connect due to SQLException.");
        }
    }
}
