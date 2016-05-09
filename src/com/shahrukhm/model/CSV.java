package com.shahrukhm.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by shahrukhmubshar on 4/2/16.
 *
 * CSV: Represents view in MVC pattern.
 * Purpose: To make the data in the format of CSV, so it may be usable in updating records.
 */
public class CSV {

    // CSV setup.
    private static final String NEW_LINE_SEPERATOR = "\n";
    private static final Object[] FILE_HEADER = { "store", "sku", "qty", "is_in_stock"};

    // List of products that'll be placed in CSV.
    private List<Product> productArrayList;

    /**
     * Create a new CSV object with the parameter: a list of products.
     * @param productArrayList - List of products.
     */
    public CSV(List<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    /**
     * Create or update the CSV and output the file.
     * @param filename - Name of the CSV file.
     * @return number of succesfully created/updated products.
     */
    public int writeCSV(String filename) {
        FileWriter fileWriter = null;
        CSVPrinter csvPrinter= null;
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPERATOR).withQuoteMode(QuoteMode.ALL);


        try {
            fileWriter = new FileWriter(filename);
            csvPrinter = new CSVPrinter(fileWriter, csvFormat);
            csvPrinter.printRecord(FILE_HEADER);

            for(Product product : productArrayList) {
                csvPrinter.printRecord(product.getProductDataRecord());
            }

            return productArrayList.size();
        } catch(Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvPrinter.close();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        return -1;
    }
}
