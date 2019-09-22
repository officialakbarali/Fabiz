package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.officialakbarali.fabiz.R;

public class Sales extends AppCompatActivity {
    private int custId;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        recyclerView = findViewById(R.id.cust_sale_recycler);
    }
}
