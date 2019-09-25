package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesReviewAdapter;
import com.officialakbarali.fabiz.customer.sale.data.SalesReviewDetail;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.ArrayList;
import java.util.List;

public class SalesReview extends AppCompatActivity implements SalesReviewAdapter.SalesReviewAdapterOnClickListener {
    SalesReviewAdapter salesReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_review);

        RecyclerView recyclerView = findViewById(R.id.sales_review_recycler);
        salesReviewAdapter = new SalesReviewAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesReviewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBills();
    }

    @Override
    public void onClick(int idOfBill) {

    }

    private void showBills() {
        FabizProvider provider = new FabizProvider(this, false);
        Cursor cursorBills = provider.query(FabizContract.BillDetail.TABLE_NAME,
                new String[]{FabizContract.BillDetail._ID, FabizContract.BillDetail.COLUMN_DATE, FabizContract.BillDetail.COLUMN_QTY, FabizContract.BillDetail.COLUMN_PRICE},
                null, null, null);

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
