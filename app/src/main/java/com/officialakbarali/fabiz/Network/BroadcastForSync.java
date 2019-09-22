package com.officialakbarali.fabiz.Network;

public class BroadcastForSync {
    private static int LATEST_SYNC_ROW_ID;

    public static int getLatestSyncRowId() {
        return LATEST_SYNC_ROW_ID;
    }

    public static void setLatestSyncRowId(int latestSyncRowId) {
        LATEST_SYNC_ROW_ID = latestSyncRowId;
    }
}
