package com.shahrukhm.view;

import com.shahrukhm.controller.DAO;
import com.shahrukhm.controller.Upload;
import com.shahrukhm.model.CSV;
import com.shahrukhm.model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by shahrukhmubshar on 4/2/16.
 *
 * MainApp: Serves as view in MVC design pattern.
 * Purpose: GUI for user to perform operations.
 */

public class MainApp extends JFrame {
    // Define width and height for form.
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    // Components of form.
    private JPanel panelRoot;
    private JLabel labelTitle;
    private JPanel panelTitle;
    private JPanel panelControls;
    private JPanel panelView;
    private JButton buttonStock;
    private JButton buttonUpload;

    // DAO to communicate between view and model.
    private DAO dao;

    // Create window.
    public MainApp() {
        setTitle("Speedway Database Tool");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(panelRoot);
        setListeners();
        setVisible(true);

        try {
            dao = new DAO();
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this, "Error: " + exc, "Error - DAO", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Button for obtaining stock.
    private void setListeners() {
        buttonStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    String fileName = "stock.csv";
                    int successCount = 0;

                    List<Product> productArrayList = dao.getAllProducts();
                    CSV myCSV = new CSV(productArrayList);
                    successCount = myCSV.writeCSV(fileName);

                    rootPane.setCursor(Cursor.getDefaultCursor());
                    JOptionPane.showMessageDialog(MainApp.this, "Succesfully exported " + successCount + " products.",
                            "Export Summary", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(MainApp.this, "Error: " + exc, "Error - CSV", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        // Button for uploading data to server.
        buttonUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    Upload myUpload = new Upload(MainApp.this);
                    myUpload.uploadFile("stock.csv");

                    rootPane.setCursor(Cursor.getDefaultCursor());
                } catch (Exception exc2) {
                    JOptionPane.showMessageDialog(MainApp.this, "Error: " + exc2, "Error - Upload", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        MainApp reportApp = new MainApp();
    }
}
