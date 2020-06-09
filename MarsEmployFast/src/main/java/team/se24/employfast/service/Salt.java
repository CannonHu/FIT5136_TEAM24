package team.se24.employfast.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/*This class is designed for the login servlet.
* It provides a static salt and dynamic salt for the login process*/
public class Salt {
    private static final String dynamicSaltKey = "dynamicSaltVal";
    private static final String staticSaltKey = "staticSaltVal";
    private byte[] dynamicSaltVal;
    private static final byte[] staticSaltVal = {62, -87, -49, 121, -44, -16, -43,
            99, -99, -35, -112, -117, 3, -7, -13, -9, 35, 48, 8, 100, 25,
            -110, -59, -79, 117, 127, 37, 68, -75, 117, 44, -121};

    public String getDynamicSaltVal() {
        return Base64.getEncoder().encodeToString(dynamicSaltVal);
    }

    public static String getStaticSaltVal() {
        return Base64.getEncoder().encodeToString(staticSaltVal);
    }
/*
    public String getDynamicSaltVal() {
        return new String(dynamicSaltVal);
    }

    public String getStaticSaltVal() {
        return new String(staticSaltVal);
    }*/

    public static String sha256Hash(String str) {
        MessageDigest messageDigest = null;
        String hashedStr = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            hashedStr = bytes2HexStr(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashedStr;
    }

    public static String bytes2HexStr(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(byte b : bytes) {
            String tmp = Integer.toHexString(b & 0xff);
            if (tmp.length() < 2)
                sb.append('0');
            sb.append(tmp);
        }
        return sb.toString();
    }

    public Salt() {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());
        dynamicSaltVal = new byte[32];
        sr.nextBytes(dynamicSaltVal);
    }

    public Salt(char digits) {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());

        dynamicSaltVal = new byte[digits];
        sr.nextBytes(dynamicSaltVal);
    }
}
