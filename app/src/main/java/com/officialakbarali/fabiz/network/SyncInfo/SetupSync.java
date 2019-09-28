package com.officialakbarali.fabiz.network.SyncInfo;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.officialakbarali.fabiz.network.SyncInfo.data.SyncLog;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.List;


public class SetupSync {
    //PUBLIC STRING FOR OPERATION

    public static String OP_INSERT = "INSERT";
    public static String OP_UPDATE = "UPDATE";
    public static String OP_DELETE = "DELETE";

    //***************************


    private List<SyncLog> syncLogList;
    private Context context;
    private FabizProvider provider;

    public SetupSync(Context context, List<SyncLog> syncLogList, FabizProvider provider) {
        this.context = context;
        this.syncLogList = syncLogList;
        this.provider = provider;

        addCurrentDataToSyncTable();

        if (isNetworkConnected()) {
            //TODO TURN SERVICE ON IF OFF
            //TODO IF ON THEN SETUP SOME FLAG FOR RE-CHECK THE SYNC_TABLE
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
                    Log.i("SetupSync", "FAILED IN SAVING");
                    Toast.makeText(context, "Something went wrong,please report this to customer care", Toast.LENGTH_SHORT).show();
                    break;
                }
                i++;
            }

            if (i == syncLogList.size()) {
                //********TRANSACTION SUCCESSFUL
                Log.i("SetupSync", "SUCCESSFULLY COMPLETED");
                provider.successfulTransaction();
            }

        } catch (Error e) {
            Log.i("SetupSync", "FAILED IN TRY CATCH");
            Toast.makeText(context, "Something went wrong,please report this to customer care", Toast.LENGTH_SHORT).show();
        } finally {
            //******TRANSACTION FINISH
            provider.finishTransaction();
        }
    }
}
