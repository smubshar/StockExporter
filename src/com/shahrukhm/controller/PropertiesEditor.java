package com.shahrukhm.controller;

import com.shahrukhm.model.ObservableProperties;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Created by shahrukhmubshar on 12/9/16.
 */
public class PropertiesEditor extends JFrame {

    private static final String APP_NAME = "Properties Editor";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;

    private JPanel rightPanel;
    private JPanel leftPanel;
    private JList propertiesList;
    private JTextField keyTextField;
    private JLabel propertyLabel;
    private JLabel valueLabel;
    private JButton saveButton;
    private JPanel rootPanel;
    private JTextArea valueTextArea;
    private MouseListener mouseListener;
    private KeyListener keyListener;

    private DefaultListModel<String> listModel;
    private ObservableProperties observableProperties;

    /**
     * GUI form for managing properties.
     * @param observableProperties Object that acts as a DAO for properties.
     */
    public PropertiesEditor(ObservableProperties observableProperties) {
        this.observableProperties = observableProperties;
        try {
            init();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.toString(),
                    "Error - Init", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Setup GUI form.
    private void init() throws Exception {
        super.setTitle(APP_NAME);
        super.setSize(WIDTH, HEIGHT);

        initList();
        initButton();
        add(rootPanel);
    }

    // Setup list object with properties.
    private void initList() throws IOException {
        observableProperties.load();
        valueTextArea.setLineWrap(true); valueTextArea.setWrapStyleWord(true);

        listModel = new DefaultListModel<String>();
        Iterator it = observableProperties.keysIterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            listModel.addElement(key);
        }

        propertiesList.setModel(listModel);

        propertiesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String key = (String) propertiesList.getSelectedValue();
                doSetTextFields(key);
            }
        });
    }

    // Set text fields.
    private void doSetTextFields(String key) {
        String value = observableProperties.getProperty(key);

        keyTextField.setText(key);
        valueTextArea.setText(value);
    }

    // Setup save action.
    private void initButton() {
        saveButton.addActionListener((ActionEvent ae) -> {
            doSaveValue();
        });
    }

    // Save changed properties in properties object.
    private void doSaveValue() {
        String key = (String) propertiesList.getSelectedValue();
        String updatedValue = valueTextArea.getText();
        try {
            observableProperties.setProperty(key, updatedValue);
            observableProperties.load();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unsuccessful save: " + e , "Save", JOptionPane.WARNING_MESSAGE);
        }
    }
}
