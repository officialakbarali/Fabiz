package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.officialakbarali.fabiz.customer.Customer;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.item.Item;
import com.officialakbarali.fabiz.network.syncInfo.SyncFromAppToServer;
import com.officialakbarali.fabiz.network.syncInfo.SyncInformation;
import com.officialakbarali.fabiz.requestStock.RequestStock;

import java.util.ArrayList;

import static com.officialakbarali.fabiz.data.CommonInformation.SET_DECIMAL_LENGTH;
import static com.officialakbarali.fabiz.requestStock.RequestStock.itemsForRequest;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialSetup();

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

        Button requestStockButton = findViewById(R.id.item_request);
        requestStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestStockShow = new Intent(MainActivity.this, RequestStock.class);
                itemsForRequest = new ArrayList<>();
                startActivity(requestStockShow);
            }
        });


        Button viewSyncButton = findViewById(R.id.view_sync);
        viewSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewSyncIntent = new Intent(MainActivity.this, SyncInformation.class);
                startActivity(viewSyncIntent);
            }
        });

        Button addDummy = findViewById(R.id.dummy);
        addDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeDummyItems();
            }
        });


    }

    private void initialSetup() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int DECIMAL_PRECISION = sharedPreferences.getInt("decimal_precision", 3);
        SET_DECIMAL_LENGTH(DECIMAL_PRECISION);
    }

    private void insertSomeDummyItems() {
        FabizProvider fabizProvider = new FabizProvider(this, true);
        ContentValues values = new ContentValues();

        values.put(FabizContract.Item.COLUMN_NAME, "EMINEM");
        values.put(FabizContract.Item.COLUMN_BRAND, "RAP");
        values.put(FabizContract.Item.COLUMN_CATEGORY, "MUSIC");
        values.put(FabizContract.Item.COLUMN_BARCODE, "DR2039");
        values.put(FabizContract.Item.COLUMN_PRICE, "250.123");
        Log.i("Item table Filling", "Inserted Id:" + fabizProvider.insert(FabizContract.Item.TABLE_NAME, values));

        values = new ContentValues();
        values.put(FabizContract.Item.COLUMN_NAME, "JUSTIN");
        values.put(FabizContract.Item.COLUMN_BRAND, "POP");
        values.put(FabizContract.Item.COLUMN_CATEGORY, "MUSIC");
        values.put(FabizContract.Item.COLUMN_PRICE, "10.321");
        Log.i("Item table Filling", "Inserted Id:" + fabizProvider.insert(FabizContract.Item.TABLE_NAME, values));

        values = new ContentValues();
        values.put(FabizContract.Item.COLUMN_NAME, "ELON");
        values.put(FabizContract.Item.COLUMN_BRAND, "TESLA");
        values.put(FabizContract.Item.COLUMN_CATEGORY, "ENTREPRENEUR");
        values.put(FabizContract.Item.COLUMN_PRICE, "500.510");
        Log.i("Item table Filling", "Inserted Id:" + fabizProvider.insert(FabizContract.Item.TABLE_NAME, values));
    }
}
