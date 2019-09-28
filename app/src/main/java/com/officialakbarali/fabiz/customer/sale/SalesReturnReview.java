package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesReturnReviewAdapter;
import com.officialakbarali.fabiz.customer.sale.data.SalesReturnReviewItem;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.ArrayList;
import java.util.List;

public class SalesReturnReview extends AppCompatActivity {
    private int custId;

    SalesReturnReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_review);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        RecyclerView recyclerView = findViewById(R.id.sales_return_review_recycler);
        reviewAdapter = new SalesReturnReviewAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reviewAdapter);

        showAllReturnedItems();
    }

    private void showAllReturnedItems() {
        FabizProvider provider = new FabizProvider(this, false);

        String tableName = FabizContract.BillDetail.TABLE_NAME + " INNER JOIN " + FabizContract.SalesReturn.TABLE_NAME + " ON " + FabizContract.BillDetail.FULL_COLUMN_ID
                + " = " + FabizContract.SalesReturn.FULL_COLUMN_BILL_ID + " INNER JOIN " + FabizContract.Item.TABLE_NAME + " ON "
                + FabizContract.Item.FULL_COLUMN_ID + " = " + FabizContract.SalesReturn.FULL_COLUMN_ITEM_ID;

        String[] projection = {FabizContract.SalesReturn.FULL_COLUMN_ID + " AS " + "a",
                FabizContract.SalesReturn.FULL_COLUMN_BILL_ID + " AS " + "b",
                FabizContract.SalesReturn.FULL_COLUMN_DATE + " AS " + "c"
                , FabizContract.Item.FULL_COLUMN_ID + " AS " + "d",
                FabizContract.Item.FULL_COLUMN_NAME + " AS " + "e",
                FabizContract.Item.FULL_COLUMN_BRAND + " AS " + "f",
                FabizContract.Item.FULL_COLUMN_CATAGORY + " AS " + "g"
                , FabizContract.SalesReturn.FULL_COLUMN_PRICE + " AS " + "h",
                FabizContract.SalesReturn.FULL_COLUMN_QTY + " AS " + "i",
                FabizContract.SalesReturn.FULL_COLUMN_TOTAL + " AS " + "j"
        };

        String selection = FabizContract.BillDetail.FULL_COLUMN_CUST_ID + "=?";
        String[] selectionArg = {custId + ""};

        Cursor returnCursor = provider.query(tableName, projection, selection, selectionArg, null);

        List<SalesReturnReviewItem> salesReturnReviewItems = new ArrayList<>();
        while (returnCursor.moveToNext()) {
            salesReturnReviewItems.add(new SalesReturnReviewItem(
                    returnCursor.getInt(returnCursor.getColumnIndexOrThrow("a")),
                    returnCursor.getInt(returnCursor.getColumnIndexOrThrow("b")),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow("c")),
                    returnCursor.getInt(returnCursor.getColumnIndexOrThrow("d")),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow("e")),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow("f")),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow("g")),
                    returnCursor.getDouble(returnCursor.getColumnIndexOrThrow("h")),
                    returnCursor.getInt(returnCursor.getColumnIndexOrThrow("i")),
                    returnCursor.getDouble(returnCursor.getColumnIndexOrThrow("j"))
            ));
        }
        reviewAdapter.swapAdapter(salesReturnReviewItems);

    }
}
