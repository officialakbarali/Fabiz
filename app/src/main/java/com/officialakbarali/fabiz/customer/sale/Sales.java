package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.data.barcode.FabizBarcode;
import com.officialakbarali.fabiz.network.syncInfo.SetupSync;
import com.officialakbarali.fabiz.network.syncInfo.data.SyncLogDetail;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesAdapter;
import com.officialakbarali.fabiz.customer.sale.data.Cart;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;
import com.officialakbarali.fabiz.item.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.GET_DATE_FORMAT_REAL;

import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;
import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToDisplayFormat;
import static com.officialakbarali.fabiz.data.barcode.FabizBarcode.FOR_ITEM;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_INSERT;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_UPDATE;

public class Sales extends AppCompatActivity implements SalesAdapter.SalesAdapterOnClickListener {
    public static List<Cart> cartItems;
    private Toast toast;
    private int custId;

    private TextView dateView, totQtyView, totalView;
    String fromDateTime, currentTime;

    private SalesAdapter salesAdapter;

    int totQtyForSave;
    double totAmountToSave;

    double dueAmtPassed, totalDueAmnt;

    TextView currentDueAmntV, totalDueAmntV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));
        dueAmtPassed = Double.parseDouble(getIntent().getStringExtra("custDueAmt"));

        currentDueAmntV = findViewById(R.id.cust_sale_curr_due);
        currentDueAmntV.setText("Current Due Amount :" + TruncateDecimal(dueAmtPassed + ""));
        totalDueAmntV = findViewById(R.id.cust_sale_tot_due);

        totQtyView = findViewById(R.id.cust_sale_tot_qty);
        totalView = findViewById(R.id.cust_sale_total);

        dateView = findViewById(R.id.cust_sale_time);
        try {
            currentTime = convertDateToDisplayFormat(getCurrentDateTime());
            dateView.setText(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button addItemButton = findViewById(R.id.cust_sale_add_item);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickItem = new Intent(Sales.this, Item.class);
                pickItem.putExtra("fromSales", true);
                startActivity(pickItem);
            }
        });

        Button showBarCoder = findViewById(R.id.cust_sale_barcode);
        showBarCoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanFromBarcodeIntent = new Intent(Sales.this, FabizBarcode.class);
                scanFromBarcodeIntent.putExtra("FOR_WHO", FOR_ITEM + "");
                startActivity(scanFromBarcodeIntent);
            }
        });

        Button saveButton = findViewById(R.id.cust_sale_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItems.isEmpty()) {
                    showToast("Please Add Item");
                } else {
                    if (getEnteredAmnt() <= totalDueAmnt) {
                        saveThisBill();
                    } else {
                        if (totalDueAmnt < 0) {
                            saveThisBill();
                        }
                        showToast("Entered Amount is greater than due amount");
                    }
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.cust_sale_recycler);
        salesAdapter = new SalesAdapter(this, this, false, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
        salesAdapter.swapAdapter(cartItems);
        setTotalAndTotalQuantity();
    }

    @Override
    public void onClick(int indexToBeRemoved, Cart cartItemsF) {
        cartItems.remove(indexToBeRemoved);
        salesAdapter.swapAdapter(cartItems);
        setTotalAndTotalQuantity();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(GET_DATE_FORMAT_REAL());
        Log.i("Time:", sdf.format(new Date()));
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

                                        fromDateTime = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth) + "T";
                                        fromDateTime += String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":30Z";
                                        try {
                                            currentTime = convertDateToDisplayFormat(fromDateTime);
                                            dateView.setText(currentTime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
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

        totalDueAmnt = totAmountToSave + dueAmtPassed;
        totalDueAmntV.setText("Total Due Amount :" + TruncateDecimal(totalDueAmnt + ""));
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

            List<SyncLogDetail> syncLogList = new ArrayList<>();
            syncLogList.add(new SyncLogDetail(billId, FabizContract.BillDetail.TABLE_NAME, OP_INSERT));

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
                        syncLogList.add(new SyncLogDetail(cartInsertId, FabizContract.Cart.TABLE_NAME, OP_INSERT));
                    } else {
                        break;
                    }
                    i++;
                }

                if (i == cartItems.size()) {

                    Cursor amountUpdateCursor = provider.query(FabizContract.AccountDetail.TABLE_NAME,
                            new String[]{FabizContract.AccountDetail._ID, FabizContract.AccountDetail.COLUMN_TOTAL
                                    , FabizContract.AccountDetail.COLUMN_PAID
                                    , FabizContract.AccountDetail.COLUMN_DUE}
                            , FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""}, null);

                    if (amountUpdateCursor.moveToNext()) {
                        double totUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL));
                        double paidUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_PAID));
                        double dueUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE));

                        int thatRowOfAcUp = amountUpdateCursor.getInt(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail._ID));

                        double enteredAmntForUpdate = getEnteredAmnt();

                        totUpdate += totAmountToSave;
                        dueUpdate = (dueUpdate + totAmountToSave) - enteredAmntForUpdate;
                        paidUpdate += enteredAmntForUpdate;

                        ContentValues accUpValues = new ContentValues();
                        accUpValues.put(FabizContract.AccountDetail.COLUMN_TOTAL, totUpdate);
                        accUpValues.put(FabizContract.AccountDetail.COLUMN_PAID, paidUpdate);
                        accUpValues.put(FabizContract.AccountDetail.COLUMN_DUE, dueUpdate);
                        int upAffectedRows = provider.update(FabizContract.AccountDetail.TABLE_NAME, accUpValues,
                                FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""});

                        if (upAffectedRows == 1) {
                            syncLogList.add(new SyncLogDetail(thatRowOfAcUp, FabizContract.AccountDetail.TABLE_NAME, OP_UPDATE));

                            long insertIdPayment = 0;
                            if (enteredAmntForUpdate != 0) {
                                ContentValues logTranscValues = new ContentValues();
                                logTranscValues.put(FabizContract.Payment.COLUMN_CUST_ID, custId);
                                logTranscValues.put(FabizContract.Payment.COLUMN_DATE, currentTime);
                                logTranscValues.put(FabizContract.Payment.COLUMN_AMOUNT, enteredAmntForUpdate);
                                logTranscValues.put(FabizContract.Payment.COLUMN_TOTAL, totUpdate);
                                logTranscValues.put(FabizContract.Payment.COLUMN_PAID, paidUpdate);
                                logTranscValues.put(FabizContract.Payment.COLUMN_DUE, dueUpdate);

                                insertIdPayment = provider.insert(FabizContract.Payment.TABLE_NAME, logTranscValues);
                                if (insertIdPayment > 0) {
                                    syncLogList.add(new SyncLogDetail(insertIdPayment, FabizContract.Payment.TABLE_NAME, OP_INSERT));
                                }
                            } else {
                                insertIdPayment = 1;
                            }
                            if (insertIdPayment > 0) {

                                //DONE**********************************************
                                new SetupSync(this, syncLogList, provider,"Successfully Saved.");
                                showDialogueInfo(totAmountToSave, enteredAmntForUpdate, totUpdate, paidUpdate, dueUpdate);

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

    private void showDialogueInfo(double billAmt, double entAmt, double totAmt, double paidAmt, double dueAmt) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_for_sale_and_payment_success);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });


        Button okayButton = dialog.findViewById(R.id.pop_up_for_payment_okay);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final LinearLayout billAmntContainer = dialog.findViewById(R.id.pop_up_for_payment_bill_amt_cont);
        billAmntContainer.setVisibility(View.VISIBLE);

        final TextView billAmntV = dialog.findViewById(R.id.pop_up_for_payment_bill_amt);
        final TextView dateV = dialog.findViewById(R.id.pop_up_for_payment_date);
        final TextView enteredAmntV = dialog.findViewById(R.id.pop_up_for_payment_ent_amt);
        final TextView totAmntV = dialog.findViewById(R.id.pop_up_for_payment_tot);
        final TextView paidAmtV = dialog.findViewById(R.id.pop_up_for_payment_paid);
        final TextView dueAmtV = dialog.findViewById(R.id.pop_up_for_payment_due);

        billAmntV.setText(": " + TruncateDecimal(billAmt + ""));
        dateV.setText(": " + currentTime);
        enteredAmntV.setText(": " + TruncateDecimal(entAmt + ""));
        totAmntV.setText(": " + TruncateDecimal(totAmt + ""));
        paidAmtV.setText(": " + TruncateDecimal(paidAmt + ""));
        dueAmtV.setText(": " + TruncateDecimal(dueAmt + ""));

        dialog.show();
    }

    private double getEnteredAmnt() {
        EditText amtEditText = findViewById(R.id.cust_sale_amnt);
        if (amtEditText.getText().toString().matches("")) {
            return 0;
        } else {
            try {
                return Double.parseDouble(amtEditText.getText().toString());
            } catch (Error e) {
                return 0;
            }
        }
    }
}
