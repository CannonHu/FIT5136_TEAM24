import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class TestCSV {


    public static void main(String[] args) throws Exception {

        /*
        String basePath = TestCSV.class.getClassLoader().getResource("").toURI().getPath();
        basePath = basePath.substring(0, basePath.length() - 8);
        String targetDir = "data/user/";
        String path = basePath + targetDir + "candidate.csv";
        CSVFileDatabase usersDb = new CSVFileDatabase(path);
        usersDb.searchUniqueRecord("username", "tony723");
        usersDb.searchUniqueRecord("username", "NeroChurch");

        File csvFile = new File(path);
        csvFile.delete();
        HashMap<String, String> newUser = new HashMap<>();
        newUser.put("userId", "10100");
        newUser.put("username", "KevinGarotte");
        newUser.put("passwd", SHA256.sha256Hash("abc62526323"));
        usersDb.addRecord(newUser);
        usersDb.modifyUniqueRecord(new CSVFileDatabase.CSVDbModify(CSVFileDatabase.CSVDbModify.ARRAY_RECORD_MOD) {
            @Override
            public boolean modifyRecord(Map<String, String> record) {
                return false;
            }

            @Override
            public boolean modifyRecord(String[] record) {
                if (record[0].equals("10099")) {
                    System.out.println("got it");
                    record[1] = "TrayYoung";
                    return true;
                }
                return false;
            }
        });

        usersDb.flush();

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
        }*/


    }
}
