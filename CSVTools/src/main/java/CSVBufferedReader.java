import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CSVBufferedReader extends CSVFileReader {
    private Map<String, String>[] recordBuf;
    private int recordOffsetinBuf = 0;
    private boolean hasReadRecord = false;

    private void bufferRefresh() {
        boolean eof = false;
        for(int i = 0; i < recordBuf.length; i++) {
            if (eof)
                recordBuf[i] = null;
            else if ((recordBuf[i] = super.readNextRecord()) == null)
                eof = true;
        }
        recordOffsetinBuf = 0;
    }

    @Override
    public Map<String, String> readNextRecord() throws RuntimeException {
        if (!hasReadRecord || recordOffsetinBuf >= recordBuf.length) {
            bufferRefresh();
            hasReadRecord = true;
        }
        return recordBuf[recordOffsetinBuf++];
    }

    @Override
    public int skip(int numberOfLinesToSkip) throws IOException {
        int skippedLines = super.skip(numberOfLinesToSkip);
        if (skippedLines != numberOfLinesToSkip)
            return skippedLines;
        else {
            if (recordOffsetinBuf + numberOfLinesToSkip >= recordBuf.length) {
                bufferRefresh();
            }
            return numberOfLinesToSkip;
        }
    }

    @Override
    public void reset() {
        bufferRefresh();
        super.reset();
    }

    public CSVBufferedReader(String filePath) throws Exception {
        super(filePath);
        recordBuf = new Map[128];
    }

    public CSVBufferedReader(String filePath, String[] attributes) throws Exception {
        super(filePath, attributes);
        recordBuf = new Map[128];
    }

    public CSVBufferedReader(String filePath, int bufSize) throws Exception {
        super(filePath);
        recordBuf = new Map[bufSize];
    }

    public CSVBufferedReader(String filePath, String[] attributes, int bufSize) throws Exception {
        super(filePath, attributes);
        recordBuf = new Map[bufSize];
    }
}
