package com.spg.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CSVFileDatabase {
    private final static int defaultBufLen = 1024;
    private final static int defaultAppendBufLen = 384;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private String[] attributes;
    private Map<String, Integer> attrMap;

    private List<String[]> csvLines;
    private long setBufLen;
    private long bufFirstLineNum = 0;
    private boolean complete = false;

    private List<String[]> appendLines;
    private long setAppendBufLen;

    private String filePath;
    private CSVFileReader reader;
    private CSVFileWriter writer;

    private int attributeIndex(String attr) throws IllegalArgumentException {
        if (attr == null || attr.length() == 0) {
            throw new IllegalArgumentException("The Attribute String Parameter must not be NULL or BLANK");
        }
        int index = -1;
        Integer i = attrMap.get(attr);
        if (i != null)
            index = i.intValue();
        return index;
    }

    private Map<String, String> strArray2RecordMap(String[] valArray) throws Exception {
        HashMap<String, String> record = new HashMap<String, String>();
        if (valArray.length != attributes.length) {
            throw new IllegalArgumentException("The Record Value String Array is INVALID. Provided Value Numbers is " + valArray.length + " Required: " + attributes.length);
        }

        for (int i = 0; i < attributes.length; i++) {
            if (valArray[i] == null)
                throw new Exception("Values in Record Value String Array must not be NULL");

            record.put(attributes[i], valArray[i]);
        }
        return record;
    }

    private String[] recordMap2StrArray(Map<String, String> wholeRecord) {
        String[] values = new String[attributes.length];
        int index = 0;
        for(String attr : attributes) {
            if (wholeRecord.containsKey(attr)) {
                String tmpVal = null;
                if ((tmpVal = wholeRecord.get(attr)) != null)
                    values[index++] = tmpVal;
                else
                    throw new RuntimeException("The Record Map Value of Key: " + attr + " must not be NULL");
            } else {
                throw new RuntimeException("The Record Map miss Key: " + attr);
            }
        }
        return values;
    }

    public String[] getHeader() {
        return attributes;
    }

    private long searchInAppendBuf(CSVDbSearchCondition cond, List<Map<String, String>> searchResults) throws Exception {
        long foundLines = 0;
        if (appendLines.size() != 0) {
            lock.readLock().lock();
            if (cond.arrayCondImplemented()) {
                for (String[] line : csvLines) {
                    if (cond.meetCondition(line)) {
                        searchResults.add(strArray2RecordMap(line));
                        foundLines++;
                    }
                }
            } else if (cond.mapCondImplemented()) {
                for (String[] line : csvLines) {
                    Map<String, String> record = strArray2RecordMap(line);

                    if (cond.meetCondition(record)) {
                        searchResults.add(record);
                        foundLines++;
                    }
                }
            }
            lock.readLock().unlock();
        } else {
            throw new RuntimeException("At least one meetCondition method in CSVDbCondition must be implemented");
        }
        return foundLines;
    }




    private long searchInCurrentBuf(CSVDbSearchCondition cond, long lineNum, List<Map<String, String>> searchResults) throws Exception {
        long lineCnt = 0;
        long foundLines = 0;

        lock.readLock().lock();

        long bufSize = csvLines.size();
        long range = (lineNum < bufSize) ? lineNum : bufSize;

        if (cond.arrayCondImplemented()) {
            for (String[] line : csvLines) {
                if (cond.meetCondition(line)) {
                    Map<String, String> record = strArray2RecordMap(line);
                    searchResults.add(record);
                    foundLines++;
                    if (++lineCnt == range)
                        break;
                }
            }
        } else if (cond.mapCondImplemented()) {
            for (String[] line : csvLines) {
                Map<String, String> record = strArray2RecordMap(line);

                if (cond.meetCondition(record)) {
                    searchResults.add(record);
                    foundLines++;
                }
                if (++lineCnt == range)
                    break;
            }
        } else {
            throw new RuntimeException("At least one meetCondition method in CSVDbCondition must be implemented");
        }

        return foundLines;
    }

    private long searchInCurrentBuf(CSVDbSearchCondition cond, List<Map<String, String>> searchResults) throws Exception {
        return searchInCurrentBuf(cond, csvLines.size() ,searchResults);
    }

    private long bufferNextRecords (long bufSize) {
        lock.writeLock().lock();
        bufFirstLineNum += csvLines.size();
        csvLines.clear();

        for (long i = 0; i < bufSize; i++) {
            String[] line = reader.readNextLine();
            if(line != null) {
                boolean blankLine = true;
                for (String s : line) {
                    if (s.length() != 0)
                        blankLine = false;
                }

                if (!blankLine)
                    csvLines.add(line);
            }

        }
        lock.writeLock().unlock();
        return bufSize;
    }

    private long bufferRefresh () {
        reader.reset();
        csvLines.clear();
        return bufferNextRecords(csvLines.size());
    }

    public List<Map<String, String>> searchRecord (CSVDbSearchCondition cond) throws IllegalArgumentException {
        if (cond == null) {
            throw new IllegalArgumentException("The Condition Parameter must not be NULL");
        }

        ArrayList<Map<String, String>> resultList = new ArrayList<>();
        lock.readLock().lock();

        try {
            if (complete) {
                searchInCurrentBuf(cond, resultList);
            } else {
                long initialBufFirstLineNum = bufFirstLineNum;
                long readRecordsLines = csvLines.size();
                do {
                    searchInCurrentBuf(cond, resultList);
                } while (readRecordsLines == setBufLen && bufferNextRecords(setBufLen) != 0);

                if (initialBufFirstLineNum != 0) {
                    bufferRefresh();
                    long searchedLines = 0;
                    do {
                        long bufLines = csvLines.size();
                        long searchingLines = (searchedLines + bufLines > initialBufFirstLineNum) ? initialBufFirstLineNum - searchedLines : bufLines;
                        searchInCurrentBuf(cond, searchingLines, resultList);

                        searchedLines += searchingLines;
                        bufferNextRecords(bufLines);
                    } while (searchedLines < initialBufFirstLineNum);
                }

                searchInAppendBuf(cond, resultList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        lock.readLock().unlock();
        return resultList;
    }

    public List<Map<String, String>> searchRecord (Map<String, String> attrValues) throws IllegalArgumentException {
        if (attrValues == null || attrValues.size() == 0) {
            throw new IllegalArgumentException("The Attribute-Value Map Parameter must not be NULL or EMPTY");
        }

        final int restrictsNum = attrValues.size();
        final int[] attrs = new int[restrictsNum];
        final String[] values = new String[restrictsNum];
        int cnt = 0;

        for(Map.Entry<String, String> entry : attrValues.entrySet()) {
            String reqAttr = entry.getKey();
            if (reqAttr == null) {
                throw new RuntimeException("The Provided Attribute must not be NULL");
            }
            else if (reqAttr.length() == 0) {
                throw new RuntimeException("The Provided Attribute must not be BLANK");
            }

            int reqAttrIndex = attributeIndex(reqAttr);
            if (reqAttrIndex < 0) {
                throw new RuntimeException("The Provided Attribute: " + reqAttr + " is INVALID");
            }
            else {
                attrs[cnt] = reqAttrIndex;
                String v = entry.getValue();
                if (v == null)
                    throw new RuntimeException("The Provided Value must not be NULL");
                else
                    values[cnt++] = v;
            }
        }

        CSVDbSearchCondition cond = new CSVDbSearchCondition(CSVDbSearchCondition.ARRAY_RECORD_COND) {
            @Override
            boolean meetCondition(Map<String, String> record) {
                return false;
            }

            @Override
            boolean meetCondition(String[] record) {
                for (int i = 0; i < attrs.length; i++) {
                    if (!record[attrs[i]].equals(values[i]))
                        return false;
                }
                return true;
            }
        };

        return searchRecord(cond);
    }

    public List<Map<String, String>> searchRecord (String attr, String value) throws IllegalArgumentException {

        if (attr == null || value == null) {
            throw new IllegalArgumentException("The Parameter must not be NULL");
        }

        int attrIndex = attributeIndex(attr);
        if (attrIndex < 0)
            return null;

        CSVDbSearchCondition cond = new CSVDbSearchCondition(CSVDbSearchCondition.ARRAY_RECORD_COND) {
            @Override
            boolean meetCondition(Map<String, String> record) {
                return false;
            }

            @Override
            boolean meetCondition(String[] record) {
                boolean meet = false;
                if (record.length == attributes.length) {
                    meet = record[attrIndex].equals(value);
                }
                return meet;
            }
        };

        return searchRecord(cond);
    }

    public int addRecord(Map<String, String> newRecord) {
        if (appendLines.size() >= setAppendBufLen) {
            try {
                RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
                long eofPos = raf.length();
                StringBuffer sb = new StringBuffer();
                raf.seek(eofPos - 2);
                if ((byte)raf.readChar() != (byte)'\n' ) {
                    sb.append('\n');
                }
                raf.seek(eofPos);

                for(String[] lineValues : appendLines) {
                    sb.append(CSVFileWriter.generateRecord(lineValues));
                }

                lock.writeLock().lock();
                raf.write(sb.toString().getBytes("utf8"));
                lock.writeLock().unlock();

            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(-1);
            }
            appendLines.clear();
        }

        appendLines.add(recordMap2StrArray(newRecord));
        return 0;
    }



    public CSVFileDatabase(String filePath, long recordsBufSize) throws Exception {
        if (filePath == null) {
            throw new IllegalArgumentException("The File Path String Parameter must not be NULL");
        }
        if (recordsBufSize <= 0) {
            throw new IllegalArgumentException("The Records Buffer Size Parameter must be POSITIVE. Given " + recordsBufSize);
        }

        File f = new File(filePath);
        if (f.exists() && f.isFile()) {
            this.filePath = filePath;
            reader = new CSVFileReader(filePath);

            attributes = reader.getHeader();
            attrMap = new HashMap<String, Integer>();
            for(int i = 0; i < attributes.length; i++) {
                attrMap.put(attributes[i], new Integer(i));
            }

            csvLines = new ArrayList<String[]>();
            appendLines = new ArrayList<String[]>();

            setBufLen = recordsBufSize > defaultBufLen ? recordsBufSize : defaultBufLen;
            setAppendBufLen = defaultAppendBufLen;
            for(long i = 0; i < setBufLen;  i++) {
                String[] l = reader.readNextLine();
                if(l != null) {
                    boolean blankLine = true;
                    for (String s : l) {
                        if (s.length() != 0)
                            blankLine = false;
                    }

                    if (!blankLine)
                        csvLines.add(l);
                }
                else {
                    complete = true;
                    break;
                }
            }
        }
        else {
            throw new FileNotFoundException("The File Path " + filePath + " is invalid");
        }
    }

    public CSVFileDatabase(String filePath) throws Exception {
        this(filePath, defaultBufLen);
    }
}
