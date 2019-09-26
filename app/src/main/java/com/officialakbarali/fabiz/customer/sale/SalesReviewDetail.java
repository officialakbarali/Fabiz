package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesAdapter;
import com.officialakbarali.fabiz.customer.sale.data.Cart;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;

import java.util.ArrayList;
import java.util.List;

public class SalesReviewDetail extends AppCompatActivity implements SalesAdapter.SalesAdapterOnClickListener {
    private int custId, billId;

    private TextView dateView, totQtyView, totalView, billIdView;
    FabizProvider provider;

    private SalesAdapter salesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_review_detail);

        provider = new FabizProvider(this, false);

        billIdView = findViewById(R.id.cust_sale_billId);
        totQtyView = findViewById(R.id.cust_sale_tot_qty);
        totalView = findViewById(R.id.cust_sale_total);
        dateView = findViewById(R.id.cust_sale_time);

        custId = Integer.parseInt(getIntent().getStringExtra("custId"));
        billId = Integer.parseInt(getIntent().getStringExtra("billId"));

        RecyclerView recyclerView = findViewById(R.id.cust_sale_recycler);
        salesAdapter = new SalesAdapter(this, this, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesAdapter);

        setUpBillDetail();
        setUpBillItems();
    }

    private void setUpBillDetail() {

        Cursor billCursor = provider.query(FabizContract.BillDetail.TABLE_NAME,
                new String[]{FabizContract.BillDetail._ID, FabizContract.BillDetail.COLUMN_QTY,
                        FabizContract.BillDetail.COLUMN_DATE, FabizContract.BillDetail.COLUMN_PRICE}
                , FabizContract.BillDetail._ID + "=?", new String[]{billId + ""}
                , null);

        if (billCursor.moveToNext()) {
            billIdView.setText("Billid :" + billId);
            dateView.setText("Time :"+billCursor.getString(billCursor.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_DATE)));
            totQtyView.setText("Total Item :" + billCursor.getInt(billCursor.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_QTY)));
            totalView.setText("Total :" + billCursor.getDouble(billCursor.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_PRICE)));
        }
    }

    private void setUpBillItems() {
        List<Cart> cartItems = new ArrayList<>();

        Cursor billItemsCursor = provider.query(FabizContract.Cart.TABLE_NAME, new String[]{
                        FabizContract.Cart._ID, FabizContract.Cart.COLUMN_BILL_ID, FabizContract.Cart.COLUMN_ITEM_ID, FabizContract.Cart.COLUMN_NAME, FabizContract.Cart.COLUMN_BRAND, FabizContract.Cart.COLUMN_CATEGORY,
                        FabizContract.Cart.COLUMN_PRICE, FabizContract.Cart.COLUMN_QTY, FabizContract.Cart.COLUMN_TOTAL, FabizContract.Cart.COLUMN_RETURN_QTY
                }, FabizContract.Cart.COLUMN_BILL_ID + "=?",
                new String[]{billId + ""}, null);

        while (billItemsCursor.moveToNext()) {
            cartItems.add(new Cart(
                    billItemsCursor.getInt(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart._ID)),
                    billItemsCursor.getInt(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_BILL_ID)),
                    billItemsCursor.getInt(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_ITEM_ID)),
                    billItemsCursor.getString(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_NAME)),
                    billItemsCursor.getString(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_BRAND)),
                    billItemsCursor.getString(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_CATEGORY)),
                    billItemsCursor.getDouble(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_PRICE)),
                    billItemsCursor.getInt(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_QTY)),
                    billItemsCursor.getDouble(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_TOTAL)),
                    billItemsCursor.getInt(billItemsCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_RETURN_QTY))
            ));
        }
        salesAdapter.swapAdapter(cartItems);
    }

    @Override
    public void onClick(int indexToBeRemoved) {

    }
}
