package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.officialakbarali.fabiz.network.SyncInfo.SetupSync;
import com.officialakbarali.fabiz.network.SyncInfo.data.SyncLog;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesAdapter;
import com.officialakbarali.fabiz.customer.sale.data.Cart;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.item.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;

public class Sales extends AppCompatActivity implements SalesAdapter.SalesAdapterOnClickListener {
    public static List<Cart> cartItems;
    private Toast toast;
    private int custId;

    private TextView dateView, totQtyView, totalView;
    String fromDateTime, currentTime;

    private SalesAdapter salesAdapter;

    int totQtyForSave;
    double totAmountToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        totQtyView = findViewById(R.id.cust_sale_tot_qty);
        totalView = findViewById(R.id.cust_sale_total);

        dateView = findViewById(R.id.cust_sale_time);
        dateView.setText(getCurrentDateTime());
        currentTime = getCurrentDateTime().trim();

        Button addItemButton = findViewById(R.id.cust_sale_add_item);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickItem = new Intent(Sales.this, Item.class);
                pickItem.putExtra("fromSales", true);
                startActivity(pickItem);
            }
        });

        Button showTimePicker = findViewById(R.id.cust_sale_show_time);
        showTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        Button saveButton = findViewById(R.id.cust_sale_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItems.isEmpty()) {
                    showToast("Please Add Item");
                } else {
                    saveThisBill();
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.cust_sale_recycler);
        salesAdapter = new SalesAdapter(this, this, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        salesAdapter.swapAdapter(cartItems);
        setTotalAndTotalQuantity();
    }

    @Override
    public void onClick(int indexToBeRemoved) {
        cartItems.remove(indexToBeRemoved);
        salesAdapter.swapAdapter(cartItems);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private void showDateTimePicker() {
        fromDateTime = "";
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(Sales.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        //*************************
                                        fromDateTime = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth) + " ";
                                        fromDateTime += String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":30";
                                        dateView.setText(fromDateTime);
                                        currentTime = fromDateTime.trim();
                                        //*************************
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void setTotalAndTotalQuantity() {
        totAmountToSave = 0;
        totQtyForSave = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            Cart cart = cartItems.get(i);
            totAmountToSave += cart.getTotal();
            totQtyForSave += cart.getQty();
        }
        totQtyView.setText("Total Item :" + TruncateDecimal(totQtyForSave + ""));
        totalView.setText("Total :" + TruncateDecimal(totAmountToSave + ""));
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }


    private void saveThisBill() {
        FabizProvider provider = new FabizProvider(this, true);

        ContentValues billValues = new ContentValues();
        billValues.put(FabizContract.BillDetail.COLUMN_CUST_ID, custId);
        billValues.put(FabizContract.BillDetail.COLUMN_DATE, currentTime);
        billValues.put(FabizContract.BillDetail.COLUMN_QTY, totQtyForSave);
        billValues.put(FabizContract.BillDetail.COLUMN_PRICE, totAmountToSave);

        try {
            //********TRANSACTION STARTED
            provider.createTransaction();
            long billId = provider.insert(FabizContract.BillDetail.TABLE_NAME, billValues);

            List<SyncLog> syncLogList = new ArrayList<>();
            syncLogList.add(new SyncLog(billId, FabizContract.BillDetail.TABLE_NAME, "INSERT"));

            if (billId > 0) {

                int i = 0;
                while (i < cartItems.size()) {
                    ContentValues cartItemsValues = new ContentValues();

                    Cart cartI = cartItems.get(i);

                    cartItemsValues.put(FabizContract.Cart.COLUMN_BILL_ID, billId);
                    cartItemsValues.put(FabizContract.Cart.COLUMN_ITEM_ID, cartI.getItemId());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_NAME, cartI.getName());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_BRAND, cartI.getBrand());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_CATEGORY, cartI.getCategory());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_PRICE, cartI.getPrice());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_QTY, cartI.getQty());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_TOTAL, cartI.getTotal());
                    cartItemsValues.put(FabizContract.Cart.COLUMN_RETURN_QTY, cartI.getReturnQty());

                    long cartInsertId = provider.insert(FabizContract.Cart.TABLE_NAME, cartItemsValues);

                    if (cartInsertId > 0) {
                        syncLogList.add(new SyncLog(cartInsertId, FabizContract.Cart.TABLE_NAME, "INSERT"));
                    } else {
                        break;
                    }
                    i++;
                }

                if (i == cartItems.size()) {

                    Cursor amountUpdateCursor = provider.query(FabizContract.AccountDetail.TABLE_NAME,
                            new String[]{FabizContract.AccountDetail.COLUMN_TOTAL, FabizContract.AccountDetail.COLUMN_DUE}
                            , FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""}, null);

                    if (amountUpdateCursor.moveToNext()) {
                        double totUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL));
                        double dueUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE));

                        totUpdate += totAmountToSave;
                        dueUpdate += totAmountToSave;

                        ContentValues accUpValues = new ContentValues();
                        accUpValues.put(FabizContract.AccountDetail.COLUMN_TOTAL, totUpdate);
                        accUpValues.put(FabizContract.AccountDetail.COLUMN_DUE, dueUpdate);
                        int upAffectedRows = provider.update(FabizContract.AccountDetail.TABLE_NAME, accUpValues,
                                FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""});

                        if (upAffectedRows == 1) {
                            syncLogList.add(new SyncLog(custId, FabizContract.AccountDetail.TABLE_NAME, "UPDATE"));
                            new SetupSync(this, syncLogList, provider);
                            showToast("Successfully Saved. Id:" + billId);
                            finish();
                        } else {
                            provider.finishTransaction();
                            showToast("Something went wrong");
                        }
                    } else {
                        provider.finishTransaction();
                        showToast("Something went wrong");
                    }


                } else {
                    provider.finishTransaction();
                    showToast("Failed to save");
                }

            } else {
                provider.finishTransaction();
                showToast("Failed to save");
            }

        } catch (Error error) {
            provider.finishTransaction();
            showToast("Failed to save");
        }
    }

}
