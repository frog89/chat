package com.franka.chat.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptUtil {
    public static String getHashString(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean comparePassword(String password, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
