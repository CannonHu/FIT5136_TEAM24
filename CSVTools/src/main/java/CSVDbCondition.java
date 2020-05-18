import java.util.Map;

interface CSVDbMapCondition {
    boolean meetCondition(Map<String, String> record);
}

interface CSVDbArrayCondition {
    boolean meetCondition(String[] record);
}
