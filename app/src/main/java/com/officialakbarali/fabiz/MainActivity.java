package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.officialakbarali.fabiz.customer.Customer;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.item.Item;

import static com.officialakbarali.fabiz.Network.BroadcastForSync.setLatestSyncRowId;
import static com.officialakbarali.fabiz.data.CommonInformation.SET_DECIMAL_LENGTH;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialSetup();

        Button addDummy = findViewById(R.id.dummy);
        addDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeDummyItems();
            }
        });

        Button customerIntent = findViewById(R.id.cust);
        customerIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent custIntent = new Intent(MainActivity.this, Customer.class);
               // custIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(custIntent);
            }
        });

        Button itemIntent = findViewById(R.id.item);
        itemIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemShow = new Intent(MainActivity.this, Item.class);
                //itemShow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(itemShow);
            }
        });
    }

    private void initialSetup() {
        FabizProvider provider = new FabizProvider(this);
        Cursor SYNC_SETUP_CURSOR = provider.query(FabizContract.SyncLog.TABLE_NAME, new String[]{FabizContract.SyncLog._ID + ""},
                null, null, FabizContract.SyncLog._ID + " DESC LIMIT 1");

        if (SYNC_SETUP_CURSOR.moveToNext()) {
            setLatestSyncRowId(SYNC_SETUP_CURSOR.getInt(SYNC_SETUP_CURSOR.getColumnIndexOrThrow(FabizContract.SyncLog._ID)));
        } else {
            setLatestSyncRowId(1);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int DECIMAL_PRECISION = sharedPreferences.getInt("decimal_precision", 3);
        SET_DECIMAL_LENGTH(DECIMAL_PRECISION);
    }

    private void insertSomeDummyItems() {
        FabizProvider fabizProvider = new FabizProvider(this);
        ContentValues values = new ContentValues();

        values.put(FabizContract.Item.COLUMN_NAME, "CHICKEN");
        values.put(FabizContract.Item.COLUMN_BRAND, "KFC");
        values.put(FabizContract.Item.COLUMN_CATEGORY, "FOOD");
        values.put(FabizContract.Item.COLUMN_PRICE, "30");
        Log.i("Item table Filling", "Inserted Id:" + fabizProvider.insert(FabizContract.Item.TABLE_NAME, values));

        values = new ContentValues();
        values.put(FabizContract.Item.COLUMN_NAME, "BROASTED");
        values.put(FabizContract.Item.COLUMN_BRAND, "KFC");
        values.put(FabizContract.Item.COLUMN_CATEGORY, "FOOD");
        values.put(FabizContract.Item.COLUMN_PRICE, "50");
        Log.i("Item table Filling", "Inserted Id:" + fabizProvider.insert(FabizContract.Item.TABLE_NAME, values));

        values = new ContentValues();
        values.put(FabizContract.Item.COLUMN_NAME, "BROASTED");
        values.put(FabizContract.Item.COLUMN_BRAND, "CHICKIN");
        values.put(FabizContract.Item.COLUMN_CATEGORY, "FOOD");
        values.put(FabizContract.Item.COLUMN_PRICE, "45");
        Log.i("Item table Filling", "Inserted Id:" + fabizProvider.insert(FabizContract.Item.TABLE_NAME, values));
    }
}
