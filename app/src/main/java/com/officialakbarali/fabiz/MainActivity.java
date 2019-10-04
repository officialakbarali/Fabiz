package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.officialakbarali.fabiz.blockPages.AppVersion;
import com.officialakbarali.fabiz.blockPages.ForcePull;
import com.officialakbarali.fabiz.blockPages.UpdateData;
import com.officialakbarali.fabiz.network.SyncService;
import com.officialakbarali.fabiz.network.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.officialakbarali.fabiz.data.CommonInformation.SET_DECIMAL_LENGTH;
import static com.officialakbarali.fabiz.data.CommonInformation.setPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.setUsername;
import static com.officialakbarali.fabiz.data.MyAppVersion.GET_MY_APP_VERSION;

public class MainActivity extends AppCompatActivity {

    private Toast toast;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialSetup();
            }
        }, 3000);
    }

    private void initialSetup() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int DECIMAL_PRECISION = sharedPreferences.getInt("decimal_precision", 3);
        SET_DECIMAL_LENGTH(DECIMAL_PRECISION);

        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(this, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(versionIntent);
        } else {
            String userName = sharedPreferences.getString("my_username", null);
            String password = sharedPreferences.getString("my_password", null);

            if (userName == null || password == null) {
                checkLatestVersion();
            } else {
                setUsername(userName);
                setPassword(password);

                boolean forcePullActivate =
                        sharedPreferences.getBoolean("force_pull", false);
                if (forcePullActivate) {
                    Intent forcePullIntent = new Intent(this, ForcePull.class);
                    forcePullIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(forcePullIntent);
                } else {
                    boolean updateData = sharedPreferences.getBoolean("update_data", false);
                    if (updateData) {
                        Intent updateDataIntent = new Intent(this, UpdateData.class);
                        updateDataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(updateDataIntent);
                    } else {
                        boolean isServiceRunning = sharedPreferences.getBoolean("service_running", false);
                        if (!isServiceRunning) {
                            new SyncService();
                        }
                        Intent mainHomeIntent = new Intent(this, MainHome.class);
                        mainHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainHomeIntent);
                    }
                }
            }
        }

    }

    private void checkLatestVersion() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("app_version", "" + GET_MY_APP_VERSION());

        final VolleyRequest volleyRequest = new VolleyRequest("version.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        Intent loginIntent = new Intent(MainActivity.this, LogIn.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                    } else {
                        if (jsonObject.getString("status").equals("VERSION")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("version", true);
                            editor.apply();
                            Intent versionIntent = new Intent(MainActivity.this, AppVersion.class);
                            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(versionIntent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Bad Response From Server");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    showToast("Server Error");
                } else if (error instanceof TimeoutError) {
                    showToast("Connection Timed Out");
                } else if (error instanceof NetworkError) {
                    showToast("Bad Network Connection");
                }
            }
        });
        requestQueue.add(volleyRequest);
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }
}
