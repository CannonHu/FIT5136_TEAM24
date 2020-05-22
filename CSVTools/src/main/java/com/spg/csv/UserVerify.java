package com.spg.csv;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

public class UserVerify {
    private CSVFileDatabase adminAuthDb;
    private CSVFileDatabase coordinateAuthDb;
    private CSVFileDatabase candidateAuthDb;

    private class SearchUsername {
        private Map<String, String> userRecord = null;
        private Thread[] threads = new Thread[3];

        private void interuptOtherThreads(Thread curThread) {
            for(Thread iThread : threads) {
                if (iThread != null) {
                    if (iThread != curThread) {
                        iThread.interrupt();
                    }
                }
            }
        }

        public Map<String, String> search(String username) {
            userRecord = null;
            threads[0] = new Thread(new Runnable() {
               @Override
               public void run() {
                   if (userRecord == null) {
                       List<Map<String, String>> records = adminAuthDb.searchRecord("username", username);
                       if (records != null && records.size() != 0) {
                           userRecord = records.get(0);
                           interuptOtherThreads(threads[0]);
                       }
                   }
               }
            });

            threads[1] = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (userRecord == null) {
                        List<Map<String, String>> records = coordinateAuthDb.searchRecord("username", username);
                        if (records != null && records.size() != 0) {
                            userRecord = records.get(0);
                            interuptOtherThreads(threads[1]);
                        }
                    }
                }
            });

            threads[2] = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (userRecord == null) {
                        List<Map<String, String>> records = candidateAuthDb.searchRecord("username", username);
                        if (records != null && records.size() != 0) {
                            userRecord = records.get(0);
                            interuptOtherThreads(threads[2]);
                        }

                    }
                }
            });

            for(Thread iThread : threads)
                iThread.start();

            try {
                for(Thread iThread : threads)
                    iThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return userRecord;
        }
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
        for(byte b : bytes) {
            String tmp = Integer.toHexString(b & 0xff);
            if (tmp.length() < 2)
                sb.append('0');
            sb.append(tmp);
        }
        return sb.toString();
    }

    public Map<String, String> getUser(String username) {
        return new SearchUsername().search(username);
    }

    public UserVerify(Map<String, String> csvDbs) throws URISyntaxException {
        String basePath = UserVerify.class.getClassLoader().getResource("").toURI().getPath();
        basePath = basePath.substring(0, basePath.length() - 8);
        String targetDir = "data/user/";

        if (csvDbs.size() != 3) {
            throw new RuntimeException("The User Verify CSVFile Database can not init because the NUMBER of provided CSV File Name is not 3 (" + csvDbs.size() + ")");
        }

        try {
            StringBuffer sb = new StringBuffer(basePath);
            basePath = sb.append(targetDir).toString();
            sb.setLength(0);
            String fileName = null;
            if ((fileName = csvDbs.get("admin")) != null) {
                sb.append(basePath);
                sb.append(fileName);
                adminAuthDb = new CSVFileDatabase(sb.toString());
            }

            sb.setLength(0); fileName = null;
            if ((fileName = csvDbs.get("coordinate")) != null) {
                sb.append(basePath);
                sb.append(fileName);
                coordinateAuthDb = new CSVFileDatabase(sb.toString());
            }

            sb.setLength(0); fileName = null;
            if ((fileName = csvDbs.get("candidate")) != null) {
                sb.append(basePath);
                sb.append(fileName);
                candidateAuthDb = new CSVFileDatabase(sb.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("The User Verify CSVFile Database init ERROR");
        }
    }
}
