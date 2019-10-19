package com.officialakbarali.fabiz.customer.route;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.adapter.CustomerAdapter;
import com.officialakbarali.fabiz.customer.data.CustomerDetail;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;

import java.util.ArrayList;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.getDayNameFromNumber;

public class RouteModify extends AppCompatActivity implements CustomerAdapter.CustomerAdapterOnClickListener {
    private String TODAY;

    CustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_modify);

        TODAY = getIntent().getStringExtra("today");

        TextView labelText = findViewById(R.id.cust_route_label);
        labelText.setText(getDayNameFromNumber(TODAY));

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

        RecyclerView recyclerView = findViewById(R.id.cust_route_recycler);
        adapter = new CustomerAdapter(this, this, TODAY);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        showCustomer(null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
    }

    @Override
    public void onClick(CustomerDetail customer) {
        if (customer.getDay() == null) {
            toggleCurrentDay(customer.getId(), true);
        } else {
            if (customer.getDay().matches(TODAY)) {
                toggleCurrentDay(customer.getId(), false);
            } else {
                toggleCurrentDay(customer.getId(), true);
            }
        }
    }

    private void toggleCurrentDay(String idToOperate, boolean add) {
        ContentValues updateValues = new ContentValues();
        if (add) {
            updateValues.put(FabizContract.Customer.COLUMN_DAY, TODAY);
        } else {
            updateValues.put(FabizContract.Customer.COLUMN_DAY, "NA");
        }

        FabizProvider provider = new FabizProvider(this, true);
        provider.createTransaction();
        int updatedRows = provider.update(FabizContract.Customer.TABLE_NAME, updateValues,
                FabizContract.Customer._ID + "=?", new String[]{idToOperate + ""});

        if (updatedRows > 0) {
            provider.successfulTransaction();
            showCustomer(null, null);
        }
        provider.finishTransaction();
    }


    private void showCustomer(String selection, String[] selectionArg) {
        FabizProvider provider = new FabizProvider(this, false);
        String[] projection = {FabizContract.Customer._ID, FabizContract.Customer.COLUMN_NAME, FabizContract.Customer.COLUMN_PHONE,
                FabizContract.Customer.COLUMN_EMAIL, FabizContract.Customer.COLUMN_ADDRESS, FabizContract.Customer.COLUMN_DAY
        };
        Cursor custCursor = provider.query(FabizContract.Customer.TABLE_NAME, projection,
                selection, selectionArg
                , FabizContract.Customer.COLUMN_NAME + " ASC");

        List<CustomerDetail> customerList = new ArrayList<>();
        while (custCursor.moveToNext()) {
            customerList.add(new CustomerDetail(custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer._ID)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_NAME)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_PHONE)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_EMAIL)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_ADDRESS)),
                    custCursor.getString(custCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_DAY))
            ));
        }
        adapter.swapAdapter(customerList);
    }

    private String getSelection(String filterFromForm) {
        String caseSelection;

        switch (filterFromForm) {
            case "Id":
                caseSelection = FabizContract.Customer._ID;
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
