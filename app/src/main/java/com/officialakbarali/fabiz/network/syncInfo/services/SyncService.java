package com.officialakbarali.fabiz.network.syncInfo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.officialakbarali.fabiz.MainActivity;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;
import com.officialakbarali.fabiz.network.VolleyRequest;
import com.officialakbarali.fabiz.network.syncInfo.data.SyncLogDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.getPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.getUsername;
import static com.officialakbarali.fabiz.data.CommonInformation.setPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.setUsername;
import static com.officialakbarali.fabiz.data.MyAppVersion.GET_MY_APP_VERSION;
import static com.officialakbarali.fabiz.network.syncInfo.NotificationFrame.CHANNEL_ID;

public class SyncService extends Service {
    FabizProvider provider;

    RequestQueue requestQueue;
    private List<SyncLogDetail> logDetailsList;
    private int indexOfLog, lengthOfLogList;

    public static String SYNC_BROADCAST_URL = "services.uiUpdateBroadcast";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fabiz Sync")
                .setContentText("File Uploading")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        Log.i("SyncService :", "Started");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("service_running", true);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SyncService :", "Destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startExecuteThisService();
            }
        }).start();
        return START_STICKY;
    }

    private void startExecuteThisService() {

        if (getUsername() == null || getPassword() == null) {
            stopSetUp("USER");
        } else {
            requestQueue = Volley.newRequestQueue(this);
            provider = new FabizProvider(getBaseContext(), true);

            Cursor syncCursor = provider.query(FabizContract.SyncLog.TABLE_NAME, new String[]{}, null, null,
                    FabizContract.SyncLog._ID + " ASC");

            lengthOfLogList = syncCursor.getCount();
            if (lengthOfLogList > 0) {
                indexOfLog = 0;
                logDetailsList = new ArrayList<>();

                while (syncCursor.moveToNext()) {
                    logDetailsList.add(new SyncLogDetail(syncCursor.getInt(syncCursor.getColumnIndexOrThrow(FabizContract.SyncLog._ID)),
                            syncCursor.getString(syncCursor.getColumnIndexOrThrow(FabizContract.SyncLog.TABLE_NAME)),
                            syncCursor.getString(syncCursor.getColumnIndexOrThrow(FabizContract.SyncLog.COLUMN_OPERATION))));
                }
                sendToServer();
            } else {
                checkForUpdate();
            }
        }
    }

    private void sendToServer() {
        HashMap<String, String> hashMap = getHashMapParm();

        final VolleyRequest volleyRequest = new VolleyRequest("sync.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        if (deleteRowFromSyncLog(indexOfLog)) {
                            indexOfLog++;
                            if (indexOfLog == lengthOfLogList) {
                                stopSetUp("Sync Successfully");
                            } else {
                                sendToServer();
                            }
                        } else {
                            stopSetUp("Failed to update local db");
                        }
                    } else {
                        if (jsonObject.getString("status").equals("VERSION")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("version", true);
                            editor.apply();
                            stopSetUp("VERSION");
                        } else if (jsonObject.getString("status").equals("PUSH")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("force_pull", true);
                            editor.apply();
                            stopSetUp("PUSH");
                        } else if (jsonObject.getString("status").equals("UPDATE")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("update_data", true);
                            editor.apply();
                            stopSetUp("UPDATE");
                        } else if (jsonObject.getString("status").equals("USER")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("my_username", null);
                            editor.putString("my_password", null);
                            editor.putBoolean("update_data", false);
                            editor.putBoolean("force_pull", false);
                            editor.apply();
                            setUsername(null);
                            setPassword(null);
                            stopSetUp("USER");
                        } else {
                            stopSetUp("Something went wrong");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopSetUp("Bad Response From Server");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    stopSetUp("Server Error");
                } else if (error instanceof TimeoutError) {
                    stopSetUp("Connection Timed Out");
                } else if (error instanceof NetworkError) {
                    stopSetUp("Bad Network Connection");
                }
            }
        });
        requestQueue.add(volleyRequest);
    }

    private HashMap<String, String> getHashMapParm() {
        HashMap<String, String> returnHashMap = new HashMap<>();

        returnHashMap.put("app_version", "" + GET_MY_APP_VERSION());
        returnHashMap.put("my_username", "" + getUsername());
        returnHashMap.put("my_password", "" + getPassword());

        SyncLogDetail syncLogDetailI = logDetailsList.get(indexOfLog);

        String tableName = syncLogDetailI.getTableName();
        String[] projection = getProjection(tableName);
        String selection = projection[0] + "=?";
        String[] selectionArg = new String[]{syncLogDetailI.getRawId() + ""};

        Cursor updateCursor = provider.query(tableName, projection, selection, selectionArg, null);

        if (updateCursor.getCount() < 1) {
            if (deleteRowFromSyncLog(indexOfLog)) {
                indexOfLog++;
                if (indexOfLog == lengthOfLogList) {
                    stopSetUp("Sync Successfully");
                } else {
                    sendToServer();
                }
            } else {
                stopSetUp("Failed to update local db");
            }
        } else {

        }
        return returnHashMap;
    }

    private String[] getProjection(String tableName) {
        String[] returnProj = null;


        switch (tableName) {
            case FabizContract.AccountDetail.TABLE_NAME:
                returnProj = new String[]{
                        FabizContract.AccountDetail._ID, FabizContract.AccountDetail.COLUMN_CUSTOMER_ID, FabizContract.AccountDetail.COLUMN_TOTAL,
                        FabizContract.AccountDetail.COLUMN_PAID, FabizContract.AccountDetail.COLUMN_DUE
                };
                break;
            case FabizContract.Item.TABLE_NAME:

                returnProj = new String[]{
                        FabizContract.Item._ID, FabizContract.Item.COLUMN_NAME, FabizContract.Item.COLUMN_BRAND,
                        FabizContract.Item.COLUMN_CATEGORY, FabizContract.Item.COLUMN_PRICE
                };

                break;
            case FabizContract.Customer.TABLE_NAME:
                returnProj = new String[]{
                        FabizContract.Customer._ID, FabizContract.Customer.COLUMN_NAME, FabizContract.Customer.COLUMN_PHONE,
                        FabizContract.Customer.COLUMN_EMAIL, FabizContract.Customer.COLUMN_ADDRESS
                };
                break;
            case FabizContract.BillDetail.TABLE_NAME:
                returnProj = new String[]{
                        FabizContract.BillDetail._ID, FabizContract.BillDetail.COLUMN_DATE, FabizContract.BillDetail.COLUMN_CUST_ID,
                        FabizContract.BillDetail.COLUMN_PRICE, FabizContract.BillDetail.COLUMN_QTY
                };
                break;
            case FabizContract.Cart.TABLE_NAME:
                returnProj = new String[]{
                        FabizContract.Cart._ID, FabizContract.Cart.COLUMN_BILL_ID, FabizContract.Cart.COLUMN_ITEM_ID, FabizContract.Cart.COLUMN_NAME, FabizContract.Cart.COLUMN_BRAND,
                        FabizContract.Cart.COLUMN_CATEGORY, FabizContract.Cart.COLUMN_PRICE, FabizContract.Cart.COLUMN_QTY, FabizContract.Cart.COLUMN_TOTAL, FabizContract.Cart.COLUMN_RETURN_QTY
                };
                break;
            case FabizContract.SalesReturn.TABLE_NAME:
                returnProj = new String[]{
                        FabizContract.SalesReturn._ID, FabizContract.SalesReturn.COLUMN_DATE, FabizContract.SalesReturn.COLUMN_BILL_ID, FabizContract.SalesReturn.COLUMN_ITEM_ID,
                        FabizContract.SalesReturn.COLUMN_PRICE,
                        FabizContract.SalesReturn.COLUMN_QTY, FabizContract.SalesReturn.COLUMN_TOTAL
                };
                break;
            case FabizContract.Payment.TABLE_NAME:
                returnProj = new String[]{
                        FabizContract.Payment._ID, FabizContract.Payment.COLUMN_CUST_ID, FabizContract.Payment.COLUMN_DATE, FabizContract.Payment.COLUMN_AMOUNT,
                        FabizContract.Payment.COLUMN_TOTAL, FabizContract.Payment.COLUMN_PAID, FabizContract.Payment.COLUMN_DUE
                };
                break;

        }

        return returnProj;
    }

    private boolean deleteRowFromSyncLog(int indexToDelete) {
        SyncLogDetail deleteItem = logDetailsList.get(indexToDelete);
        int deleteCount = provider.delete(FabizContract.SyncLog.TABLE_NAME, FabizContract.SyncLog._ID + "=?", new String[]{deleteItem.getRawId() + ""});
        return deleteCount > 0;
    }

    private void checkForUpdate() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("app_version", "" + GET_MY_APP_VERSION());
        hashMap.put("my_username", "" + getUsername());
        hashMap.put("my_password", "" + getPassword());

        final VolleyRequest volleyRequest = new VolleyRequest("simple.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        stopSetUp("Sync Successfully");
                    } else {
                        if (jsonObject.getString("status").equals("VERSION")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("version", true);
                            editor.apply();
                            stopSetUp("VERSION");
                        } else if (jsonObject.getString("status").equals("PUSH")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("force_pull", true);
                            editor.apply();
                            stopSetUp("PUSH");
                        } else if (jsonObject.getString("status").equals("UPDATE")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SyncService.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("update_data", true);
                            editor.apply();
                            stopSetUp("UPDATE");
                        } else {
                            stopSetUp("Something went wrong");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    stopSetUp("Bad Response From Server");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    stopSetUp("Server Error");
                } else if (error instanceof TimeoutError) {
                    stopSetUp("Connection Timed Out");
                } else if (error instanceof NetworkError) {
                    stopSetUp("Bad Network Connection");
                }
            }
        });
        requestQueue.add(volleyRequest);
    }

    private void stopSetUp(String msgToPassed) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("service_running", false);
        editor.apply();

        Intent updateUiIntent = new Intent(SYNC_BROADCAST_URL);
        updateUiIntent.putExtra("msgPassed", msgToPassed);
        sendBroadcast(updateUiIntent);

        stopSelf();

        Log.i("SyncService :", "Executed");
    }
}
