package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.officialakbarali.fabiz.network.SyncInfo.SetupSync;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.network.SyncInfo.data.SyncLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.officialakbarali.fabiz.data.CommonInformation.GET_PHONE_NUMBER_LENGTH;


public class AddCustomer extends AppCompatActivity {
    EditText nameE, phoneE, emailE, addresssE;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        nameE = findViewById(R.id.cust_add_name);
        phoneE = findViewById(R.id.cust_add_phone);
        emailE = findViewById(R.id.cust_add_email);
        addresssE = findViewById(R.id.cust_add_address);

        final Button saveCustomer = findViewById(R.id.cust_add_save);
        saveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameE.getText().toString().toUpperCase().trim();
                String phone = phoneE.getText().toString().trim();
                String email = emailE.getText().toString().trim();
                String address = addresssE.getText().toString().toUpperCase().trim();

                ContentValues values = new ContentValues();
                values.put(FabizContract.Customer.COLUMN_NAME, name);
                values.put(FabizContract.Customer.COLUMN_PHONE, phone);

                if (email.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_EMAIL, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_EMAIL, email);
                }

                if (address.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_ADDRESS, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_ADDRESS, address);
                }


                if (validateCustomerFields(values)) {
                    saveCustomer(values);
                }
            }
        });
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean validateName(String name) {
        Pattern pattern = Pattern.compile("[^A-Za-z0-9 ._-]");
        Matcher matcher = pattern.matcher(name);
        return name.length() > 0 && !matcher.find();
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("[^0-9+ ]");
        Matcher matcher = pattern.matcher(phoneNumber);
        return phoneNumber.length() >= GET_PHONE_NUMBER_LENGTH() && !matcher.find();
    }

    private boolean validateEmail(String email) {
        if (email.matches("NA")) return true;
        return email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
    }

    private boolean validateAddress(String address) {
        if (address.matches("NA")) return true;
        Pattern pattern = Pattern.compile("[^A-Za-z0-9 ._-]");
        Matcher matcher = pattern.matcher(address);
        return !matcher.find();
    }


    private boolean validateCustomerFields(ContentValues values) {
        if (validateName(values.getAsString(FabizContract.Customer.COLUMN_NAME))) {
            if (validatePhoneNumber(values.getAsString(FabizContract.Customer.COLUMN_PHONE))) {
                if (validateEmail(values.getAsString(FabizContract.Customer.COLUMN_EMAIL))) {
                    if (validateAddress(values.getAsString(FabizContract.Customer.COLUMN_ADDRESS))) {
                        return true;
                    } else {
                        showToast("Please enter valid Address");
                    }
                } else {
                    showToast("Please enter valid Email Address");
                }
            } else {
                showToast("Please enter valid Phone Number");
            }
        } else {
            showToast("Please enter valid Name");
        }
        return false;
    }

    private void saveCustomer(ContentValues values) {

        FabizProvider fabizProvider = new FabizProvider(this, true);
        try {
            //********TRANSACTION STARTED
            fabizProvider.createTransaction();
            long idOfCustomer = fabizProvider.insert(FabizContract.Customer.TABLE_NAME, values);

            if (idOfCustomer > 0) {
                ContentValues accountInitialsValues = new ContentValues();
                accountInitialsValues.put(FabizContract.AccountDetail.COLUMN_CUSTOMER_ID, idOfCustomer);
                accountInitialsValues.put(FabizContract.AccountDetail.COLUMN_TOTAL, 0);
                accountInitialsValues.put(FabizContract.AccountDetail.COLUMN_PAID, 0);
                accountInitialsValues.put(FabizContract.AccountDetail.COLUMN_DUE, 0);
                long idOfAccount = fabizProvider.insert(FabizContract.AccountDetail.TABLE_NAME, accountInitialsValues);
                if (idOfAccount > 0) {
                    List<SyncLog> syncLogList = new ArrayList<>();
                    syncLogList.add(new SyncLog(idOfCustomer, FabizContract.Customer.TABLE_NAME, "INSERT"));
                    syncLogList.add(new SyncLog(idOfAccount, FabizContract.AccountDetail.TABLE_NAME, "INSERT"));
                    new SetupSync(this, syncLogList, fabizProvider);
                    showToast("Successfully Saved. Id:" + idOfCustomer);
                    finish();
                } else {
                    fabizProvider.finishTransaction();
                    showToast("Failed to Save");
                }
            } else {
                fabizProvider.finishTransaction();
                showToast("Failed to Save");
            }
        } catch (Error e) {
            fabizProvider.finishTransaction();
            showToast("Failed to Save");
        }
    }
}
