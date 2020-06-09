package com.spg.csv;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CSVFileDatabase {
    private final static int defaultBufLen = 1024;
    private final static int defaultAppendBufLen = 256;

   // private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private String[] attributes;
    private Map<String, Integer> attrMap;

    private static final int bufBlockSize = 128;
    private class CSVBuffer {
        String[][] bufBlock = null;
        long bufBeginCnt = 0;
        int blockLen = 0;
        boolean bufChanged = false;
        long shotTimes = 0;

        public void shot(long times) {
            if (this.shotTimes + times > 0)
                this.shotTimes += times;
        }

        public void decrease() {
            this.shotTimes--;
        }
    }

    private CSVBuffer[] csvBufs = null;
    private int csvBufBlockNum = 0;
    private int lruBlockIndex = 0;

    private boolean complete = false;

    private static final int setAppendBufLen = 256;
    private int appendingLineNum = 0;
    private String[][] appendLines = new String[setAppendBufLen][];

    private String filePath;

    //private int searchCnt = 0;

    public abstract static class CSVDbSearchCondition {
        public static final int MAP_RECORD_COND = 2;
        public static final int ARRAY_RECORD_COND = 1;
        protected int condType = 0;

        public boolean mapCondImplemented() { return (condType & MAP_RECORD_COND) != 0; }
        public boolean arrayCondImplemented() { return (condType & ARRAY_RECORD_COND) != 0; }

        public abstract boolean meetCondition(Map<String, String> record);

        public abstract boolean meetCondition(String[] record);

        public CSVDbSearchCondition(int condType) { this.condType = condType; }
    }

    public abstract static class CSVDbModify {
        public static final int MAP_RECORD_MOD= 2;
        public static final int ARRAY_RECORD_MOD = 1;
        protected int modType = 0;

        public boolean mapModImplemented() { return (modType & MAP_RECORD_MOD) != 0; }
        public boolean arrayModImplemented() { return (modType & ARRAY_RECORD_MOD) != 0; }

        public abstract boolean modifyRecord(Map<String, String> record);

        public abstract boolean modifyRecord(String[] record);

        public CSVDbModify(int modType) {
            this.modType = modType;
        }
    }

    private static int findLRUBlock(CSVBuffer[] bufArr, int arrLen) {
        int lruIndex = 0;
        int len = arrLen > bufArr.length ? bufArr.length : arrLen;
        for (int i = 1; i < len; i++)
            if (bufArr[i].shotTimes < bufArr[lruIndex].shotTimes)
                lruIndex = i;

        return lruIndex;
    }

    private void bufBlockQSort(CSVBuffer[] cb, int low, int high) {
        if(low>high){
            return;
        }
        int i = low, j = high;
        CSVBuffer base = cb[low];
        while (i<j) {
            while (base.bufBeginCnt <= cb[j].bufBeginCnt && i<j)
                j--;

            while (base.bufBeginCnt >= cb[i].bufBeginCnt && i<j)
                i++;

            if (i<j) {
                CSVBuffer t = cb[j];
                cb[j] = cb[i];
                cb[i] = t;
            }
        }

        cb[low] = cb[i];
        cb[i] = base;

        bufBlockQSort(cb, low, j - 1);
        bufBlockQSort(cb, j + 1, high);
    }

    private int bufferWriteBack() {
        bufBlockQSort(csvBufs, 0, csvBufBlockNum - 1);
        boolean need = false;
        for (int i = 0; i < csvBufBlockNum; i++) {
            if (csvBufs[i].bufChanged) {
                need = true;
                break;
            }
        }

        if (need) {
            String tmpFilePath = filePath + (int) (Math.random() * 1000) + "~.tmp";
            CSVFileReader reader = null;
            try {
                reader = new CSVFileReader(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CSVFileWriter writer = new CSVFileWriter(tmpFilePath, attributes);
            long index = 0;
            for (int i = 0; i < csvBufBlockNum; i++) {
                long endRange = csvBufs[i].bufBeginCnt * bufBlockSize;
                for (; index < endRange; index++) {
                    String[] l = reader.readNextLine();
                    if (l != null)
                        writer.writeNextRecord(l);
                }

                if (csvBufs[i].bufChanged) {
                    for (int j = 0; j < csvBufs[i].blockLen; j++) {
                        if (csvBufs[i].bufBlock[j] != null)
                            writer.writeNextRecord(csvBufs[i].bufBlock[j]);
                        index++;
                    }
                    csvBufs[i].bufChanged = false;
                    try {
                        reader.skip(csvBufs[i].blockLen);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (int j = 0; j < csvBufs[i].blockLen; j++) {
                        String[] l = reader.readNextLine();
                        if (l != null)
                            writer.writeNextRecord(l);
                    }
                }
            }

            String[] nextLine = null;
            while ((nextLine = reader.readNextLine()) != null)
                writer.writeNextRecord(nextLine);
            reader.close();
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File originFile = new File(filePath);
            if (originFile.exists())
                originFile.delete();

            File newFileTemp = new File(tmpFilePath);
            File newFile = new File(filePath);
            newFileTemp.renameTo(newFile);
        }
        return 0;
    }

    public void flush() {
        bufferWriteBack();

        if (appendingLineNum != 0)
            writeAppendRecords();
    }

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
            throw new Exception("The Record Value String Array is INVALID. Provided Value Numbers is " + valArray.length + " Required: " + attributes.length);
        }

        for (int i = 0; i < attributes.length; i++) {
            if (valArray[i] == null)
                throw new Exception("Values in Record Value String Array must not be NULL");

            record.put(attributes[i], valArray[i]);
        }
        return record;
    }

    private String[] recordMap2StrArray(Map<String, String> wholeRecord) throws Exception {
        String[] values = new String[attributes.length];
        int index = 0;
        for(String attr : attributes) {
            if (wholeRecord.containsKey(attr)) {
                String tmpVal = null;
                if ((tmpVal = wholeRecord.get(attr)) != null)
                    values[index++] = tmpVal;
                else
                    throw new Exception("The Record Map Value of Key: " + attr + " must not be NULL");
            } else {
                throw new Exception("The Record Map miss Key: " + attr);
            }
        }
        return values;
    }

    public String[] getHeader() {
        return attributes;
    }

    private long searchInGivenBuf(String[][] givenLinesBuf, long lineNum, CSVDbSearchCondition cond, List<Map<String, String>> searchResults) throws Exception {
        long lineCnt = 0;
        long foundLines = 0;

        long bufSize = givenLinesBuf.length;
        long range = (lineNum < bufSize) ? lineNum : bufSize;

        if (cond.arrayCondImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                if (cond.meetCondition(line)) {
                    Map<String, String> record = strArray2RecordMap(line);
                    searchResults.add(record);
                    foundLines++;
                }
            }
        } else if (cond.mapCondImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                Map<String, String> record = strArray2RecordMap(line);

                if (cond.meetCondition(record)) {
                    searchResults.add(record);
                    foundLines++;
                }
            }
        } else {
            throw new IllegalArgumentException("At least one meetCondition method in CSVDbSearchCondition must be implemented");
        }

        return foundLines;
    }

    private Map<String, String> searchUniqueInGivenBuf(String[][] givenLinesBuf, long lineNum, CSVDbSearchCondition cond) throws Exception {
        Map<String, String> res = null;

        long bufSize = givenLinesBuf.length;
        long range = (lineNum < bufSize) ? lineNum : bufSize;

        if (cond.arrayCondImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                if (cond.meetCondition(line)) {
                    res = strArray2RecordMap(line);
                    break;
                }
            }
        } else if (cond.mapCondImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                Map<String, String> record = strArray2RecordMap(line);

                if (cond.meetCondition(record)) {
                    res = record;
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("At least one meetCondition method in CSVDbSearchCondition must be implemented");
        }
        return res;
    }

    private long searchInBufBlock(CSVBuffer cb, CSVDbSearchCondition cond, List<Map<String, String>> searchResults) throws Exception {
        long cnt = searchInGivenBuf(cb.bufBlock, cb.blockLen, cond, searchResults);
        if (cnt != 0)
            cb.shot(cnt);
        else
            cb.decrease();

        return cnt;
    }

    private long searchInBuf(CSVDbSearchCondition cond, List<Map<String, String>> searchResults) throws Exception {
        long cnt = 0;
        for (int i = 0; i < csvBufBlockNum; i++)
            cnt += searchInBufBlock(csvBufs[i], cond, searchResults);

        return cnt;
    }

    private long searchInAppendBuf(CSVDbSearchCondition cond, List<Map<String, String>> searchResults) throws Exception {
        if (appendLines == null || appendLines.length == 0 || appendingLineNum == 0)
            return 0l;
        else
            return searchInGivenBuf(appendLines, appendingLineNum, cond, searchResults);
    }

    private Map<String, String> searchUniqueInBuf(CSVDbSearchCondition cond) throws Exception {
        Map<String, String> res = null;
        for (int i = 0; i < csvBufBlockNum; i++) {
            res = searchUniqueInGivenBuf(csvBufs[i].bufBlock, csvBufs[i].blockLen, cond);
            if (res != null) {
                csvBufs[i].shot(1);
                break;
            } else
                csvBufs[i].decrease();
        }
        return res;
    }

    private Map<String, String> searchUniqueInAppendBuf(CSVDbSearchCondition cond) throws Exception {
        if (appendLines == null || appendLines.length == 0 || appendingLineNum == 0)
            return null;
        else
            return searchUniqueInGivenBuf(appendLines, appendingLineNum, cond);
    }

    private long modifyInGivenBuf(String[][] givenLinesBuf, long lineNum, CSVDbModify dbModify, List<Map<String, String>> modifiedList) throws Exception {
        long lineCnt = 0;
        long foundLines = 0;

        long bufSize = givenLinesBuf.length;
        long range = (lineNum < bufSize) ? lineNum : bufSize;

        if (dbModify.arrayModImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                if (dbModify.modifyRecord(line)) {
                    modifiedList.add(strArray2RecordMap(line));
                    foundLines++;
                }

                if (++lineCnt == range)
                    break;
            }
        } else if (dbModify.mapModImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                Map<String, String> record = strArray2RecordMap(line);
                if (dbModify.modifyRecord(record)) {
                    modifiedList.add(record);
                    foundLines++;
                }

                if (++lineCnt == range)
                    break;
            }
        } else {
            throw new IllegalArgumentException("At least one modifyRecord method in CSVDbModify must be implemented");
        }

        return foundLines;
    }

    private Map<String, String> modifyUniqueInGivenBuf(String[][] givenLinesBuf, long lineNum, CSVDbModify dbModify) throws Exception {
        Map<String, String> modifiedRecord = null;
        long lineCnt = 0;

        long bufSize = givenLinesBuf.length;
        long range = (lineNum < bufSize) ? lineNum : bufSize;

        if (dbModify.arrayModImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                if (dbModify.modifyRecord(line)) {
                    modifiedRecord = strArray2RecordMap(line);
                    break;
                }

                if (++lineCnt == range)
                    break;
            }
        } else if (dbModify.mapModImplemented()) {
            for (int i = 0; i < range; i++) {
                String[] line = givenLinesBuf[i];
                Map<String, String> record = strArray2RecordMap(line);
                if (dbModify.modifyRecord(record)) {
                    modifiedRecord = record;
                    break;
                }

                if (++lineCnt == range)
                    break;
            }
        } else {
            throw new IllegalArgumentException("At least one modifyRecord method in CSVDbModify must be implemented");
        }

        return modifiedRecord;
    }

    private long modifyInBufBlock(CSVBuffer cb, CSVDbModify dbModify, List<Map<String, String>> modifiedList) throws Exception {

        long cnt = modifyInGivenBuf(cb.bufBlock, cb.blockLen, dbModify, modifiedList);
        if (cnt > 0) {
            cb.shot(cnt);
            cb.bufChanged = true;
        } else
            cb.decrease();
        return cnt;
    }

    private long modifyInBuf(CSVDbModify dbModify, List<Map<String, String>> modifiedList) throws Exception {
        long cnt = 0;
        for (int i = 0; i < csvBufBlockNum; i++) {
            cnt += modifyInBufBlock(csvBufs[i], dbModify, modifiedList);
        }

        return cnt;
    }

    private Map<String, String> modifyUniqueInBuf(CSVDbModify dbModify) throws Exception {
        Map<String, String> modifiedRecord = null;
        for (int i = 0; i < csvBufBlockNum; i++)
            if ((modifiedRecord = modifyUniqueInGivenBuf(csvBufs[i].bufBlock, csvBufs[i].blockLen, dbModify)) != null) {
                csvBufs[i].bufChanged = true;
                break;
            }

        return modifiedRecord;
    }

    private long modifyInAppendBuf(CSVDbModify dbModify, List<Map<String, String>> modifiedList) throws Exception {
        return modifyInGivenBuf(appendLines, appendingLineNum, dbModify, modifiedList);
    }

    private Map<String, String> modifyUniqueInAppendBuf(CSVDbModify dbModify) throws Exception {
        return modifyUniqueInGivenBuf(appendLines, appendingLineNum, dbModify);
    }

    private CSVDbSearchCondition createSearchCondFromMap(Map<String, String> attrValues) {
        final int restrictsNum = attrValues.size();
        final int[] attrs = new int[restrictsNum];
        final String[] vals = new String[restrictsNum];

        CSVDbSearchCondition cond = null;
        if (attrValues.size() == attrs.length && attrs.length == vals.length) {
            int cnt = 0;
            for(Map.Entry<String, String> entry : attrValues.entrySet()) {
                String reqAttr = entry.getKey();
                if (reqAttr == null) {
                    throw new IllegalArgumentException("The Provided Attribute must not be NULL");
                }
                else if (reqAttr.length() == 0) {
                    throw new IllegalArgumentException("The Provided Attribute must not be BLANK");
                }

                int reqAttrIndex = attributeIndex(reqAttr);
                if (reqAttrIndex < 0) {
                    throw new IllegalArgumentException("The Provided Attribute: " + reqAttr + " is INVALID");
                }
                else {
                    attrs[cnt] = reqAttrIndex;
                    String v = entry.getValue();
                    if (v == null)
                        throw new RuntimeException("The Provided Value must not be NULL");
                    else
                        vals[cnt++] = v;
                }
            }
            cond = new CSVDbSearchCondition(CSVDbSearchCondition.ARRAY_RECORD_COND) {
                @Override
                public boolean meetCondition(Map<String, String> record) {
                    return false;
                }

                @Override
                public boolean meetCondition(String[] record) {
                    for (int i = 0; i < attrs.length; i++) {
                        if (!record[attrs[i]].equals(vals[i]))
                            return false;
                    }
                    return true;
                }
            };
        }

        return cond;
    }

    private CSVDbSearchCondition createSearchCondFromValAttr(String attr, String value) throws IllegalArgumentException {
        int attrIndex = attributeIndex(attr);
        if (attrIndex < 0)
            throw new IllegalArgumentException("The Required Attribute + " + attr + " is INVALID");

        CSVDbSearchCondition cond = new CSVDbSearchCondition(CSVDbSearchCondition.ARRAY_RECORD_COND) {
            @Override
            public boolean meetCondition(Map<String, String> record) {
                return false;
            }

            @Override
            public boolean meetCondition(String[] record) {
                boolean meet = false;
                if (record.length == attributes.length) {
                    meet = record[attrIndex].equals(value);
                }
                return meet;
            }
        };
        return cond;
    }


    public synchronized List<Map<String, String>> searchRecord (CSVDbSearchCondition cond) throws IllegalArgumentException {
        if (cond == null) {
            throw new IllegalArgumentException("The Condition Parameter must not be NULL");
        } else if (!(cond.arrayCondImplemented() || cond.mapCondImplemented())) {
            throw new IllegalArgumentException("The CSVDbSearchCondition has not been Initialized, neither modifyRecord method has been implemented!");
        }

        ArrayList<Map<String, String>> resultList = new ArrayList<>();

        try {
            if (complete) {
                searchInBuf(cond, resultList);
            } else {
                bufferWriteBack();

                CSVFileReader reader = new CSVFileReader(filePath);
                String[][] tmpBuf = new String[bufBlockSize][];
                long[] beginCnts = new long[bufBlockSize];
                for (int i = 0; i < tmpBuf.length; i++)
                    beginCnts[i] = csvBufs[i].bufBeginCnt;

                long lineNum = 0;
                int blockNum = 0;
                int bufCnt = 0;
                boolean readFinished = false;
                while (!readFinished) {
                    String[] l = reader.readNextLine();
                    if (l != null) {
                        tmpBuf[bufCnt++] = l;
                        lineNum++;
                    }
                    else
                        readFinished = true;

                    if (bufCnt == tmpBuf.length || readFinished) {
                        long shotCnt = searchInGivenBuf(tmpBuf, (long) bufCnt, cond, resultList);

                        CSVBuffer lruBlock = csvBufs[lruBlockIndex];
                        if (shotCnt > lruBlock.shotTimes) {
                            lruBlock.bufBlock = tmpBuf;
                            lruBlock.bufBeginCnt = lineNum;
                            lruBlock.bufChanged = false;
                            lruBlock.blockLen = bufCnt;
                            lruBlock.shotTimes = shotCnt;

                            lruBlockIndex = findLRUBlock(csvBufs, csvBufBlockNum);
                        }

                        bufCnt = 0;
                    }
                }
                searchInAppendBuf(cond, resultList);

                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return resultList;
    }

    public List<Map<String, String>> searchRecord (Map<String, String> attrValues) throws IllegalArgumentException {
        if (attrValues == null || attrValues.size() == 0) {
            throw new IllegalArgumentException("The Attribute-Value Map Parameter must not be NULL or EMPTY");
        }

        return searchRecord(createSearchCondFromMap(attrValues));
    }

    public List<Map<String, String>> searchRecord (String attr, String value) throws IllegalArgumentException {

        if (attr == null || value == null) {
            throw new IllegalArgumentException("The Parameter must not be NULL");
        }

        return searchRecord(createSearchCondFromValAttr(attr, value));
    }

    public Map<String, String> searchUniqueRecord(CSVDbSearchCondition cond) {
        if (cond == null) {
            throw new IllegalArgumentException("The Condition Parameter must not be NULL");
        } else if (!(cond.arrayCondImplemented() || cond.mapCondImplemented())) {
            throw new IllegalArgumentException("The CSVDbSearchCondition has not been Initialized, neither modifyRecord method has been implemented!");
        }

        Map<String, String> record = new HashMap<>();

        try {
            record = searchUniqueInBuf(cond);
            if (record == null) {
                record = searchUniqueInAppendBuf(cond);
                String[][] tmpBuf = new String[bufBlockSize][];
                long[] beginCnts = new long[bufBlockSize];
                for (int i = 0; i < csvBufBlockNum; i++)
                    beginCnts[i] = csvBufs[i].bufBeginCnt;

                if (record == null) {
                    bufBlockQSort(csvBufs, 0, csvBufBlockNum - 1);
                    CSVFileReader reader = new CSVFileReader(filePath);

                    long lineNum = 0;
                    int blockNum = 0;
                    int bufCnt = 0;
                    boolean readFinished = false;
                    while (!readFinished && record == null) {
                        if (blockNum < csvBufBlockNum
                                && lineNum % bufBlockSize == 0
                                && lineNum / bufBlockSize == beginCnts[blockNum]) {
                            CSVBuffer cb = csvBufs[blockNum++];
                            lineNum += cb.blockLen;
                            reader.skip(cb.blockLen);
                        } else {
                            String[] l = reader.readNextLine();
                            if (l != null) {
                                tmpBuf[bufCnt++] = l;
                                lineNum++;
                            }
                            else
                                readFinished = true;

                            if (bufCnt == tmpBuf.length || readFinished) {
                                record = searchUniqueInGivenBuf(tmpBuf, (long) bufCnt, cond);
                                if (record != null) {
                                    CSVBuffer lruBlock = csvBufs[lruBlockIndex];
                                    if (1 > lruBlock.shotTimes) {
                                        lruBlock.bufBlock = tmpBuf;
                                        lruBlock.bufBeginCnt = lineNum;
                                        lruBlock.bufChanged = false;
                                        lruBlock.blockLen = bufCnt;
                                        lruBlock.shotTimes = 1;

                                        lruBlockIndex = findLRUBlock(csvBufs, csvBufBlockNum);
                                    }
                                }
                            }
                            bufCnt = 0;
                        }
                    }
                    reader.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public Map<String, String> searchUniqueRecord (Map<String, String> attrValues) throws IllegalArgumentException {
        if (attrValues == null || attrValues.size() == 0) {
            throw new IllegalArgumentException("The Attribute-Value Map Parameter must not be NULL or EMPTY");
        }

        return searchUniqueRecord(createSearchCondFromMap(attrValues));
    }

    public Map<String, String> searchUniqueRecord (String attr, String value) throws IllegalArgumentException {
        if (attr == null || value == null) {
            throw new IllegalArgumentException("The Parameter must not be NULL");
        }

        return searchUniqueRecord(createSearchCondFromValAttr(attr, value));
    }

    public List<Map<String, String>> modifyRecord(CSVDbModify dbModify) {
        if (dbModify == null) {
            throw new IllegalArgumentException("The Condition Parameter must not be NULL");
        } else if (!(dbModify.mapModImplemented() || dbModify.arrayModImplemented())) {
            throw new IllegalArgumentException("The CSVDbSearchCondition has not been Initialized, neither modifyRecord method has been implemented!");
        }

        List<Map<String, String>> modifiedList = new ArrayList<>();

        try {
            if (complete) {
                modifyInBuf(dbModify, modifiedList);
            } else {
                String tmpFilePath = filePath + (int)(Math.random() * 1000) + "~.tmp";
                CSVFileWriter writer = new CSVFileWriter(tmpFilePath, attributes);
                CSVFileReader reader = new CSVFileReader(filePath);

                bufBlockQSort(csvBufs, 0, csvBufBlockNum - 1);

                CSVBuffer[] newBufBlocks = new CSVBuffer[csvBufBlockNum];
                for (int i = 0; i < csvBufBlockNum; i++)
                    newBufBlocks[i] = csvBufs[i];
                String[][] tmpBuf = new String[bufBlockSize][];

                long lineNum = 0;
                int blockNum = 0;
                int bufCnt = 0;
                boolean readFinished = false;
                while (!readFinished) {
                    if (blockNum < csvBufBlockNum
                            && lineNum % bufBlockSize == 0
                            && lineNum / bufBlockSize == csvBufs[blockNum].bufBeginCnt) {
                        CSVBuffer cb = csvBufs[blockNum++];
                        modifyInBufBlock(cb, dbModify, modifiedList);
                        for (int i = 0; i < bufCnt; i++)
                            writer.writeNextRecord(cb.bufBlock[i]);

                        cb.bufChanged = false;
                        reader.skip(cb.blockLen);
                        lineNum += cb.blockLen;
                    } else {
                        String[] l = reader.readNextLine();
                        if (l != null) {
                            tmpBuf[bufCnt++] = l;
                            lineNum++;
                        } else
                            readFinished = true;

                        if (bufCnt == tmpBuf.length || readFinished) {
                            long shotCnt = modifyInGivenBuf(tmpBuf, (long) bufCnt, dbModify, modifiedList);
                            for (int i = 0; i < bufCnt; i++)
                                writer.writeNextRecord(tmpBuf[i]);

                            CSVBuffer lruBlock = csvBufs[lruBlockIndex];
                            if (shotCnt > lruBlock.shotTimes) {
                                if (lruBlockIndex <= blockNum) {
                                    lruBlock.bufBlock = tmpBuf;
                                    lruBlock.bufBeginCnt = lineNum;
                                    lruBlock.bufChanged = false;
                                    lruBlock.blockLen = bufCnt;
                                    lruBlock.shotTimes = shotCnt;

                                    lruBlockIndex = findLRUBlock(newBufBlocks, csvBufBlockNum);
                                } else {
                                    CSVBuffer newBuf = new CSVBuffer();
                                    newBuf.bufBlock = new String[tmpBuf.length][];
                                    for (int m = 0; m < tmpBuf.length; m++)
                                        newBuf.bufBlock[m] = tmpBuf[m];
                                    newBuf.bufBeginCnt = lineNum;
                                    newBuf.bufChanged = false;
                                    newBuf.blockLen = bufCnt;
                                    newBuf.shotTimes = shotCnt;

                                    newBufBlocks[lruBlockIndex] = newBuf;
                                    lruBlockIndex = findLRUBlock(newBufBlocks, csvBufBlockNum);
                                }
                            }
                            bufCnt = 0;
                        }
                    }
                }
                csvBufs = newBufBlocks;
                reader.close();
                writer.close();

                modifyInAppendBuf(dbModify, modifiedList);
                writeAppendRecords();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return modifiedList;
    }

    public Map<String, String> modifyUniqueRecord(CSVDbModify dbModify) {
        if (dbModify == null) {
            throw new IllegalArgumentException("The Condition Parameter must not be NULL");
        } else if (!(dbModify.mapModImplemented() || dbModify.arrayModImplemented())) {
            throw new IllegalArgumentException("The CSVDbSearchCondition has not been Initialized, neither modifyRecord method has been implemented!");
        }

        Map<String, String> modifiedRecord = null;
        try {
            modifiedRecord = modifyUniqueInBuf(dbModify);
            if (modifiedRecord == null) {
                modifiedRecord = modifyUniqueInAppendBuf(dbModify);
                if (modifiedRecord == null) {
                    String tmpFilePath = filePath + (int)(Math.random() * 1000) + "~.tmp";
                    CSVFileWriter writer = new CSVFileWriter(tmpFilePath, attributes);
                    CSVFileReader reader = new CSVFileReader(filePath);

                    bufBlockQSort(csvBufs, 0, csvBufBlockNum - 1);

                    CSVBuffer[] newBufBlocks = new CSVBuffer[csvBufBlockNum];
                    for (int i = 0; i < csvBufBlockNum; i++)
                        newBufBlocks[i] = csvBufs[i];
                    String[][] tmpBuf = new String[bufBlockSize][];

                    long lineNum = 0;
                    int blockNum = 0;
                    int bufCnt = 0;
                    boolean readFinished = false;
                    while (!readFinished) {
                        if (blockNum < csvBufBlockNum
                                && lineNum % bufBlockSize == 0
                                && lineNum / bufBlockSize == csvBufs[blockNum].bufBeginCnt) {
                            CSVBuffer cb = csvBufs[blockNum++];
                            for (int i = 0; i < bufCnt; i++)
                                writer.writeNextRecord(cb.bufBlock[i]);

                            cb.bufChanged = false;
                            reader.skip(cb.blockLen);
                            lineNum += cb.blockLen;
                        } else {
                            String[] l = reader.readNextLine();
                            if (modifiedRecord == null) {
                                if (l != null) {
                                    tmpBuf[bufCnt++] = l;
                                    lineNum++;
                                } else
                                    readFinished = true;

                                if (bufCnt == tmpBuf.length || readFinished) {
                                    modifiedRecord = modifyUniqueInGivenBuf(tmpBuf, (long) bufCnt, dbModify);
                                    for (int i = 0; i < bufCnt; i++)
                                        writer.writeNextRecord(tmpBuf[i]);

                                    CSVBuffer lruBlock = csvBufs[lruBlockIndex];
                                    if (1 > lruBlock.shotTimes) {
                                        if (lruBlockIndex <= blockNum) {
                                            lruBlock.bufBlock = tmpBuf;
                                            lruBlock.bufBeginCnt = lineNum;
                                            lruBlock.bufChanged = false;
                                            lruBlock.blockLen = bufCnt;
                                            lruBlock.shotTimes = 1;

                                            lruBlockIndex = findLRUBlock(newBufBlocks, csvBufBlockNum);
                                        } else {
                                            CSVBuffer newBuf = new CSVBuffer();
                                            newBuf.bufBlock = new String[tmpBuf.length][];
                                            for (int m = 0; m < tmpBuf.length; m++)
                                                newBuf.bufBlock[m] = tmpBuf[m];
                                            newBuf.bufBeginCnt = lineNum;
                                            newBuf.bufChanged = false;
                                            newBuf.blockLen = bufCnt;
                                            newBuf.shotTimes = 1;

                                            newBufBlocks[lruBlockIndex] = newBuf;
                                            lruBlockIndex = findLRUBlock(newBufBlocks, csvBufBlockNum);
                                        }
                                    }
                                    bufCnt = 0;
                                }
                            } else if (l != null) {
                                writer.writeNextRecord(l);
                                readFinished = true;
                            }
                        }
                        reader.close();
                        writer.close();
                    }
                    csvBufs = newBufBlocks;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modifiedRecord;
    }

    private void writeAppendRecords() {
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            long eofPos = raf.length();
            StringBuffer sb = new StringBuffer();
            raf.seek(eofPos - 2);
            if ((byte)raf.readChar() != (byte)'\n' ) {
                sb.append('\n');
            }
            raf.seek(eofPos);

            for(int i = 0; i < appendingLineNum; i++) {
                sb.append(CSVFileWriter.generateRecord(appendLines[i]));
            }

            raf.write(sb.toString().getBytes("utf8"));

        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(-1);
        }

        for(int i = 0; i < appendLines.length; i++)
            appendLines[i] = null;
        appendingLineNum = 0;
    }

    public synchronized int addRecord(Map<String, String> newRecord) throws Exception   {
        if (appendingLineNum >= setAppendBufLen) {
            writeAppendRecords();
        }

        appendLines[appendingLineNum++] = recordMap2StrArray(newRecord);
        return 0;
    }


    public CSVFileDatabase(String filePath, int recordsBufSize) throws Exception {
        if (filePath == null) {
            throw new IllegalArgumentException("The File Path String Parameter must not be NULL");
        }
        if (recordsBufSize <= 0) {
            throw new IllegalArgumentException("The Records Buffer Size Parameter must be POSITIVE. Given " + recordsBufSize);
        } else if (recordsBufSize > 32768) {
            throw new IllegalArgumentException("The Records Buffer Size Parameter is too big. The MAX supported is 32768");
        }

        File f = new File(filePath);
        if (f.exists() && f.isFile()) {
            this.filePath = filePath;
            CSVFileReader reader = new CSVFileReader(filePath);

            attributes = reader.getHeader();
            attrMap = new HashMap<String, Integer>();
            for(int i = 0; i < attributes.length; i++) {
                attrMap.put(attributes[i], new Integer(i));
            }

            int setBufLen = recordsBufSize > defaultBufLen ? recordsBufSize : defaultBufLen;
            csvBufs = new CSVBuffer[(setBufLen / bufBlockSize) + (setBufLen % bufBlockSize != 0 ? 1 : 0)];
            for (int i = 0; i < csvBufs.length; i++) {
                csvBufs[i] = new CSVBuffer();
                csvBufs[i].bufBlock = new String[bufBlockSize][];
            }

            for (int i = 0; i < csvBufs.length && !complete; i++) {
                for (int j = 0; j < bufBlockSize; j++) {
                    String[] l = reader.readNextLine();
                    if (l != null && l.length != 0) {
                        csvBufs[i].bufBlock[j] = l;
                        csvBufs[i].blockLen++;

                        if (j == 0) {
                            csvBufBlockNum++;
                            csvBufs[i].bufBeginCnt = i;
                        }
                    } else {
                        complete = true;
                        break;
                    }
                }
            }
            reader.close();
        }
        else {
            throw new FileNotFoundException("The File Path " + filePath + " is invalid");
        }
    }

    public CSVFileDatabase(String filePath) throws Exception {
        this(filePath, defaultBufLen);
    }
}
