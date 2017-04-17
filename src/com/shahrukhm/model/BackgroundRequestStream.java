package com.shahrukhm.model;

import com.shahrukhm.controller.DAOIdeal;
import com.shahrukhm.controller.DAOMagento;
import com.shahrukhm.controller.PasswordForm;
import com.shahrukhm.controller.Upload;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A class to handle events and exectue them sequentially.
 */
public class BackgroundRequestStream implements Observer {

    final static Logger logger = Logger.getLogger(PasswordForm.class.getName());


    @Override
    public void update(Observable o, Object arg) {
        queue.clear();
    }

    public enum Request {
        AUTO_GET, MANUAL
    }

    private static final int QUEUE_CAPACITY = 2;
    private static final String LIB_PATH = "lib/";
    private static final String STOCK_FILE = "stock.csv";

    private DAOIdeal daoIdeal;
    private DAOMagento daoMagento;
    private Upload upload;
    public final BlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(QUEUE_CAPACITY);

    /**
     * Constructor to generate background request stream object.
     * @param daoIdeal is the dao object for the POS.
     * @param daoMagento is the dao object for the destination.
     * @param upload is the object to manage uploading of data.
     */
    public BackgroundRequestStream(DAOIdeal daoIdeal, DAOMagento daoMagento, Upload upload) {
        this.daoIdeal = daoIdeal;
        this.daoMagento = daoMagento;
        this.upload = upload;
        try {
            daoIdeal.connect();
            daoMagento.connect();
        } catch (SQLException e) {
            logger.debug("BackgroundRequestStream: Unable to connect DAO.");
        }
        try {
            upload.connect();
        } catch (IOException e) {
            logger.debug("BackgroundRequestStream: Unable to connect Upload object.");
        }

        new Thread(() -> drainQueue()).start();
    }

    /**
     * Get object from blocking queue and execute inventory update.
     */
    private void drainQueue() {
        try {
            while (true) {
                Request request = queue.take();
                System.out.println("DAO - " + daoMagento.isConnected());
                    if ( daoIdeal.isConnected() && daoMagento.isConnected() && upload.isConnected()
                            && (request == Request.AUTO_GET || request == Request.MANUAL)) {
                        System.out.println("Entered");
                        List<Product> idealList = daoIdeal.getAllProducts();
                        List<Product> magentoList = daoMagento.getAllProducts();
                        List<Product> list = new ArrayList<Product>();
                        LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();
                        for(Product p : idealList) {
                            map.put(p.getPartNumber(), p);
                        }
                        for(Product p : magentoList) {
                            if (!map.containsKey(p.getPartNumber())) {
                                System.out.println(p.getPartNumber());
                                p.setQuantity(BigDecimal.ZERO);
                                map.put(p.getPartNumber(), p);
                            }
                        }
                        for(Map.Entry<String, Product> entry : map.entrySet()) {
                            list.add(entry.getValue());
                        }

                        CSV myCSV = new CSV(list);
                        int i = myCSV.writeCSV(LIB_PATH + STOCK_FILE);
                        File f = new File(LIB_PATH + STOCK_FILE);
                        boolean b = upload.uploadFile(f, STOCK_FILE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        Date now = new Date();
                        String strDate = sdf.format(now);
                        logger.debug(strDate + " - Queue result in - " + b);
                    } else {
                        System.out.println("Failed");
                    }

            }
        } catch (InterruptedException e) {
            logger.debug("BackgroundRequestStream: Unable to complete request due to InterruptedException.");
        } catch (IOException e) {
            logger.debug("BackgroundRequestStream: Unable to complete request due to IOException.");
        } catch (SQLException e) {
            logger.debug("BackgroundRequestStream: Unable to complete request due to SQLException.");
        }

        System.out.println("Completed");
    }

    /**
     * Add an element to the concurrent queue.
     * @param request to ask, cannot be null.
     * @return true if the element could be added.
     */
    public boolean requestDownloadAndUpload(Request request) {
        return queue.offer(request);
    }
}
