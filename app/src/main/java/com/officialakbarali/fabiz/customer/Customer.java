package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.officialakbarali.fabiz.LogIn;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.blockPages.AppVersion;
import com.officialakbarali.fabiz.blockPages.ForcePull;
import com.officialakbarali.fabiz.blockPages.UpdateData;
import com.officialakbarali.fabiz.customer.adapter.CustomerAdapter;
import com.officialakbarali.fabiz.customer.data.CustomerDetail;
import com.officialakbarali.fabiz.customer.route.ManageRoute;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;
import com.officialakbarali.fabiz.data.barcode.FabizBarcode;
import com.officialakbarali.fabiz.network.syncInfo.services.SyncService;

import java.util.ArrayList;
import java.util.Calendar;
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

        Button viewTodayButton = findViewById(R.id.cust_today);
        viewTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomer(FabizContract.Customer.COLUMN_DAY + "=?", new String[]{getCurrentDay()});
            }
        });

        Button viewAllButton = findViewById(R.id.cust_all);
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomer(null, null);
            }
        });

        recyclerView = findViewById(R.id.cust_recycler);
        customerAdapter = new CustomerAdapter(this, this, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(customerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBeforeResume();
        showCustomer(FabizContract.Customer.COLUMN_DAY + "=?", new String[]{getCurrentDay()});
    }

    @Override
    public void onClick(CustomerDetail customer) {
        Intent showHome = new Intent(Customer.this, Home.class);
        showHome.putExtra("id", customer.getId() + "");
        startActivity(showHome);
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
        customerAdapter.swapAdapter(customerList);
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

    private String getCurrentDay() {
        return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    private void checkBeforeResume() {
        Context context = Customer.this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(context, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(versionIntent);
        } else {
            String userName = sharedPreferences.getString("my_username", null);
            String password = sharedPreferences.getString("my_password", null);
            if (userName == null || password == null) {
                Intent loginIntent = new Intent(context, LogIn.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(loginIntent);
            } else {
                boolean forcePullActivate = sharedPreferences.getBoolean("force_pull", false);
                if (forcePullActivate) {
                    Intent forcePullIntent = new Intent(context, ForcePull.class);
                    forcePullIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(forcePullIntent);
                } else {
                    boolean updateData = sharedPreferences.getBoolean("update_data", false);
                    if (updateData) {
                        Intent updateDataIntent = new Intent(context, UpdateData.class);
                        updateDataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(updateDataIntent);
                    }else {
                        boolean isServiceRunning = sharedPreferences.getBoolean("service_running", false);
                        if (!isServiceRunning) {
                            new SyncService();
                        }
                    }
                }
            }
        }
    }
}
