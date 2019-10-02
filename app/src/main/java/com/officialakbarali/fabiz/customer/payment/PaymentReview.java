package com.officialakbarali.fabiz.customer.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.adapter.PaymentReviewAdapter;
import com.officialakbarali.fabiz.customer.data.PaymentReviewDetail;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToSearchFormat;

public class PaymentReview extends AppCompatActivity {

    int custId;
    PaymentReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_review);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        RecyclerView recyclerView = findViewById(R.id.payment_review_recycler);
        adapter = new PaymentReviewAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Button showCalenderForFilter = findViewById(R.id.payment_review_date_filter);
        showCalenderForFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        Button searchButton = findViewById(R.id.payment_review_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fromEditText = findViewById(R.id.payment_review_search);
                String searchString = fromEditText.getText().toString();
                if (searchString.matches("")) {
                    showPayments(null, null);
                    return;
                }
                showPayments(FabizContract.Payment._ID + "=?", new String[]{searchString});
            }
        });

        showPayments(null, null);
    }

    private void showPayments(String Fselection, String[] FselectionArg) {
        List<PaymentReviewDetail> details = new ArrayList<>();
        FabizProvider provider = new FabizProvider(this, false);

        String selection = FabizContract.Payment.COLUMN_CUST_ID + "=?";

        String[] selectionArg;
        if (Fselection != null) {
            selection += " AND " + Fselection;
            selectionArg = new String[]{custId + "", FselectionArg[0]};
        } else {
            selectionArg = new String[]{custId + ""};
        }

        Cursor paymentDetailCursor = provider.query(FabizContract.Payment.TABLE_NAME,
                new String[]{
                        FabizContract.Payment._ID, FabizContract.Payment.COLUMN_DATE, FabizContract.Payment.COLUMN_AMOUNT,
                        FabizContract.Payment.COLUMN_TOTAL, FabizContract.Payment.COLUMN_PAID, FabizContract.Payment.COLUMN_DUE
                },
                selection,
                selectionArg,
                null);
        while (paymentDetailCursor.moveToNext()) {
            details.add(new PaymentReviewDetail(
                    paymentDetailCursor.getInt(paymentDetailCursor.getColumnIndexOrThrow(FabizContract.Payment._ID)),
                    paymentDetailCursor.getString(paymentDetailCursor.getColumnIndexOrThrow(FabizContract.Payment.COLUMN_DATE)),
                    paymentDetailCursor.getDouble(paymentDetailCursor.getColumnIndexOrThrow(FabizContract.Payment.COLUMN_AMOUNT)),
                    paymentDetailCursor.getDouble(paymentDetailCursor.getColumnIndexOrThrow(FabizContract.Payment.COLUMN_TOTAL)),
                    paymentDetailCursor.getDouble(paymentDetailCursor.getColumnIndexOrThrow(FabizContract.Payment.COLUMN_PAID)),
                    paymentDetailCursor.getDouble(paymentDetailCursor.getColumnIndexOrThrow(FabizContract.Payment.COLUMN_DUE))
            ));
        }
        adapter.swapAdapter(details);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        String fromDateTime = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth) + "T";

                        try {
                            showPayments(FabizContract.Payment.COLUMN_DATE + " LIKE ?", new String[]{convertDateToSearchFormat(fromDateTime) + "%"});
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
