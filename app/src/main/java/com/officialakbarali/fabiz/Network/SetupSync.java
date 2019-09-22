package com.officialakbarali.fabiz.Network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.util.Log;

import com.officialakbarali.fabiz.Network.data.SyncLog;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.List;

import static com.officialakbarali.fabiz.Network.BroadcastForSync.setLatestSyncRowId;

public class SetupSync {
    private List<SyncLog> syncLogList;
    private Context context;
    private FabizProvider provider;

    public SetupSync(Context context, List<SyncLog> syncLogList) {
        this.context = context;
        this.syncLogList = syncLogList;
        provider = new FabizProvider(context);
        if (isNetworkConnected()) {
            Cursor checkSyncStatus = provider.query(FabizContract.SyncLog.TABLE_NAME, new String[]{FabizContract.SyncLog._ID},
                    null, null, null);
            if (checkSyncStatus.getCount() <= 0) {
                //TODO SYNC change below and replace with server update ****IMPORTANT = UPDATE LATEST_SYNC_ROW
                addCurrentDataToSyncTable();
            } else {
                addCurrentDataToSyncTable();
            }
        } else {
            addCurrentDataToSyncTable();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void addCurrentDataToSyncTable() {
        long id = 0;
        for (int i = 0; i < syncLogList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(FabizContract.SyncLog.COLUMN_ROW_ID, syncLogList.get(i).getRawId());
            values.put(FabizContract.SyncLog.COLUMN_TABLE_NAME, syncLogList.get(i).getTableName());
            values.put(FabizContract.SyncLog.COLUMN_OPERATION, syncLogList.get(i).getOperation());
            id = provider.insert(FabizContract.SyncLog.TABLE_NAME, values);
            Log.i("SetupSync", "Sync Row Created Id:" + id);
        }
        setLatestSyncRowId(Integer.parseInt(id + ""));
    }
}
