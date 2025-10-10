package com.cs360.eventtrackeratsushi.util;
import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}