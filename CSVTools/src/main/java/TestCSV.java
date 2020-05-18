import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCSV {
    public static void main(String[] args) throws Exception {
        String testCsvPath = "E:\\JAVA_EXERCISE\\CSVTools\\src\\main\\resources\\candidates.csv";
/*
        CSVFileReader csvReader = new CSVFileReader(testCsvPath);

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

        CSVFileDatabase testDb = new CSVFileDatabase(testCsvPath);

        HashMap<String, String> searchCondMap = new HashMap<String, String>();
        searchCondMap.put("name", "Lionel Wyatt");
        for(Map<String, String> r : testDb.searchRecord(searchCondMap)) {
            System.out.println(r);
        }
    }
}
