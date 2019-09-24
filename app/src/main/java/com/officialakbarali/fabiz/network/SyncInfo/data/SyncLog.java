package com.officialakbarali.fabiz.network.SyncInfo.data;

public class SyncLog {

    private long rawId;
    private String tableName;
    private String operation;

    public SyncLog(long rawId, String tableName, String operation) {
        this.rawId = rawId;
        this.tableName = tableName;
        this.operation = operation;
    }


    public long getRawId() {
        return rawId;
    }

    public String getOperation() {
        return operation;
    }

    public String getTableName() {
        return tableName;
    }
}
