package com.spg.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.*;

public class CSVFileReader {
    private String[] attributes;
    private boolean haveAttrLine;
    private int coveredLineNum;
    private String filePath;
    private CSVReader reader;

    public String[] getHeader() throws RuntimeException {
        if (attributes == null) {
            throw new RuntimeException("com.spg.csv.CSVFileReader Constructor Implementation Error");
        }
        return attributes;
    }

    public Map<String, String> readNextRecord() throws RuntimeException {
        HashMap<String, String> lineMap = null;
        String[] nextLine = readNextLine();
        if (nextLine != null) {
            lineMap = new HashMap<String, String>();
            for (int i = 0; i < attributes.length; i++) {
                String v = "";
                if (i < nextLine.length) {
                    v = nextLine[i];
                }
                lineMap.put(attributes[i], v);
            }
        }
        return lineMap;
    }

    public String[] readNextLine() throws RuntimeException {
        String[] values = null;
        try {
            values = reader.readNext();

            if (values != null && values.length > attributes.length)
                throw new RuntimeException("This record contains more values: " + values.length + " than expected " + attributes.length);
            else
                coveredLineNum++;
        }
        catch(IOException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
        catch(CsvValidationException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }

        return values;
    }

    public List<Map<String, String>> readRemainRecords() {
        Map<String, String> curRecord = null;
        ArrayList<Map<String, String>> recordsList = new ArrayList<Map<String, String>>();

        while((curRecord = readNextRecord()) != null) {
            recordsList.add(curRecord);
            coveredLineNum++;
        }
        return recordsList;
    }

    public List<String[]> readRemainLines() {
        ArrayList<String[]> linesList = new ArrayList<String[]>();
        String[] curLine = null;

        while((curLine = readNextLine()) != null) {
            linesList.add(curLine);
            coveredLineNum++;
        }
        return linesList;
    }

    public List<Map<String, String>> readAllRecords() {
        reset();
        return readRemainRecords();
    }

    public List<String[]> readAllLines() {
        reset();
        return readRemainLines();
    }

    public int skip(int numberOfLinesToSkip) throws IOException {
        int linesSkipped = 0;
        for(linesSkipped = 0; linesSkipped < numberOfLinesToSkip; linesSkipped++) {
            if(reader.readNextSilently() == null)
                break;
        }
        return linesSkipped;
    }

    public long nextLineNum() {
        return coveredLineNum + 1;
    }

    public void close() {
        try {
            this.reader.close();
        }
        catch(IOException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    public void reset() {
        close();
        File csvFile = new File(filePath);
        try {
            reader = new CSVReader(new InputStreamReader(new FileInputStream(csvFile)));
            coveredLineNum = 0;
            if (haveAttrLine)
                reader.skip(1);
        }
        catch(FileNotFoundException e) {
            System.out.println("The File Path " + filePath + " is invalid");
            System.out.println(e.toString());
            System.exit(-1);
        }
        catch(IOException e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    public CSVFileReader(String filePath) throws Exception {
        this.filePath = filePath;

        File csvFile = new File(filePath);
        try {
            reader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "utf-8")));
            attributes = reader.readNext();
            if (attributes == null) {
                throw new Exception("The Attributes Declaration line should not be blank");
            }
            haveAttrLine = true;

            for(int i = 0; i < attributes.length; i++)
                attributes[i] = attributes[i].trim();
        }
        catch(FileNotFoundException e) {
            System.out.println("The File Path " + filePath + " is invalid");
            System.out.println(e.toString());
            System.exit(-1);
        }
        catch(Exception e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    public CSVFileReader(String filePath, String[] attributes) throws Exception {
        this.filePath = filePath;

        if (attributes == null) {
            throw new Exception("The Attributes Declaration line should not be blank");
        }

        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i] == null)
                throw new Exception("The Attribute No." + i + " must not be blank");
            else
                attributes[i] = attributes[i].trim();
        }
        this.attributes = attributes;
        haveAttrLine = false;

        File csvFile = new File(filePath);
        try {
            reader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "utf-8")));
        }
        catch(FileNotFoundException e) {
            System.out.println(e.toString() + ":\nThe File Path " + filePath + " is invalid");
            System.exit(-1);
        }
    }
}