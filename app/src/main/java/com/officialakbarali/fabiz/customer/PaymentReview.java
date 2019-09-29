package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.adapter.PaymentReviewAdapter;
import com.officialakbarali.fabiz.customer.data.PaymentReviewDetail;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.ArrayList;
import java.util.List;

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

        showAllPayments();
    }

    private void showAllPayments() {
        List<PaymentReviewDetail> details = new ArrayList<>();
        FabizProvider provider = new FabizProvider(this, false);
        Cursor paymentDetailCursor = provider.query(FabizContract.Payment.TABLE_NAME,
                new String[]{
                        FabizContract.Payment._ID, FabizContract.Payment.COLUMN_DATE, FabizContract.Payment.COLUMN_AMOUNT,
                        FabizContract.Payment.COLUMN_TOTAL, FabizContract.Payment.COLUMN_PAID, FabizContract.Payment.COLUMN_DUE
                },
                FabizContract.Payment.COLUMN_CUST_ID + "=?",
                new String[]{custId + ""},
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
}
