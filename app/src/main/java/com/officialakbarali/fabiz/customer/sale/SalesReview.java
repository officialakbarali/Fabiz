package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesReviewAdapter;
import com.officialakbarali.fabiz.customer.sale.data.SalesReviewDetail;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToSearchFormat;

public class SalesReview extends AppCompatActivity implements SalesReviewAdapter.SalesReviewAdapterOnClickListener {
    SalesReviewAdapter salesReviewAdapter;
    private String custId;

    private boolean FROM_SALERS_RETURN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_review);

        FROM_SALERS_RETURN = getIntent().getBooleanExtra("fromSalesReturn", false);
        custId = getIntent().getStringExtra("id");

        RecyclerView recyclerView = findViewById(R.id.sales_review_recycler);

        salesReviewAdapter = new SalesReviewAdapter(this, this, false);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesReviewAdapter);

        if (FROM_SALERS_RETURN) setUpThisPageForReturn();

        Button showCalenderForFilter = findViewById(R.id.sales_review_date_filter_button);
        showCalenderForFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        Button searchButton = findViewById(R.id.sales_review_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.sales_review_search);
                if (!editText.getText().toString().trim().matches("")) {
                    Spinner filterSpinner = findViewById(R.id.sales_review_spinner);
                    String selection = getSelection(String.valueOf(filterSpinner.getSelectedItem()));
                    showBills(selection, new String[]{editText.getText().toString().trim() + "%"});
                } else {
                    showBills(null, null);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
        showBills(null, null);
    }

    @Override
    public void onClick(SalesReviewDetail salesReviewDetail) {
        String idOfBill = salesReviewDetail.getId();
        Intent salesDetaiiilIntent = new Intent(SalesReview.this, com.officialakbarali.fabiz.customer.sale.SalesReviewDetail.class);
        salesDetaiiilIntent.putExtra("custId", custId + "");
        salesDetaiiilIntent.putExtra("billId", idOfBill + "");

        if (FROM_SALERS_RETURN) {
            salesDetaiiilIntent.putExtra("fromSalesReturn", true);
        } else {
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

    private void showBills(String Fselection, String[] FselectionArg) {
        FabizProvider provider = new FabizProvider(this, false);

        String tableName = FabizContract.BillDetail.TABLE_NAME + " INNER JOIN " + FabizContract.Cart.TABLE_NAME
                + " ON " + FabizContract.BillDetail.FULL_COLUMN_ID + " = " + FabizContract.Cart.FULL_COLUMN_BILL_ID;

        String selection = FabizContract.BillDetail.FULL_COLUMN_CUST_ID + "=?";

        String[] selectionArg;

        if (Fselection != null) {
            selection += " AND " + Fselection;
            selectionArg = new String[]{custId + "", FselectionArg[0]};
        } else {
            selectionArg = new String[]{custId + ""};
        }

        Cursor cursorBills = provider.queryExplicit(
                true, tableName,
                new String[]{FabizContract.BillDetail.FULL_COLUMN_ID, FabizContract.BillDetail.FULL_COLUMN_DATE,
                        FabizContract.BillDetail.FULL_COLUMN_QTY, FabizContract.BillDetail.FULL_COLUMN_PRICE,
                        FabizContract.BillDetail.FULL_COLUMN_PAID, FabizContract.BillDetail.FULL_COLUMN_DUE,
                        FabizContract.BillDetail.FULL_COLUMN_RETURNED_TOTAL, FabizContract.BillDetail.FULL_COLUMN_CURRENT_TOTAL
                        , FabizContract.BillDetail.FULL_COLUMN_DISCOUNT},
                selection, selectionArg, null, null, FabizContract.BillDetail.FULL_COLUMN_ID + " DESC", null);

        List<SalesReviewDetail> salesReviewList = new ArrayList<>();

        while (cursorBills.moveToNext()) {
            salesReviewList.add(new SalesReviewDetail(cursorBills.getString(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail._ID)),
                    cursorBills.getString(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_DATE)),
                    cursorBills.getInt(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_QTY)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_PRICE)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_PAID)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_DUE)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_RETURNED_TOTAL)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_CURRENT_TOTAL)),
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_DISCOUNT))
            ));
        }
        salesReviewAdapter.swapAdapter(salesReviewList);
    }

    private String getSelection(String filterFromForm) {
        String caseSelection;

        switch (filterFromForm) {
            case "Name":
                caseSelection = FabizContract.Cart.FULL_COLUMN_NAME;
                break;
            case "ItemId":
                caseSelection = FabizContract.Cart.FULL_COLUMN_ITEM_ID;
                break;
            case "Brand":
                caseSelection = FabizContract.Cart.FULL_COLUMN_BRAND;
                break;
            case "Category":
                caseSelection = FabizContract.Cart.FULL_COLUMN_CATAGORY;
                break;
            default:
                caseSelection = FabizContract.BillDetail.FULL_COLUMN_ID;
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
                            showBills(FabizContract.BillDetail.COLUMN_DATE + " LIKE ?", new String[]{convertDateToSearchFormat(fromDateTime) + "%"});
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
