package com.spg.csv;

import java.util.HashMap;
import java.util.Map;

public class TestCSV {


    public static void main(String[] args) throws Exception {
        String basePath = TestCSV.class.getClassLoader().getResource("").toURI().getPath();
        basePath = basePath.substring(0, basePath.length() - 8);
        String targetDir = "data/user/";
        String fromCsvNames[] = {"adminRaw.csv", "coordinateRaw.csv", "candidateRaw.csv"};
        String targetCsvNames[] = {"admin.csv", "coordinate.csv", "candidate.csv"};

        String dirPath = basePath + targetDir;
        String staticSalt = SHA256.getStaticSalt();
        for(int i = 0; i < fromCsvNames.length; i++) {
            int initId = 0;
            switch(i) {
                case 0:
                    initId = 0;
                    break;
                case 1:
                    initId = 4000;
                    break;
                case 2:
                    initId = 7000;
                    break;
            }
            CSVBufferedReader csvReader = new CSVBufferedReader(dirPath + fromCsvNames[i]);
            CSVFileDatabase usersDb = new CSVFileDatabase(dirPath + targetCsvNames[i]);
            String[] recordAttrs = usersDb.getHeader();
            Map<String, String> rawUserRecord = null;

            while((rawUserRecord = csvReader.readNextRecord()) != null) {
                HashMap<String, String> userRecord = new HashMap<String, String>();
                String name = rawUserRecord.get("username");
                String passwd = rawUserRecord.get("passwd");

                userRecord.put(recordAttrs[0], Integer.toString(initId++));
                userRecord.put(recordAttrs[1], name);
                userRecord.put(recordAttrs[2], SHA256.sha256Hash(passwd + staticSalt));
                usersDb.addRecord(userRecord);
            }
        }




/*
        com.spg.csv.CSVFileReader csvReader = new com.spg.csv.CSVFileReader(basePath + testCsvName);
        for(String[] line : csvReader.readAllLines()) {
            if (line == null)
                System.out.println("null");
            else {
                for(String s : line)
                    System.out.print(s + ", ");
            }
            System.out.println();
        }
        csvReader.close();*/
/*
        com.spg.csv.CSVFileDatabase testDb = new com.spg.csv.CSVFileDatabase(testCsvPath);

        HashMap<String, String> searchCondMap = new HashMap<String, String>();
        searchCondMap.put("name", "Lionel Wyatt");
        for(Map<String, String> r : testDb.searchRecord(searchCondMap)) {
            System.out.println(r);
        }*/

    }
}
