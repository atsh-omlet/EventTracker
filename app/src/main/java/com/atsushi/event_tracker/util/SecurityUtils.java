package com.atsushi.event_tracker.util;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Security utility for hashing and checking passwords
 */
public class SecurityUtils {

    /**
     * Hashes a password using BCrypt.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if a password matches a hashed password using BCrypt.
     * @param password The password to check.
     * @param hashedPassword The hashed password to compare against.
     * @return True if the password matches the hashed password, false otherwise.
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}