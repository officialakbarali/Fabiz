package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.officialakbarali.fabiz.customer.Customer;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.item.Item;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button customerIntent = findViewById(R.id.cust);
        customerIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent custIntent = new Intent(MainActivity.this, Customer.class);
                custIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(custIntent);
            }
        });

        Button itemIntent = findViewById(R.id.item);
        itemIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemShow = new Intent(MainActivity.this, Item.class);
                itemShow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(itemShow);
            }
        });
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
