package com.officialakbarali.fabiz.network.syncInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.officialakbarali.fabiz.MainActivity;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.blockPages.AppVersion;

public class SyncInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_information);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(this, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(versionIntent);
        }


        Button viewSyncLogButton = findViewById(R.id.view_sync_log);
        viewSyncLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent syncLogIntent = new Intent(SyncInformation.this, SyncFromAppToServer.class);
                startActivity(syncLogIntent);
            }
        });
    }
}
