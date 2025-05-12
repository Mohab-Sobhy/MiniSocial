package com.example.minisocial.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Private constructor to prevent instantiation
    private PasswordUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Encrypts a raw password using BCrypt
     * @param rawPassword the raw password to encrypt
     * @return the encrypted password
     */
    public static String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * Verifies if a raw password matches an encrypted password
     * @param rawPassword the raw password to check
     * @param encryptedPassword the encrypted password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            throw new IllegalArgumentException("Encrypted password cannot be null or empty");
        }
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }
}