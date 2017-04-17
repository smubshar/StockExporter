package com.shahrukhm.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Properties;

/**
 * A class to manage properties of application.
 */
public class ObservableProperties extends Observable {

    private String path;
    private Properties properties;

    /**
     * Constructor generating an observable properties object.
     * @param path is the file location.
     */
    public ObservableProperties(String path)  {
        this.path = path;
        properties = new Properties();
    }

    /**
     * Load the properties from path.
     * @throws IOException
     */
    public void load() throws IOException{
        FileInputStream fileInputStream = new FileInputStream(path);
        properties.load(fileInputStream);
        fileInputStream.close();
        setChanged();
        notifyObservers();
    }

    /**
     * Get a property value by key.
     * @param key is the properties name.
     * @return the properties value as a string.
     */
    public String getProperty(String key) {
        String value = "";
        value = properties.getProperty(key);
        return value;
    }

    /**
     * Set a property.
     * @param key is the properties name.
     * @param value is the properties new value.
     * @throws IOException if unable to generate a stream.
     */
    public void  setProperty(String key, String value) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        properties.setProperty(key, value);
        properties.store(fileOutputStream, null);
        fileOutputStream.close();
    }

    /**
     * Generate an iterator for the keys.
     * @return an iterator.
     */
    public Iterator<Object> keysIterator() {
        return properties.keySet().iterator();
    }
}
