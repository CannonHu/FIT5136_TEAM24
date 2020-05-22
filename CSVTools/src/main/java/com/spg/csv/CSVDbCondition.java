package com.spg.csv;

import java.util.Map;

abstract class CSVDbSearchCondition {
    public static final int MAP_RECORD_COND = 2;
    public static final int ARRAY_RECORD_COND = 1;
    private int condType = 0;

    public boolean mapCondImplemented() { return (condType & MAP_RECORD_COND) != 0; }
    public boolean arrayCondImplemented() { return (condType & ARRAY_RECORD_COND) != 0; }

    abstract boolean meetCondition(Map<String, String> record);

    abstract boolean meetCondition(String[] record);

    public CSVDbSearchCondition(int condType) { this.condType = condType; }
}
/*
interface CSVDbMapCondition {
    boolean meetCondition(Map<String, String> record);
}

interface CSVDbArrayCondition {
    boolean meetCondition(String[] record);
}*/
