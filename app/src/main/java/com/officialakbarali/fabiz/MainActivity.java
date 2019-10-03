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
import com.officialakbarali.fabiz.network.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.officialakbarali.fabiz.data.AppVersion.GET_MY_APP_VERSION;

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

        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            //TODO APP VERSION PAGE
        } else {
            String userName = sharedPreferences.getString("my_username", null);
            String password = sharedPreferences.getString("my_password", null);

            if (userName == null || password == null) {
                checkLatestVersion();
            } else {
                boolean forcePullActivate =
                        false;//TODO REMOVE THIS FALSE AND UNCOMMENT //sharedPreferences.getBoolean("force_pull", false);
                if (forcePullActivate) {
                    //TODO FORCE PULL PAGE
                } else {
                    boolean updateData = sharedPreferences.getBoolean("update_data", false);
                    if (updateData) {
                        //TODO UPDATE DATA PAGE
                    } else {
                        //TODO START SERVICE
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

        final VolleyRequest volleyRequest = new VolleyRequest("index.php", hashMap, new Response.Listener<String>() {
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
                            //TODO APP_VERSION_PAGE
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
