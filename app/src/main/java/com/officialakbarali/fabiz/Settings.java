package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.officialakbarali.fabiz.blockPages.AppVersion;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;

import static com.officialakbarali.fabiz.data.CommonInformation.setPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.setUsername;

public class Settings extends AppCompatActivity {
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(this, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(versionIntent);
        }

    }

    public void logout(View view) {
        FabizProvider provider = new FabizProvider(this, false);
        Cursor cursor = provider.query(FabizContract.SyncLog.TABLE_NAME, new String[]{FabizContract.SyncLog._ID}, null, null, null);
        if (cursor.getCount() > 0) {
            showToast("Some data need to be sync! Please try after sometime");
        } else {
            SharedPreferences
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("my_username", null);
            editor.putString("my_password", null);
            editor.putBoolean("update_data", false);
            editor.putBoolean("force_pull", false);
            editor.apply();

            setUsername(null);
            setPassword(null);
        }
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }
}
