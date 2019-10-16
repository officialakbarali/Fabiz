package com.officialakbarali.fabiz.network.syncInfo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.officialakbarali.fabiz.data.MyAppVersion.GET_MY_APP_VERSION;
import static com.officialakbarali.fabiz.network.syncInfo.NotificationFrame.CHANNEL_ID;

public class ForcePullService extends Service {
    String userName;
    String password;
    public static String FORCE_SYNC_BROADCAST_URL = "force_services.uiUpdateBroadcast";
    RequestQueue requestQueue;
    FabizProvider provider;

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
                .setContentText("Fetching file from server")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(2, notification);


        Log.i("ForcePullService :", "Started");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sharedPreferences.getString("my_username", null);
        password = sharedPreferences.getString("my_password", null);

//    todo uncomment this    SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("force_service_running", true);
//        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ForcePullService :", "Destroyed");
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


    private void stopSetUp(String msgToPassed) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("force_service_running", false);
        editor.apply();

        Intent updateUiIntent = new Intent(FORCE_SYNC_BROADCAST_URL);
        updateUiIntent.putExtra("msgPassed", msgToPassed);
        sendBroadcast(updateUiIntent);
        stopSelf();
        Log.i("ForcePullService :", "Executed");
    }

    private void startExecuteThisService() {
        if (userName == null || password == null) {
            stopSetUp("USER");
        } else {
            requestQueue = Volley.newRequestQueue(this);
            checkForUpdate();
        }
    }

    private void checkForUpdate() {
        Log.i("ForcePullService", "Request Sent");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("app_version", "" + GET_MY_APP_VERSION());
        hashMap.put("my_username", "" + userName);
        hashMap.put("my_password", "" + password);

        final VolleyRequest volleyRequest = new VolleyRequest("forcePull.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        addDataToDb(jsonObject);

                    } else {
                        switch (jsonObject.getString("status")) {
                            case "VERSION": {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ForcePullService.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("version", true);
                                editor.apply();
                                stopSetUp("VERSION");
                                break;
                            }
                            case "USER": {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ForcePullService.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("my_username", null);
                                editor.putString("my_password", null);
                                editor.putBoolean("update_data", false);
                                editor.putBoolean("force_pull", false);
                                editor.apply();
                                stopSetUp("USER");
                                break;
                            }
                            case "ITEM": {
                                stopSetUp("Server is empty");
                                break;
                            }
                            default:
                                stopSetUp("Something went wrong");
                                break;
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

    private void addDataToDb(JSONObject jsonObject) {
        provider = new FabizProvider(this, true);
        provider.deleteAllTables();
        provider.createTransaction();
        try {
            if (insertItem(jsonObject.getJSONArray(FabizContract.Item.TABLE_NAME))) {
                if (insertCustomer(jsonObject)) {
                    if (insertBillDetail(jsonObject)) {
                        if (insertCart(jsonObject)) {
                            if (insertSalesReturn(jsonObject)) {
                                if (insertPayment(jsonObject)) {
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("force_pull", false);
                                    editor.putBoolean("update_data", false);
                                    editor.apply();

                                    provider.successfulTransaction();
                                    provider.finishTransaction();
                                    stopSetUp("SUCCESS");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        provider.finishTransaction();
        stopSetUp("FAILED");
    }

    private boolean insertItem(JSONArray itemArray) throws JSONException {
        boolean thisSuccess = true;
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject obj = itemArray.getJSONObject(i);

            ContentValues values = new ContentValues();
            values.put(FabizContract.Item._ID, obj.getString(FabizContract.Item.TABLE_NAME + FabizContract.Item._ID));
            values.put(FabizContract.Item.COLUMN_BARCODE, obj.getString(FabizContract.Item.COLUMN_BARCODE));
            values.put(FabizContract.Item.COLUMN_NAME, obj.getString(FabizContract.Item.COLUMN_NAME));
            values.put(FabizContract.Item.COLUMN_BRAND, obj.getString(FabizContract.Item.COLUMN_BRAND));
            values.put(FabizContract.Item.COLUMN_CATEGORY, obj.getString(FabizContract.Item.COLUMN_CATEGORY));
            values.put(FabizContract.Item.COLUMN_PRICE, obj.getString(FabizContract.Item.COLUMN_PRICE));
            long insertId = provider.insert(FabizContract.Item.TABLE_NAME, values);
            if (insertId < 0) {
                thisSuccess = false;
            }
        }
        return thisSuccess;
    }

    private boolean insertCustomer(JSONObject jsonObject) {
        boolean thisSuccess = true;
        return thisSuccess;
    }

    private boolean insertBillDetail(JSONObject jsonObject) {
        boolean thisSuccess = true;
        return thisSuccess;
    }

    private boolean insertCart(JSONObject jsonObject) {
        boolean thisSuccess = true;
        return thisSuccess;
    }

    private boolean insertSalesReturn(JSONObject jsonObject) {
        boolean thisSuccess = true;
        return thisSuccess;
    }

    private boolean insertPayment(JSONObject jsonObject) {
        boolean thisSuccess = true;
        return thisSuccess;
    }
}
