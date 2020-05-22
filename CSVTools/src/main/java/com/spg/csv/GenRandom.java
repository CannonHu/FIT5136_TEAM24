package com.spg.csv;

import java.security.SecureRandom;

public class GenRandom {
    private static char[] allChars = { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
            'p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K',
            'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6',
            '7','8','9','`','~','!','@','#','$','%','^','&','*','(',')','-','=','_','+','[',']',
            '{','}','|',';',':','\'','"','\\',',','.','/','<','>','?' };

    public static String genRandomString(int len) throws IllegalArgumentException {
        StringBuffer sb = new StringBuffer();
        if (len < 0) {
            throw new IllegalArgumentException("The Given Length of a String must be POSITIVE");
        }
        byte[] seq = new byte[32];
        SecureRandom sr = new SecureRandom();

        for(int i = 0; i < len; i += seq.length) {
            sr.setSeed(System.currentTimeMillis());
            sr.nextBytes(seq);
            for(int j = 0; j < seq.length && i + j < len; j++) {
                int index = (seq[j] < 0) ? 0 - seq[j] : seq[j];
                index = index % allChars.length;
                sb.append(allChars[index]);
            }
        }
        return sb.toString();
    }

    public static String genRandomString(int minLen, int maxLen) {
        int len = 0;
        if (minLen > maxLen) {
            throw new IllegalArgumentException("The Minimum Length of a random string must not larger than the Maximum Length");
        } else if (minLen == maxLen) {
            len = minLen;
        } else {
            len = (int) (Math.random() * (maxLen - minLen + 1)) + minLen;
        }
        return genRandomString(len);
    }
}
