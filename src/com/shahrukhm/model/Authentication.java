package com.shahrukhm.model;

/**
 * A class to handle user credentials.
 */
public class Authentication {
    private String user;
    private char[] password;

    /**
     * Constructor for authentication object.
     * @param user Username credential.
     * @param password Password stored as a char array.
     */
    public Authentication(String user, char[] password) {
        this.user = user;
        this.password = password;
    }

    /**
     * Delete password form memory.
     */
    public void deletePassword() {
        for (int i=0; i < password.length; i++) {
            password[i] = '0';
        }
    }

    /**
     * Get username credential.
     * @return String value of username credential.
     */
    public String getUser() {
        return user;
    }

    /**
     * Get password.
     * @return password as a char array.
     */
    public char[] getPassword() {
        return password;
    }
}
