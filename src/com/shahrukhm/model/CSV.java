package com.shahrukhm.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Class of objects to represent comma separated values.
 */
public class CSV {

    private static final String NEW_LINE_SEPERATOR = "\n";
    private static final Object[] FILE_HEADER = { "store", "sku", "qty", "is_in_stock"};
    private static final Object[] FILE_HEADER_SKU = { "sku" };

    private List<Product> productArrayList;

    /**
     * Create a new CSV object with the parameter a list of products.
     * @param productArrayList - List of products.
     */
    public CSV(List<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    /**
     * Create or update the CSV and output the file.
     * @param filename - Name of the CSV file.
     * @return number of successfully created/updated products.
     * @throws IOException when error involving file.
     */
    public int writeCSV(String filename) throws IOException {
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
        } finally {
            close(fileWriter, csvPrinter);
        }
    }

    /**
     * Create or update the CSV of skus.
     * @param filename - Name of the CSV file.
     * @return number of successfully created/updated products.
     * @throws IOException when error involving file.
     */
    public int writeSKU(String filename) throws IOException {
        FileWriter fileWriter = null;
        CSVPrinter csvPrinter= null;
        CSVFormat csvFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPERATOR).withQuoteMode(QuoteMode.ALL);


        try {
            fileWriter = new FileWriter(filename);
            csvPrinter = new CSVPrinter(fileWriter, csvFormat);
            csvPrinter.printRecord(FILE_HEADER_SKU);

            for(Product product : productArrayList) {
                csvPrinter.printRecord(product.getPartNumber());
            }

            return productArrayList.size();
        } finally {
            close(fileWriter, csvPrinter);
        }
    }

    private void close(FileWriter fw, CSVPrinter csvPrinter) throws IOException {
        if (fw != null) {
            fw.flush();
            fw.close();
        }

        if (csvPrinter != null) {
            csvPrinter.close();
        }
    }
}
