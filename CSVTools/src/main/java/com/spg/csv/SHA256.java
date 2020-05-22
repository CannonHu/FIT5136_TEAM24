package com.spg.csv;

import java.security.MessageDigest;
import java.util.Base64;

public class SHA256 {
    private static final byte[] staticSaltVal = {62, -87, -49, 121, -44, -16, -43,
            99, -99, -35, -112, -117, 3, -7, -13, -9, 35, 48, 8, 100, 25,
            -110, -59, -79, 117, 127, 37, 68, -75, 117, 44, -121};

    public static String getStaticSalt() {
        return Base64.getEncoder().encodeToString(staticSaltVal);
    }

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
        for (byte b : bytes) {
            String tmp = Integer.toHexString(b & 0xff);
            if (tmp.length() < 2)
                sb.append('0');
            sb.append(tmp);
        }
        return sb.toString();
    }
}
