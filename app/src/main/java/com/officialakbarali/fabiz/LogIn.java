package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.officialakbarali.fabiz.data.db.FabizProvider;

import static com.officialakbarali.fabiz.data.CommonInformation.SET_DECIMAL_LENGTH;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


    }

    public void logn(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("my_username", "username");
        editor.putString("my_password", "password");
        editor.putBoolean("update_data", false);
        editor.putBoolean("force_pull", true);
        editor.apply();
        int DECIMAL_PRECISION = sharedPreferences.getInt("decimal_precision", 3);
        SET_DECIMAL_LENGTH(DECIMAL_PRECISION);

        FabizProvider provider = new FabizProvider(this, true);
        provider.deleteAllTables();

        Intent mainHomeIntent = new Intent(LogIn.this, MainHome.class);
        mainHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainHomeIntent);
    }
}
