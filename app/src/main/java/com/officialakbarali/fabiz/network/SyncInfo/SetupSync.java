package com.officialakbarali.fabiz.network.SyncInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.officialakbarali.fabiz.network.SyncInfo.data.SyncLog;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.List;


public class SetupSync {
    private List<SyncLog> syncLogList;
    private Context context;
    private FabizProvider provider;

    public SetupSync(Context context, List<SyncLog> syncLogList, FabizProvider provider) {
        this.context = context;
        this.syncLogList = syncLogList;
        this.provider = provider;
        if (isNetworkConnected()) {
            Cursor checkSyncStatus = provider.query(FabizContract.SyncLog.TABLE_NAME, new String[]{FabizContract.SyncLog._ID},
                    null, null, null);
            if (checkSyncStatus.getCount() <= 0) {
                //TODO *****************DON'T FORGET TO STOP TRANSACTION************
                //TODO SYNC change below and replace with server update ****IMPORTANT = UPDATE LATEST_SYNC_ROW
                addCurrentDataToSyncTable();
            } else {
                addCurrentDataToSyncTable();
                //TODO TURN SERVICE ON IF OFF
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

        try {
            int i = 0;
            while (i < syncLogList.size()) {
                ContentValues values = new ContentValues();
                values.put(FabizContract.SyncLog.COLUMN_ROW_ID, syncLogList.get(i).getRawId());
                values.put(FabizContract.SyncLog.COLUMN_TABLE_NAME, syncLogList.get(i).getTableName());
                values.put(FabizContract.SyncLog.COLUMN_OPERATION, syncLogList.get(i).getOperation());
                long id = provider.insert(FabizContract.SyncLog.TABLE_NAME, values);

                if (id > 0) {
                    Log.i("SetupSync", "Sync Row Created Id:" + id);
                } else {
                    Toast.makeText(context, "Something went wrong,please report this to customer care", Toast.LENGTH_SHORT).show();
                    break;
                }
                i++;
            }

            if (i == syncLogList.size()) {
                //********TRANSACTION SUCCESSFUL
                provider.successfulTransaction();
            }

        } catch (Error e) {
            Toast.makeText(context, "Something went wrong,please report this to customer care", Toast.LENGTH_SHORT).show();
        } finally {
            //******TRANSACTION FINISH
            provider.finishTransaction();
        }
    }
}
