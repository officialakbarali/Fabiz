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
import com.officialakbarali.fabiz.customer.payment.AddPayment;
import com.officialakbarali.fabiz.customer.payment.PaymentReview;
import com.officialakbarali.fabiz.customer.sale.Sales;
import com.officialakbarali.fabiz.customer.sale.SalesReturnReview;
import com.officialakbarali.fabiz.customer.sale.SalesReview;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.ArrayList;

import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;

public class Home extends AppCompatActivity {
    private int custId;
    private String custName;
    private String custPhone;
    private String custEmail;
    private String custAddress;

    private FabizProvider provider;
    private Toast toast;

    private double custDueAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        provider = new FabizProvider(this, false);

        setCustomerDetail();

        Button goToSaleButton = findViewById(R.id.cust_home_sale);
        goToSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saleIntent = new Intent(Home.this, Sales.class);
                saleIntent.putExtra("id", custId + "");
                saleIntent.putExtra("custDueAmt", custDueAmt + "");
                Sales.cartItems = new ArrayList<>();
                startActivity(saleIntent);
            }
        });

        Button salesReview = findViewById(R.id.cust_home_sale_review);
        salesReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salesReviewIntent = new Intent(Home.this, SalesReview.class);
                salesReviewIntent.putExtra("id", custId + "");
                startActivity(salesReviewIntent);
            }
        });

        final Button salesReturnButton = findViewById(R.id.cust_home_sales_return);
        salesReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salesReturnIntent = new Intent(Home.this, SalesReview.class);
                salesReturnIntent.putExtra("fromSalesReturn", true);
                salesReturnIntent.putExtra("id", custId + "");

                startActivity(salesReturnIntent);
            }
        });

        Button saleReturnReviewButton = findViewById(R.id.cust_home_sales_return_review);
        saleReturnReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saleReturnReviewIntent = new Intent(Home.this, SalesReturnReview.class);
                saleReturnReviewIntent.putExtra("id", custId + "");
                startActivity(saleReturnReviewIntent);
            }
        });


        Button payDueButton = findViewById(R.id.cust_home_sales_paydue);
        payDueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payIntent = new Intent(Home.this, AddPayment.class);
                payIntent.putExtra("id", custId + "");
                startActivity(payIntent);
            }
        });

        Button paymentReviewButton = findViewById(R.id.cust_home_sales_payment_review);
        paymentReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paymentReviewIntent = new Intent(Home.this, PaymentReview.class);
                paymentReviewIntent.putExtra("id", custId + "");
                startActivity(paymentReviewIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setPaymentsDetail();
    }

    private void setCustomerDetail() {

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
            custDueAmt = Double.parseDouble(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE)));
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
