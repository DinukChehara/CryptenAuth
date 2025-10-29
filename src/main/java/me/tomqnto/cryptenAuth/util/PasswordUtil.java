package me.tomqnto.cryptenAuth.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hashes a plain-text password using BCrypt
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Verifies a plain-text password against a hashed password
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
