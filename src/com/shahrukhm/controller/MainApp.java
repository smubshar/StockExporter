package com.shahrukhm.controller;

import com.shahrukhm.model.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

/**
 * A class to view database functionality and status.
 */
public class MainApp extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final String APP_NAME = "Speedway Database Tool";
    private static final String LIB_PATH = "lib/";
    private static final String LOGO = "logo.gif";

    final static Logger logger = Logger.getLogger(PasswordForm.class.getName());


    private JPanel panelRoot;
    private JPanel panelView;
    private JPanel panelControls;
    private JPanel panelTitle;
    private JButton buttonStock;
    private JLabel labelTitle;
    private JTextArea logTextArea;
    private PropertiesEditor propertiesEditor;

    private BackgroundRequestStream backgroundRequestStream;
    private ObservableProperties observableProperties;
    private Authentication dbAuthentication, serverAuthentication, serverDBAuthentication;

    /**
     * Construct a new view.
     */
    public MainApp(BackgroundRequestStream backgroundRequestStream, ObservableProperties observableProperties,
                   Authentication dbAuthentication, Authentication serverAuthentication, Authentication serverDBAuthentication) {
        setTitle(APP_NAME);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        this.backgroundRequestStream = backgroundRequestStream;
        this.observableProperties = observableProperties;
        this.dbAuthentication = dbAuthentication;
        this.serverAuthentication = serverAuthentication;
        this.serverDBAuthentication = serverDBAuthentication;
        propertiesEditor = new PropertiesEditor(observableProperties);

        try {
            init();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to construct main app due to IO Exception.",
                    "Construction Error", JOptionPane.ERROR_MESSAGE);
            logger.debug("MainApp: Unable to construct main app due to IO Exception.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Unable to construct main app ui due to SQLException.",
                    "Construction Error", JOptionPane.ERROR_MESSAGE);
            logger.debug("MainApp: Unable to construct main app ui due to SQLException.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to construct main app ui.",
                    "Construction Error", JOptionPane.ERROR_MESSAGE);
            logger.debug("MainApp: Unable to construct main app ui.");
        }
    }

    // GUI and listener initialization.
    private void init() throws Exception {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(panelRoot);
        labelTitle.setIcon(new ImageIcon(LIB_PATH + LOGO));
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        createMenu();
        setListeners();

        setVisible(true);
    }

    // Setup file menu system
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        JMenuItem quitItem = new JMenuItem("Quit");
        JMenuItem propertiesItem = new JMenuItem("Edit Properties");

        fileMenu.add(quitItem);
        editMenu.add(propertiesItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        panelTitle.add(menuBar, BorderLayout.NORTH);

        quitItem.addActionListener((ActionEvent ae) -> {
            doQuit();
        });

        propertiesItem.addActionListener((ActionEvent ae) -> {
            doURL();
        });
    }

    // Setup listeners.
    private void setListeners() {
        buttonStock.addActionListener((ActionEvent ae) -> {
            doGetStock();
        });
    }

    // Get stock manually using BackgroundRequestStream object.
    private void doGetStock() {
        boolean b = backgroundRequestStream.requestDownloadAndUpload(BackgroundRequestStream.Request.MANUAL);
        if (!b) {
            logger.debug("MainApp: Queue is full, unable to submit request at this time.");
        }
    }

    // Quit application.
    private void doQuit() {
        dbAuthentication.deletePassword();
        serverAuthentication.deletePassword();
        serverDBAuthentication.deletePassword();
        System.exit(0);
    }

    private void doURL() {
        propertiesEditor.show();
    }
}
