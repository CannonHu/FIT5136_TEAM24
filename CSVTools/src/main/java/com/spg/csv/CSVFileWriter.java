package com.spg.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.List;
import java.util.Map;

public class CSVFileWriter {
    private String[] attributes;
    private boolean hasAttrLine;
    private int writtenLineNum;
    private String filePath;
    private CSVWriter writer;

    private static boolean isSpecialChar(char c) {
        return c == ',' || c == '"' || c == '\n' || c == ' ' || c == '\t';
    }

    public static String generateRecord(String[] values) {
        StringBuffer recordSb = new StringBuffer();

        for (String v : values) {
            boolean doubleQuotation = false;
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < v.length(); i++) {
                char c = v.charAt(i);
                if (isSpecialChar(c)) {
                    doubleQuotation = true;
                    if (c == '"')
                        sb.append(c);
                }
                sb.append(c);
            }
            if (doubleQuotation) {
                sb.insert(0, '"');
                sb.append('"');
            }

            if(recordSb.length() != 0)
                recordSb.append(',');
            recordSb.append(sb);
        }

        recordSb.append('\n');
        return recordSb.toString();
    }

    public void writeNextRecord(String[] values) throws RuntimeException, IllegalArgumentException {
        if (values == null) {
            throw new IllegalArgumentException("The Record String Array must not be NULL");
        }
        if (values.length > attributes.length) {
            throw new RuntimeException("This record contains more values: " + values.length + " than expected " + attributes.length);
        }

        writer.writeNext(values);
    }

    public void writeNextRecord(Map<String, String> valuesMap) throws RuntimeException, IllegalArgumentException {
        int mapSize = valuesMap.size();
        if (valuesMap == null || mapSize == 0) {
            throw new IllegalArgumentException("The Record String Array must not be NULL or BLANK");
        }

        if (mapSize > attributes.length) {
            throw new RuntimeException("This record contains more values: " + mapSize + " than expected " + attributes.length);
        }

        String[] values = new String[mapSize];
        int i = 0;
        for(String curAttr : attributes) {
            values[i++] = valuesMap.get(curAttr);
        }
        writer.writeNext(values);
    }

    public void writeAllString(List<String[]> valueList) throws RuntimeException, IllegalArgumentException {
        if (valueList == null || valueList.size() == 0) {
            throw new IllegalArgumentException("The Record String Array must not be NULL or BLANK");
        }
        for (String[] v : valueList) {
            writeNextRecord(v);
        }
    }

    public void writeAllMap(List<Map<String, String>> mapList) throws RuntimeException, IllegalArgumentException {
        if (mapList == null || mapList.size() == 0) {
            throw new IllegalArgumentException("The Record String Array must not be NULL or BLANK");
        }
        for (Map m : mapList) {
            writeNextRecord(m);
        }
    }

    public CSVFileWriter(String filePath, String[] attributes, boolean hasAttrLine) throws IllegalArgumentException {
        if (attributes == null || attributes.length == 0) {
            throw new IllegalArgumentException("The Attributes Array Parameter must not be NULL");
        }
        this.attributes = attributes;

        this.filePath = filePath;

        File csvFile = new File(filePath);
        try {
            if (!csvFile.exists()) {
                csvFile.createNewFile();
            }
            writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "utf-8"));

            if (hasAttrLine)
                writer.writeNext(attributes);
        }
        catch(FileNotFoundException e) {
            System.out.println(e.toString() + ":\nThe File Path " + filePath + " is invalid");
            System.exit(-1);
        }
        catch(IOException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    public CSVFileWriter(String filePath, String[] attributes) throws IllegalArgumentException {
        this(filePath, attributes, true);
    }
}
