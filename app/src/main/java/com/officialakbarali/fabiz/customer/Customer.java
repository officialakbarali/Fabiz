package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.adapter.CustomerAdapter;
import com.officialakbarali.fabiz.customer.data.CustomerDetail;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.data.barcode.FabizBarcode;

import java.util.ArrayList;
import java.util.List;

import static com.officialakbarali.fabiz.data.barcode.FabizBarcode.FOR_CUSTOMER;

public class Customer extends AppCompatActivity implements CustomerAdapter.CustomerAdapterOnClickListener {
    RecyclerView recyclerView;
    CustomerAdapter customerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Button scanFromBarcodeButton = findViewById(R.id.cust_barcode);
        scanFromBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanFromBarcodeIntent = new Intent(Customer.this, FabizBarcode.class);
                scanFromBarcodeIntent.putExtra("FOR_WHO", FOR_CUSTOMER + "");
                startActivity(scanFromBarcodeIntent);
            }
        });

        Button addCustomerButton = findViewById(R.id.add_cust);
        addCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CustomerIntent = new Intent(Customer.this, AddCustomer.class);
                startActivity(CustomerIntent);
            }
        });

        Button searchButton = findViewById(R.id.search_cust_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.cust_search);
                if (!editText.getText().toString().trim().matches("")) {
                    Spinner filterSpinner = findViewById(R.id.cust_filter);
                    String selection = getSelection(String.valueOf(filterSpinner.getSelectedItem()));
                    showCustomer(selection, new String[]{editText.getText().toString().trim() + "%"});
                } else {
                    showCustomer(null, null);
                }

            }
        });

        Button manageRouteButton = findViewById(R.id.cust_manage_route);
        manageRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manageRouteIntent = new Intent(Customer.this, ManageRoute.class);
                startActivity(manageRouteIntent);
            }
        });

        recyclerView = findViewById(R.id.cust_recycler);
        customerAdapter = new CustomerAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(customerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCustomer(null, null);
    }

    @Override
    public void onClick(String mCustomerCurrentRaw, String mCustomerSelectedName) {
        Intent showHome = new Intent(Customer.this, Home.class);
        showHome.putExtra("id", mCustomerCurrentRaw);
        startActivity(showHome);
    }


    private void showCustomer(String selection, String[] selectionArg) {
        FabizProvider provider = new FabizProvider(this, false);
        String[] projection = {FabizContract.Customer._ID, FabizContract.Customer.COLUMN_NAME, FabizContract.Customer.COLUMN_PHONE,
                FabizContract.Customer.COLUMN_EMAIL, FabizContract.Customer.COLUMN_ADDRESS};
        Cursor custCursor = provider.query(FabizContract.Customer.TABLE_NAME, projection,
                selection, selectionArg
                , FabizContract.Customer.COLUMN_NAME + " ASC");

        List<CustomerDetail> customerList = new ArrayList<>();
        while (custCursor.moveToNext()) {
            customerList.add(new CustomerDetail(custCursor.getInt(custCursor.getColumnIndexOrThrow(FabizContract.Customer._ID)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_NAME)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_PHONE)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_EMAIL)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_ADDRESS))
            ));
        }
        customerAdapter.swapAdapter(customerList);
    }


    private String getSelection(String filterFromForm) {
        String caseSelection;

        switch (filterFromForm) {
            case "Name":
                caseSelection = FabizContract.Customer.COLUMN_NAME;
                break;
            case "Id":
                caseSelection = FabizContract.Item._ID;
                break;
            case "Phone":
                caseSelection = FabizContract.Customer.COLUMN_PHONE;
                break;
            case "Email":
                caseSelection = FabizContract.Customer.COLUMN_EMAIL;
                break;
            case "Address":
                caseSelection = FabizContract.Customer.COLUMN_ADDRESS;
                break;
            default:
                caseSelection = FabizContract.Customer.COLUMN_NAME;
        }

        return caseSelection + " LIKE ?";
    }
}
