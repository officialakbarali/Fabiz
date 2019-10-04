package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.officialakbarali.fabiz.blockPages.AppVersion;
import com.officialakbarali.fabiz.data.db.FabizProvider;

import static com.officialakbarali.fabiz.data.CommonInformation.setPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.setUsername;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(this, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(versionIntent);
        }
    }

    public void login(View view) {
        String userName = "username";
        String password = "password";

        SharedPreferences
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("my_username", userName);
        editor.putString("my_password", password);
        editor.putBoolean("update_data", false);
        editor.putBoolean("force_pull", false);//TODO SET THIS TO TRUE
        editor.apply();

        setUsername(userName);
        setPassword(password);

        FabizProvider provider = new FabizProvider(this, true);
        provider.deleteAllTables();

        Intent mainHomeIntent = new Intent(LogIn.this, MainHome.class);
        mainHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainHomeIntent);
    }
}
