package team.se24.employfast.service;

import java.security.SecureRandom;
import java.util.Base64;

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

    public String getStaticSaltVal() {
        return Base64.getEncoder().encodeToString(staticSaltVal);
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
