package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesReviewAdapter;
import com.officialakbarali.fabiz.customer.sale.data.SalesReviewDetail;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.ArrayList;
import java.util.List;

public class SalesReview extends AppCompatActivity implements SalesReviewAdapter.SalesReviewAdapterOnClickListener {
    SalesReviewAdapter salesReviewAdapter;
    private int custId;

    private boolean FROM_SALERS_RETURN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_review);

        FROM_SALERS_RETURN = getIntent().getBooleanExtra("fromSalesReturn", false);
        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        RecyclerView recyclerView = findViewById(R.id.sales_review_recycler);
        salesReviewAdapter = new SalesReviewAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesReviewAdapter);

        if (FROM_SALERS_RETURN) setUpThisPageForReturn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBills();
    }

    @Override
    public void onClick(int idOfBill) {
        Intent salesDetaiiilIntent = new Intent(SalesReview.this, com.officialakbarali.fabiz.customer.sale.SalesReviewDetail.class);
        salesDetaiiilIntent.putExtra("custId", custId + "");
        salesDetaiiilIntent.putExtra("billId", idOfBill + "");

        if(FROM_SALERS_RETURN){
            salesDetaiiilIntent.putExtra("fromSalesReturn", true);
        }else {
            salesDetaiiilIntent.putExtra("fromSalesReturn", false);
        }


        startActivity(salesDetaiiilIntent);
    }

    private void setUpThisPageForReturn() {
        TextView textHead, quoteText;
        textHead = findViewById(R.id.sales_review_head);
        textHead.setText("Sales Return");
        quoteText = findViewById(R.id.sales_review_quote);
        quoteText.setVisibility(View.VISIBLE);
    }

    private void showBills() {
        FabizProvider provider = new FabizProvider(this, false);
        Cursor cursorBills = provider.query(FabizContract.BillDetail.TABLE_NAME,
                new String[]{FabizContract.BillDetail._ID, FabizContract.BillDetail.COLUMN_DATE, FabizContract.BillDetail.COLUMN_QTY, FabizContract.BillDetail.COLUMN_PRICE},
                FabizContract.BillDetail.COLUMN_CUST_ID + "=?", new String[]{custId + ""}, null);

        List<SalesReviewDetail> salesReviewList = new ArrayList<>();
        while (cursorBills.moveToNext()) {
            salesReviewList.add(new SalesReviewDetail(cursorBills.getInt(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail._ID)),
                    cursorBills.getString(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_DATE)),
                    cursorBills.getInt(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_QTY)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_PRICE))
            ));
        }

        salesReviewAdapter.swapAdapter(salesReviewList);
    }
}
