package team.se24.employfast.service;

import java.security.SecureRandom;

public class Salt {
    private byte[] dynamicSaltVal;
    private static final byte[] staticSaltVal = {62, -87, -49, 121, -44, -16, -43, 99, -99, -35, -112,
            -117, 3, -7, -13, -9, 35, 48, 8, 100, 25, -110, -59, -79,
            117, 127, 37, 68, -75, 117, 44, -121};

    public byte[] getDynamicSaltVal() {
        return dynamicSaltVal;
    }

    public byte[] getStaticSaltVal() {
        return staticSaltVal;
    }

    public Salt() {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());
        dynamicSaltVal = new byte[24];
        sr.nextBytes(dynamicSaltVal);
    }

    public Salt(char digits) {
        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());

        int dig = 0;
        for (int i = 0; i < digits / 8; i++) {
            long randomLong = sr.nextLong();
            while (randomLong != 0) {
                dynamicSaltVal[dig++] = (byte)(randomLong | 0xff);
                randomLong >>>= 8;
            }
        }

        sr.setSeed(System.currentTimeMillis());
        for (int i = 0; i < digits % 8; i++) {
            dynamicSaltVal[dig++] = (byte)sr.nextInt();
        }
    }
}
