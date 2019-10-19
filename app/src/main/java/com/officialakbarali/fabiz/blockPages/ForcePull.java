package com.officialakbarali.fabiz.blockPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.officialakbarali.fabiz.LogIn;
import com.officialakbarali.fabiz.MainHome;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.ServiceResumeCheck;
import com.officialakbarali.fabiz.network.syncInfo.services.ForcePullService;


import static com.officialakbarali.fabiz.network.syncInfo.services.ForcePullService.FORCE_SYNC_BROADCAST_URL;

public class ForcePull extends AppCompatActivity {
    TextView textMsg;
    Button pullDataBtn;

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_pull);

        textMsg = findViewById(R.id.txt_msg);
        pullDataBtn = findViewById(R.id.pull_btn);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msgPassed = intent.getExtras().getString("msgPassed");

                if (msgPassed.matches("VERSION")) {
                    Intent versionIntent = new Intent(ForcePull.this, AppVersion.class);
                    versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(versionIntent);
                } else if (msgPassed.matches("USER")) {
                    Intent logIntent = new Intent(ForcePull.this, LogIn.class);
                    logIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logIntent);
                } else if (msgPassed.matches("SUCCESS")) {
                    Intent mainHomeIntent = new Intent(ForcePull.this, MainHome.class);
                    mainHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainHomeIntent);
                } else if (msgPassed.matches("FAILED")) {
                    textMsg.setVisibility(View.VISIBLE);
                    textMsg.setText("Failed to Sync");
                    pullDataBtn.setVisibility(View.GONE);
                } else if (msgPassed.matches("PAUSE")) {
                    textMsg.setVisibility(View.VISIBLE);
                    textMsg.setText("Server is currently syncing with desktop.Please try after some time");
                    pullDataBtn.setVisibility(View.GONE);
                } else if (msgPassed.matches("PUSH")) {
                    textMsg.setVisibility(View.GONE);
                    pullDataBtn.setVisibility(View.VISIBLE);
                    pullDataBtn.setText("Please Sync Again,Something new added");
                } else {
                    textMsg.setVisibility(View.VISIBLE);
                    textMsg.setText(msgPassed);
                    pullDataBtn.setVisibility(View.GONE);
                }
            }
        };

        pullDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ForcePull.this);


                String userName = sharedPreferences.getString("my_username", null);
                String password = sharedPreferences.getString("my_password", null);

                if (userName != null && password != null) {
                    boolean isServiceRunning = sharedPreferences.getBoolean("force_service_running", false);
                    if (!isServiceRunning) {
                        Intent serviceIntent = new Intent(ForcePull.this, ForcePullService.class);
                        ContextCompat.startForegroundService(getBaseContext(), serviceIntent);
                        pullDataBtn.setVisibility(View.GONE);
                        textMsg.setVisibility(View.VISIBLE);
                    } else {
                        pullDataBtn.setVisibility(View.GONE);
                        textMsg.setVisibility(View.VISIBLE);
                    }
                } else {
                    Intent loginIntent = new Intent(ForcePull.this, LogIn.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                }
            }
        });

        final Button logOut = findViewById(R.id.log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ForcePull.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("my_username", null);
                editor.putString("my_password", null);
                editor.putBoolean("update_data", false);
                editor.putBoolean("force_pull", false);
                editor.apply();

                Intent logIntent = new Intent(ForcePull.this, LogIn.class);
                logIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(FORCE_SYNC_BROADCAST_URL));

        new ServiceResumeCheck(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isServiceRunning = sharedPreferences.getBoolean("force_service_running", false);
        if (isServiceRunning) {
            pullDataBtn.setVisibility(View.GONE);
            textMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
