import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CSVFileDatabase {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private String[] attributes;
    private Map<String, Integer> attrMap;
    private List<String[]> csvLines;
    private long setBufLen;
    private final static int defaultBufLen = 1024;
    private long bufFirstLineNum = 0;
    private boolean complete = false;
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

    public String[] getHeader() {
        return attributes;
    }

    private ArrayList<Map<String, String>> searchInCurrentBuf(CSVDbMapCondition cond, long lineNum) {
        lock.readLock().lock();
        ArrayList<Map<String, String>> searchResults = new ArrayList<Map<String, String>>();

        long bufSize = csvLines.size();
        long range = (lineNum < bufSize) ? lineNum : bufSize;
        long lineCnt = 0;
        for (String[] line : csvLines) {
            HashMap<String, String> record = new HashMap<String, String>();
            for (int i = 0; i < attributes.length; i++) {
                String v = "";
                if (i < line.length)
                    v = line[i];

                record.put(attributes[i], v);
            }

            if (cond.meetCondition(record))
                searchResults.add(record);
            if(lineCnt++ == range)
                break;
        }
        lock.readLock().unlock();
        if (searchResults.size() != 0)
            return searchResults;
        else
            return null;
    }

    private ArrayList<Map<String, String>> searchInCurrentBuf(CSVDbMapCondition cond) {
        return searchInCurrentBuf(cond, csvLines.size());
    }

    private ArrayList<Map<String, String>> searchInCurrentBuf(CSVDbArrayCondition cond, long lineNum) {
        lock.readLock().lock();
        ArrayList<Map<String, String>> searchResults = new ArrayList<Map<String, String>>();

        long bufSize = csvLines.size();
        long range = (lineNum < bufSize) ? lineNum : bufSize;
        long lineCnt = 0;
        for (String[] line : csvLines) {
            if (cond.meetCondition(line)) {
                Map<String, String> record = new HashMap<String, String>();
                for (int i = 0; i < attributes.length; i++) {
                    String v = "";
                    if (i < line.length) {
                        v = line[i];
                    }
                    record.put(attributes[i], v);
                }
                searchResults.add(record);
            }
        }
        lock.readLock().unlock();
        if (searchResults.size() != 0)
            return searchResults;
        else
            return null;
    }

    private ArrayList<Map<String, String>> searchInCurrentBuf(CSVDbArrayCondition cond) {
        return searchInCurrentBuf(cond, csvLines.size());
    }

    private ArrayList<Map<String, String>> searchInCurrentBuf(int attrIndex, String value, long lineNum) {
        lock.readLock().lock();
        ArrayList<Map<String, String>> searchResults = new ArrayList<Map<String, String>>();

        long bufSize = csvLines.size();
        long range = (lineNum < bufSize) ? lineNum : bufSize;
        long lineCnt = 0;
        for (String[] line : csvLines) {
            if (line[attrIndex].equals(value)) {
                Map<String, String> curRecord = new HashMap<String, String>();
                for (int i = 0; i < attributes.length; i++) {
                    String v = "";
                    if (i < line.length) {
                        v = line[i];
                    }
                    curRecord.put(attributes[i], v);
                }
                searchResults.add(curRecord);
            }
            if (lineCnt++ == range)
                break;
        }

        lock.readLock().unlock();
        if (searchResults.size() != 0)
            return searchResults;
        else
            return null;
    }

    private ArrayList<Map<String, String>> searchInCurrentBuf(int attrIndex, String value) {
        return searchInCurrentBuf(attrIndex, value, csvLines.size());
    }

    private long bufferNextRecords (long bufSize) {
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
            else {
                return i;
            }
        }
        return bufSize;
    }

    private long bufferRefresh () {
        reader.reset();
        csvLines.clear();
        return bufferNextRecords(csvLines.size());
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

        CSVDbArrayCondition cond = new CSVDbArrayCondition() {
            public boolean meetCondition(String[] record) {
                for (int i = 0; i < attrs.length; i++) {
                    if (!record[attrs[i]].equals(values[i]))
                        return false;
                }
                return true;
            }
        };

        lock.readLock().lock();
        ArrayList<Map<String, String>> searchResults = null;

        if (complete) {
            searchResults = searchInCurrentBuf(cond);
        }
        else {
            long initialBufFirstLineNum = bufFirstLineNum;
            long readRecordsCnt = csvLines.size();
            do {
                ArrayList<Map<String, String>> bufResults = searchInCurrentBuf(cond);
                if (bufResults != null) {
                    for (Map m : bufResults) {
                        searchResults.add(m);
                    }
                }
            } while (readRecordsCnt == setBufLen && bufferNextRecords(setBufLen) != 0);

            if (initialBufFirstLineNum != 0) {
                bufferRefresh();
                long searchedLines = 0;
                do {
                    long bufLines = csvLines.size();
                    long searchingLines = (searchedLines + bufLines > initialBufFirstLineNum) ? initialBufFirstLineNum - searchedLines : bufLines;
                    ArrayList<Map<String, String>> bufResults = searchInCurrentBuf(cond, searchingLines);
                    if (bufResults != null) {
                        for (Map m : bufResults) {
                            searchResults.add(m);
                        }
                    }

                    searchedLines += searchingLines;
                    bufferNextRecords(bufLines);
                } while(searchedLines < initialBufFirstLineNum);
            }
        }
        lock.readLock().unlock();
        return searchResults;
    }

    public List<Map<String, String>> searchRecord (String attr, String value) throws IllegalArgumentException {

        if (attr == null || value == null) {
            throw new IllegalArgumentException("The Parameter must not be NULL");
        }

        int attrIndex = attributeIndex(attr);
        if (attrIndex < 0)
            return null;

        lock.readLock().lock();
        ArrayList<Map<String, String>> searchResults = null;

        if (complete) {
            searchResults = searchInCurrentBuf(attrIndex, value);
        }
        else {
            long initialBufFirstLineNum = bufFirstLineNum;
            searchResults = searchInCurrentBuf(attrIndex, value);

            final long bufSize = csvLines.size();
            long bufRecordsNum = 0;
            while((bufRecordsNum = bufferNextRecords(bufSize)) != 0) {
                ArrayList<Map<String, String>> bufResults = searchInCurrentBuf(attrIndex, value);
                if (bufResults != null) {
                    for (Map m : bufResults) {
                        searchResults.add(m);
                    }
                }

                if(bufRecordsNum != bufSize)
                    break;
            }

            if (initialBufFirstLineNum != 0) {
                bufferRefresh();
                long searchedLines = 0;
                do {
                    long bufLines = csvLines.size();
                    long searchingLines = (searchedLines + bufLines > initialBufFirstLineNum) ? initialBufFirstLineNum - searchedLines : bufLines;
                    ArrayList<Map<String, String>> bufResults = searchInCurrentBuf(attrIndex, value, searchingLines);
                    if (bufResults != null) {
                        for (Map m : bufResults) {
                            searchResults.add(m);
                        }
                    }

                    searchedLines += searchingLines;
                    bufferNextRecords(bufLines);
                } while(searchedLines < initialBufFirstLineNum);
            }
        }
        lock.readLock().unlock();
        return searchResults;
    }

    public int addRecord(Map<String, String> newRecord) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            long eofPos = raf.length();

            StringBuffer newlineSb = new StringBuffer();
            raf.seek(eofPos - 2);
            if (raf.readChar() != '\n')
                newlineSb.append('\n');
            raf.seek(eofPos);

            String[] newValues = new String[attributes.length];
            for(int i = 0; i < attributes.length; i++) {
                String s = newRecord.get(attributes[i]);
                if (s == null)
                    throw new RuntimeException("The Record Map Parameter is INVALID. There is no key: " + attributes[i]);
                else
                    newValues[i] = s;
            }
            String newRecordLine = CSVFileWriter.generateRecord(newValues);
            newlineSb.append(newRecordLine);

            lock.writeLock().lock();
            raf.write(newlineSb.toString().getBytes("utf8"));
            lock.writeLock().unlock();
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.exit(-1);
        }
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

            setBufLen = recordsBufSize > defaultBufLen ? recordsBufSize : defaultBufLen;
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
