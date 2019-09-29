package com.officialakbarali.fabiz.network.syncInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.officialakbarali.fabiz.MainActivity;
import com.officialakbarali.fabiz.R;

public class SyncInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_information);

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
