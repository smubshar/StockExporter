package com.shahrukhm.controller;

import com.shahrukhm.view.MainApp;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;

/**
 * Created by shahrukhmubshar on 4/9/16.
 * Upload: Serves as a control in MVC pattern.
 * Purpose: Upload a local file to a remote server via FTP.
 */
public class Upload {
    private FTPClient ftpClient;
    private MainApp mainApp;
    private String destination;

    /**
     * Constructs a new Upload object with a single parameter being the calling form.
     * @param mainApp - Used in the case of displaying message dialog.
     * @throws IOException - If the properties file source is invalid. Unable to connect to server.
     */
    public Upload(MainApp mainApp) throws IOException {
        this.mainApp = mainApp;

        // 1. Get server credentials from properties file.
        Properties properties = new Properties();
        properties.load(new FileInputStream("SpeedwayDatabaseTool.properties"));

        String server = properties.getProperty("server");
        int port = Integer.parseInt(properties.getProperty("port"));
        String serverUser = properties.getProperty("server_user");
        String serverPass = properties.getProperty("server_pass");
        destination = properties.getProperty("destination");

        // 2. Initialize ftpclient and enter local passive mode.
        ftpClient =  new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(serverUser, serverPass);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    /**
     * Upload local file to remove server with a single parameter to identify destination filename.
     * @param filename - Name for file on remote server.
     */
    public void uploadFile(String filename) {
        try {
            // 1. Get local file for upload.
            File localFile = new File(filename);
            InputStream inputStream = new FileInputStream(localFile);

            // 2. Upload file to destination.
            boolean done = ftpClient.storeFile(destination + filename, inputStream);

            // 3. Close input stream and display appropriate message.
            inputStream.close();
            if(done) JOptionPane.showMessageDialog(mainApp, "Sucessfully uploaded file.", "Success", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(mainApp, "Unsucessfully uploaded file.", "Error", JOptionPane.ERROR_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainApp, "Error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {

            // 4. Close ftp connection.
            try {
                if(ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainApp, "Error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
