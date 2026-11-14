package com.campusevents.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility for secure password hashing and verification using SHA-256 with salt.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hashes a password with a newly generated random salt.
     * Returns "salt:hashHex"
     */
    public static String hashPassword(String password) {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        String salt = bytesToHex(saltBytes);
        return hashWithSalt(password, salt);
    }

    /**
     * Hashes a password with a specific salt.
     */
    public static String hashWithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return salt + ":" + bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verifies a plain text password against a stored "salt:hashHex" string.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        if (!storedHash.contains(":")) {
            // Backward compatibility or direct hex check
            return hashWithFixedSalt(password).equalsIgnoreCase(storedHash);
        }

        String[] parts = storedHash.split(":", 2);
        String salt = parts[0];
        String expectedHash = parts[1];

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] actualHashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String actualHash = bytesToHex(actualHashBytes);

            return MessageDigest.isEqual(
                    expectedHash.getBytes(StandardCharsets.UTF_8),
                    actualHash.getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static String hashWithFixedSalt(String password) {
        return hashWithSalt(password, "fixedseed12345678");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

