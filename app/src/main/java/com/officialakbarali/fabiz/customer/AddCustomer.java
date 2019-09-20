package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.officialakbarali.fabiz.R;

public class AddCustomer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

    /*
        ContentValues values = new ContentValues();
        values.put(FabizContract.Customer.COLUMN_NAME, "Sharaf");
        values.put(FabizContract.Customer.COLUMN_PHONE, "9798612345");
        values.put(FabizContract.Customer.COLUMN_EMAIL, "sharaf@gm.com");
        values.put(FabizContract.Customer.COLUMN_ADDRESS, "Pothady House");

        FabizProvider fabizProvider = new FabizProvider(this);
        long id = fabizProvider.customerInsert(values);
        Toast.makeText(this, "Id:" + id, Toast.LENGTH_SHORT).show();
    */
    }
}
