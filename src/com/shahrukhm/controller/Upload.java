package com.shahrukhm.controller;

import com.shahrukhm.model.Authentication;
import com.shahrukhm.model.ObservableProperties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Observable;
import java.util.Observer;

/**
 * A class for objects which handle file upload.
 */
public class Upload implements Observer {

    private static final String SERVER = "server";
    private static final String PORT = "port";
    private static final String DESTINATION = "destination";

    final static Logger logger = Logger.getLogger(PasswordForm.class.getName());


    private FTPClient ftpClient;
    private String destination;
    private ObservableProperties observableProperties;
    private Authentication authentication;

    /**
     * Constructs a new Upload object.
     * @throws IOException - If the properties file source is invalid. Unable to connect to server.
     */
    public Upload(ObservableProperties observableProperties, Authentication authentication) throws IOException{
        this.observableProperties = observableProperties;
        this.authentication = authentication;
        observableProperties.load();
    }

    /**
     * Connects the Upload object using the initial credentials and paths from properties file.
     * @throws IOException if unable to find ObservableProperties.
     */
    public void connect() throws IOException {
        String server = observableProperties.getProperty(SERVER);
        int port = Integer.parseInt(observableProperties.getProperty(PORT));
        String user = authentication.getUser();
        String password = new String(authentication.getPassword());
        destination = observableProperties.getProperty(DESTINATION);

        // 2. Initialize ftpclient and enter local passive mode.
        ftpClient =  new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    /**
     * Determines if Upload object has a valid connection.
     * @return true is a valid connection exists, otherwise false.
     */
    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    /**
     * Upload file to destination.
     * @param f File to be uploaded.
     * @param filename Name to write on upload.
     * @return If upload was successful.
     * @throws IOException when unable to store file.
     */
    public boolean uploadFile(File f, String filename) throws IOException {
        boolean done = false;

        // 1. Get local file for upload.
        File localFile = f;
        InputStream inputStream = new FileInputStream(localFile);

        // 2. Upload file to destination.
        done = ftpClient.storeFile(destination + filename, inputStream);

        // 3. Close FTPClient and InputStream.
        if (ftpClient.isConnected()) {
            ftpClient.enterLocalPassiveMode();
        }
        inputStream.close();

        return done;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            connect();
        } catch (IOException e) {
            logger.debug("Upload: Cannot connect due to IOException.");
        }
    }
}
