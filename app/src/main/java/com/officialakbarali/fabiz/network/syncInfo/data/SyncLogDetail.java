package com.officialakbarali.fabiz.network.syncInfo.data;

public class SyncLogDetail {
    private long rawId;
    private String tableName;
    private String operation;

    public SyncLogDetail(long rawId, String tableName, String operation) {
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
