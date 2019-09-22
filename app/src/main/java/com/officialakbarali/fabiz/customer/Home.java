package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.Sales;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;

public class Home extends AppCompatActivity {
    private int custId;
    private String custName;
    private String custPhone;
    private String custEmail;
    private String custAddress;

    private FabizProvider provider;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        provider = new FabizProvider(this);

        setCustomerDetail();

        setPaymentsDetail();

        Button goToSaleButton = findViewById(R.id.cust_home_sale);
        goToSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saleIntent = new Intent(Home.this, Sales.class);
                saleIntent.putExtra("id",custId + "");
                startActivity(saleIntent);
            }
        });
    }

    private void setCustomerDetail() {
        custId = Integer.parseInt( getIntent().getStringExtra("id"));

        Cursor customerDetailCursor = provider.query(FabizContract.Customer.TABLE_NAME, new String[]{},
                FabizContract.Customer._ID + "=?", new String[]{custId + ""}, null);

        if (customerDetailCursor.moveToNext()) {

            custName = customerDetailCursor.getString(customerDetailCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_NAME));
            custPhone = customerDetailCursor.getString(customerDetailCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_PHONE));
            custEmail = customerDetailCursor.getString(customerDetailCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_EMAIL));
            custAddress = customerDetailCursor.getString(customerDetailCursor.getColumnIndexOrThrow(FabizContract.Customer.COLUMN_ADDRESS));

            TextView idView = findViewById(R.id.cust_home_id);
            idView.setText("Cust Id :" + custId);

            TextView nameView = findViewById(R.id.cust_home_name);
            if (custName.length() > 38) {
                nameView.setText("Name :" + custName.substring(0, 34) + "...");
            } else {
                nameView.setText("Name :" + custName);
            }

            TextView phoneView = findViewById(R.id.cust_home_phone);
            if (custPhone.length() > 19) {
                phoneView.setText("Phone :" + custPhone.substring(0, 15) + "...");
            } else {
                phoneView.setText("Phone :" + custPhone);
            }

            TextView emailView = findViewById(R.id.cust_home_email);
            if (custEmail.length() > 38) {
                emailView.setText("Email :" + custEmail.substring(0, 34) + "...");
            } else {
                emailView.setText("Email :" + custEmail);
            }

            TextView addressView = findViewById(R.id.cust_home_address);
            if (custAddress.length() > 38) {
                addressView.setText("Address :" + custAddress.substring(0, 34) + "...");
            } else {
                addressView.setText("Address :" + custAddress);
            }
        } else {
            showToast("Something went wrong");
            finish();
        }
    }

    private void setPaymentsDetail() {
        TextView custTotal, custPaid, custDue;
        custTotal = findViewById(R.id.cust_home_total);
        custPaid = findViewById(R.id.cust_home_paid);
        custDue = findViewById(R.id.cust_home_due);

        Cursor paymentDetails = provider.query(FabizContract.AccountDetail.TABLE_NAME,
                new String[]{FabizContract.AccountDetail.COLUMN_TOTAL, FabizContract.AccountDetail.COLUMN_PAID, FabizContract.AccountDetail.COLUMN_DUE},
                FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""}, null);
        if (paymentDetails.moveToNext()) {
            custTotal.setText("Total Amount :" + TruncateDecimal(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL))));
            custPaid.setText("Paid :" + TruncateDecimal(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_PAID))));
            custDue.setText("Due Amount :" + TruncateDecimal(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE))));
        }
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }
}