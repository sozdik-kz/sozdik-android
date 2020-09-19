package kz.sozdik.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String getHash(String str) {
        MessageDigest md5;
        StringBuilder hexString = new StringBuilder();
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            try {
                md5.update(str.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte messageDigest[] = md5.digest();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xff & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            return e.toString();
        }
        return hexString.toString();
    }
}
