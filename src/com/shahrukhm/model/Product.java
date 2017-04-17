package com.shahrukhm.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahrukhmubshar on 4/2/16.
 *
 * Product: Represents model in MVC pattern.
 * Purpose: Hold required product attributes.
 */
public class Product {

    // Key product attributes.
    private String partNumber;
    private BigDecimal quantity;
    private static final String store = "admin";
    private int isInStock = 0;

    /**
     * Product constructor. Requires at least and id and quantity.
     * @param partNumber - Some form of identification of the product.
     * @param quantity - Quantity on hand of product.
     */
    public Product(String partNumber, BigDecimal quantity) {
        this.partNumber = partNumber;
        this.quantity = quantity;
        if(quantity.compareTo(BigDecimal.ZERO) > 0) isInStock = 1;
    }

    /**
     * Get the part number of a product.
     * @return Returns the products part number.
     */
    public String getPartNumber() {
        return partNumber;
    }

    /**
     * Get the quanitity of a particular product.
     * @return Returns quantity of calling product.
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    /**
     * Generate a list of the products attributes.
     * @return List of products attributes.
     */
    public List<Product> getProductDataRecord() {
        List productDataRecord = new ArrayList();
        productDataRecord.add(store);
        productDataRecord.add(partNumber);
        productDataRecord.add(quantity);
        productDataRecord.add(isInStock);

        return productDataRecord;
    }

    /**
     * String representation of products part number and quantity.
     * @return String of part number and quantity.
     */
    @Override
    public String toString() {
        return partNumber + ", " + quantity;
    }
}