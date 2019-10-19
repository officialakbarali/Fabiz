package com.officialakbarali.fabiz.customer.sale;

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
import android.widget.Spinner;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesReturnReviewAdapter;
import com.officialakbarali.fabiz.customer.sale.data.SalesReturnReviewItem;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToSearchFormat;

public class SalesReturnReview extends AppCompatActivity {
    private String custId;

    SalesReturnReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_review);

        custId = getIntent().getStringExtra("id");

        RecyclerView recyclerView = findViewById(R.id.sales_return_review_recycler);
        reviewAdapter = new SalesReturnReviewAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reviewAdapter);

        showReturnedItems(null, null);

        Button showCalenderForFilter = findViewById(R.id.sales_return_review_date);
        showCalenderForFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        Button searchButton = findViewById(R.id.sales_return_review_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.sales_return_review_search);
                if (!editText.getText().toString().trim().matches("")) {
                    Spinner filterSpinner = findViewById(R.id.sales_return_review_filter);
                    String selection = getSelection(String.valueOf(filterSpinner.getSelectedItem()));
                    showReturnedItems(selection, new String[]{editText.getText().toString().trim() + "%"});
                } else {
                    showReturnedItems(null, null);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
    }

    private void showReturnedItems(String Fselection, String[] FselectionArg) {


        FabizProvider provider = new FabizProvider(this, false);

        String tableName = FabizContract.SalesReturn.TABLE_NAME + " INNER JOIN " + FabizContract.BillDetail.TABLE_NAME + " ON " + FabizContract.SalesReturn.FULL_COLUMN_BILL_ID
                + " = " + FabizContract.BillDetail.FULL_COLUMN_ID + " INNER JOIN " + FabizContract.Cart.TABLE_NAME + " ON "
                + FabizContract.Cart.FULL_COLUMN_BILL_ID + " = " + FabizContract.BillDetail.FULL_COLUMN_ID;

        String[] projection = {FabizContract.SalesReturn.FULL_COLUMN_ID,
                FabizContract.SalesReturn.FULL_COLUMN_BILL_ID,
                FabizContract.SalesReturn.FULL_COLUMN_DATE
                , FabizContract.Cart.FULL_COLUMN_ITEM_ID,
                FabizContract.Cart.FULL_COLUMN_NAME,
                FabizContract.Cart.FULL_COLUMN_BRAND,
                FabizContract.Cart.FULL_COLUMN_CATAGORY
                , FabizContract.SalesReturn.FULL_COLUMN_PRICE,
                FabizContract.SalesReturn.FULL_COLUMN_QTY,
                FabizContract.SalesReturn.FULL_COLUMN_TOTAL
        };

        String selection = FabizContract.SalesReturn.FULL_COLUMN_ITEM_ID + " = " + FabizContract.Cart.FULL_COLUMN_ITEM_ID + " AND " + FabizContract.BillDetail.FULL_COLUMN_CUST_ID + "=?";

        String[] selectionArg;

        if (Fselection != null) {
            selection += " AND " + Fselection;
            selectionArg = new String[]{custId + "", FselectionArg[0]};
        } else {
            selectionArg = new String[]{custId + ""};
        }

        Cursor returnCursor = provider.queryExplicit(true, tableName, projection, selection, selectionArg, null, null, null, null);

        List<SalesReturnReviewItem> salesReturnReviewItems = new ArrayList<>();
        while (returnCursor.moveToNext()) {
            salesReturnReviewItems.add(new SalesReturnReviewItem(
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.SalesReturn._ID)),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.SalesReturn.COLUMN_BILL_ID)),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.SalesReturn.COLUMN_DATE)),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_ITEM_ID)),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_NAME)),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_BRAND)),
                    returnCursor.getString(returnCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_CATEGORY)),
                    returnCursor.getDouble(returnCursor.getColumnIndexOrThrow(FabizContract.SalesReturn.COLUMN_PRICE)),
                    returnCursor.getInt(returnCursor.getColumnIndexOrThrow(FabizContract.SalesReturn.COLUMN_QTY)),
                    returnCursor.getDouble(returnCursor.getColumnIndexOrThrow(FabizContract.SalesReturn.COLUMN_TOTAL))
            ));
        }
        reviewAdapter.swapAdapter(salesReturnReviewItems);

    }

    private String getSelection(String filterFromForm) {
        String caseSelection;

        switch (filterFromForm) {
            case "Name":
                caseSelection = FabizContract.Cart.FULL_COLUMN_NAME;
                break;
            case "ItemId":
                caseSelection = FabizContract.SalesReturn.FULL_COLUMN_ITEM_ID;
                break;
            case "Brand":
                caseSelection = FabizContract.Cart.FULL_COLUMN_BRAND;
                break;
            case "Category":
                caseSelection = FabizContract.Cart.FULL_COLUMN_CATAGORY;
                break;
            default:
                caseSelection = FabizContract.SalesReturn.COLUMN_BILL_ID;
        }

        return caseSelection + " LIKE ?";
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
                            showReturnedItems(FabizContract.SalesReturn.COLUMN_DATE + " LIKE ?", new String[]{convertDateToSearchFormat(fromDateTime) + "%"});
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
