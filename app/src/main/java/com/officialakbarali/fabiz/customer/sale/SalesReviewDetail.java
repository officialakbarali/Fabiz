package com.officialakbarali.fabiz.customer.sale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesAdapter;
import com.officialakbarali.fabiz.customer.sale.data.Cart;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.network.SyncInfo.SetupSync;
import com.officialakbarali.fabiz.network.SyncInfo.data.SyncLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.GET_DATE_FORMAT_REAL;
import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;
import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToDisplayFormat;
import static com.officialakbarali.fabiz.network.SyncInfo.SetupSync.OP_INSERT;
import static com.officialakbarali.fabiz.network.SyncInfo.SetupSync.OP_UPDATE;

public class SalesReviewDetail extends AppCompatActivity implements SalesAdapter.SalesAdapterOnClickListener {
    private Toast toast;
    private int custId, billId;

    private TextView dateView, totQtyView, totalView, billIdView;
    FabizProvider fabizProvider;

    private SalesAdapter salesAdapter;

    private boolean FROM_SALES_RETURN = false;


    //IF DUE BECOME NEGATIVE AFTER RETURN AN ITEM
    private boolean NEGATIVE_DUE = false;

    //***************************FOR DIALOGUE***************************
    private String DcurrentTime, DfromDateTime;
    private TextView DdateTextP;

    //******************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_review_detail);

        FROM_SALES_RETURN = getIntent().getBooleanExtra("fromSalesReturn", false);

        fabizProvider = new FabizProvider(this, false);

        billIdView = findViewById(R.id.cust_sale_billId);
        totQtyView = findViewById(R.id.cust_sale_tot_qty);
        totalView = findViewById(R.id.cust_sale_total);
        dateView = findViewById(R.id.cust_sale_time);

        custId = Integer.parseInt(getIntent().getStringExtra("custId"));
        billId = Integer.parseInt(getIntent().getStringExtra("billId"));

        RecyclerView recyclerView = findViewById(R.id.cust_sale_recycler);


        if (FROM_SALES_RETURN) {
            salesAdapter = new SalesAdapter(this, this, true, true);
        } else {
            salesAdapter = new SalesAdapter(this, this, true, false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesAdapter);

        setUpBillDetail();
        setUpBillItems();
        if (FROM_SALES_RETURN) setUpThisPageForReturn();
    }


    private void setUpThisPageForReturn() {
        //TODO IF ANYTHING NEED TO BE CHANGE IN FUTURE FOR SALES RETURN
    }

    private void setUpBillDetail() {

        Cursor billCursor = fabizProvider.query(FabizContract.BillDetail.TABLE_NAME,
                new String[]{FabizContract.BillDetail._ID, FabizContract.BillDetail.COLUMN_QTY,
                        FabizContract.BillDetail.COLUMN_DATE, FabizContract.BillDetail.COLUMN_PRICE}
                , FabizContract.BillDetail._ID + "=?", new String[]{billId + ""}
                , null);

        if (billCursor.moveToNext()) {
            billIdView.setText("Billid :" + billId);

            dateView.setText("Time :" + billCursor.getString(billCursor.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_DATE)));

            totQtyView.setText("Total Item :" + billCursor.getInt(billCursor.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_QTY)));
            totalView.setText("Total :" + billCursor.getDouble(billCursor.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_PRICE)));
        }
    }

    private void setUpBillItems() {
        List<Cart> cartItems = new ArrayList<>();

        Cursor billItemsCursor = fabizProvider.query(FabizContract.Cart.TABLE_NAME, new String[]{
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
    public void onClick(int indexToBeRemoved, Cart cartITemList) {
        if (FROM_SALES_RETURN) {
            setUpReturnPop(cartITemList);
        }
    }

    private void setUpReturnPop(final Cart cartITemList) {

        final TextView nameTextP, maxQtyP, totAmountP;
        final EditText priceTextP, qtyTextP;
        final Button returnB, cancelB, changeDateP;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_sales_return_item);

        nameTextP = dialog.findViewById(R.id.sales_return_pop_item_name);
        maxQtyP = dialog.findViewById(R.id.sales_return_pop_max);

        priceTextP = dialog.findViewById(R.id.sales_return_pop_price);
        qtyTextP = dialog.findViewById(R.id.sales_return_pop_qty);
        totAmountP = dialog.findViewById(R.id.sales_return_pop_total);

        returnB = dialog.findViewById(R.id.sales_return_pop_remove);
        cancelB = dialog.findViewById(R.id.sales_return_pop_cancel);

        DdateTextP = dialog.findViewById(R.id.sales_return_pop_date);
        changeDateP = dialog.findViewById(R.id.sales_return_pop_date_change);

        nameTextP.setText(cartITemList.getName());

        final int maxLimitOfReturn = cartITemList.getQty() - cartITemList.getReturnQty();
        maxQtyP.setText("Enter QTY to Return\n(Maximum " + maxLimitOfReturn + ")");

        priceTextP.setText(cartITemList.getPrice() + "");

        qtyTextP.setText("1");

        totAmountP.setText(TruncateDecimal("" + cartITemList.getPrice()));


        qtyTextP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String priceS = priceTextP.getText().toString().trim();
                String qtyS = qtyTextP.getText().toString().trim();
                String totS = totAmountP.getText().toString().trim();

                if (conditionsForDialogue(priceS, qtyS, totS, maxLimitOfReturn)) {
                    double priceToCart = Double.parseDouble(priceS);
                    int quantityToCart = Integer.parseInt(qtyS);
                    double totalToCart = priceToCart * quantityToCart;
                    totAmountP.setText(TruncateDecimal(totalToCart + ""));
                }
            }
        });

        priceTextP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String priceS = priceTextP.getText().toString().trim();
                String qtyS = qtyTextP.getText().toString().trim();
                String totS = totAmountP.getText().toString().trim();

                if (conditionsForDialogue(priceS, qtyS, totS, maxLimitOfReturn)) {
                    double priceToCart = Double.parseDouble(priceS);
                    int quantityToCart = Integer.parseInt(qtyS);
                    double totalToCart = priceToCart * quantityToCart;
                    totAmountP.setText(TruncateDecimal(totalToCart + ""));
                }
            }
        });

//*****************************setting up date************************************
        try {
            DcurrentTime = convertDateToDisplayFormat(getCurrentDateTime());
            DdateTextP.setText(DcurrentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        changeDateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
//********************************************************************************

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        returnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = validateAndReturnContentValues(DcurrentTime, cartITemList.getItemId(), qtyTextP.getText().toString(), priceTextP.getText().toString(),
                        totAmountP.getText().toString(), maxLimitOfReturn);
                if (values != null) {
                    saveThisReturnedItem(values);
                }
            }
        });

        if (maxLimitOfReturn < 1) {
            showToast("This is completely Returned");
        } else {
            dialog.show();
        }
    }

    private ContentValues validateAndReturnContentValues(String dateR, int itemIdR, String qtyR, String priceR, String totalR, int maxLimitOfReturn) {
        if (conditionsForDialogue(priceR, qtyR, totalR, maxLimitOfReturn)) {
            ContentValues values = new ContentValues();
            values.put(FabizContract.SalesReturn.COLUMN_DATE, dateR);
            values.put(FabizContract.SalesReturn.COLUMN_BILL_ID, billId);
            values.put(FabizContract.SalesReturn.COLUMN_ITEM_ID, itemIdR);
            values.put(FabizContract.SalesReturn.COLUMN_QTY, qtyR);
            values.put(FabizContract.SalesReturn.COLUMN_PRICE, priceR);
            values.put(FabizContract.SalesReturn.COLUMN_TOTAL, totalR);

            return values;
        }
        return null;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(GET_DATE_FORMAT_REAL());
        Log.i("Time:", sdf.format(new Date()));
        return sdf.format(new Date());
    }

    private void showDateTimePicker() {
        DfromDateTime = "";
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(SalesReviewDetail.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        //*************************

                                        DfromDateTime = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth) + "T";
                                        DfromDateTime += String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":30Z";
                                        try {
                                            DcurrentTime = convertDateToDisplayFormat(DfromDateTime);
                                            DdateTextP.setText(DcurrentTime);
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

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean conditionsForDialogue(String s1, String s2, String s3, int maxLimitOfReturn) {
        if (s1.matches("") || s2.matches("") ||
                s3.matches("")) {
            showToast("Some fields are empty");
            return false;
        } else {
            try {
                double priceToCart = Double.parseDouble(s1);
                int quantityToCart = Integer.parseInt(s2);
                double totalToCart = Double.parseDouble(s3);


                if (quantityToCart > maxLimitOfReturn) {
                    showToast("Only " + maxLimitOfReturn + " Items Left");
                    return false;
                }

                if (priceToCart > 0 && quantityToCart > 0 && totalToCart > 0) {
                    return true;
                } else {
                    showToast("Invalid Number");
                    return false;
                }
            } catch (Error e) {
                showToast("Invalid Number");
                return false;
            }
        }
    }

    private void saveThisReturnedItem(ContentValues values) {
        NEGATIVE_DUE = false;
        List<SyncLog> syncLogList = new ArrayList<>();
        FabizProvider saveProvider = new FabizProvider(this, true);
        try {
            //********TRANSACTION STARTED
            saveProvider.createTransaction();
            long idOfSalesReturn = saveProvider.insert(FabizContract.SalesReturn.TABLE_NAME, values);

            if (idOfSalesReturn > 0) {

                syncLogList.add(new SyncLog(idOfSalesReturn, FabizContract.SalesReturn.TABLE_NAME, OP_INSERT));

                Cursor amountUpdateCursor = saveProvider.query(FabizContract.AccountDetail.TABLE_NAME,
                        new String[]{FabizContract.AccountDetail._ID, FabizContract.AccountDetail.COLUMN_TOTAL, FabizContract.AccountDetail.COLUMN_DUE}
                        , FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""}, null);

                if (amountUpdateCursor.moveToNext()) {

                    int thatRowOfAcUp = amountUpdateCursor.getInt(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail._ID));
                    double totUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL));
                    double dueUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE));

                    totUpdate -= values.getAsDouble(FabizContract.SalesReturn.COLUMN_TOTAL);
                    dueUpdate -= values.getAsDouble(FabizContract.SalesReturn.COLUMN_TOTAL);

                    if (dueUpdate < 0) {
                        NEGATIVE_DUE = true;
                    }


                    ContentValues accUpValues = new ContentValues();
                    accUpValues.put(FabizContract.AccountDetail.COLUMN_TOTAL, totUpdate);
                    accUpValues.put(FabizContract.AccountDetail.COLUMN_DUE, dueUpdate);
                    int upAffectedRows = saveProvider.update(FabizContract.AccountDetail.TABLE_NAME, accUpValues,
                            FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""});

                    if (upAffectedRows == 1) {

                        syncLogList.add(new SyncLog(thatRowOfAcUp, FabizContract.AccountDetail.TABLE_NAME, OP_UPDATE));

                        Cursor returnUpdateToBillCursor = saveProvider.query(FabizContract.Cart.TABLE_NAME,
                                new String[]{FabizContract.Cart._ID, FabizContract.Cart.COLUMN_RETURN_QTY}
                                , FabizContract.Cart.COLUMN_BILL_ID + "=? AND " + FabizContract.Cart.COLUMN_ITEM_ID + "=?",
                                new String[]{billId + "", values.getAsInteger(FabizContract.SalesReturn.COLUMN_ITEM_ID) + ""}, null);

                        if (returnUpdateToBillCursor.moveToNext()) {

                            int retQtyUpdate = returnUpdateToBillCursor.getInt(
                                    returnUpdateToBillCursor.getColumnIndexOrThrow(FabizContract.Cart.COLUMN_RETURN_QTY));

                            int idOfRowReturn = returnUpdateToBillCursor.getInt(
                                    returnUpdateToBillCursor.getColumnIndexOrThrow(FabizContract.Cart._ID));

                            retQtyUpdate += values.getAsInteger(FabizContract.SalesReturn.COLUMN_QTY);

                            ContentValues billReturnUpValues = new ContentValues();
                            billReturnUpValues.put(FabizContract.Cart.COLUMN_RETURN_QTY, retQtyUpdate);

                            int upReturnAffectedRaw = saveProvider.update(FabizContract.Cart.TABLE_NAME, billReturnUpValues,
                                    FabizContract.Cart._ID + "=?",
                                    new String[]{idOfRowReturn + ""});

                            if (upReturnAffectedRaw > 0) {
                                syncLogList.add(new SyncLog(idOfRowReturn, FabizContract.Cart.TABLE_NAME, OP_UPDATE));
                                new SetupSync(this, syncLogList, saveProvider);
                                showToast("Successfully Returned");
                                finish();
                            } else {
                                saveProvider.finishTransaction();
                                showToast("Something went wrong");
                                Log.e("SalesReview:", "Failed To Update Return");
                            }

                        } else {
                            saveProvider.finishTransaction();
                            showToast("Something went wrong");
                            Log.e("SalesReview:", "Cart Item Not Found");
                        }


                    } else {
                        saveProvider.finishTransaction();
                        showToast("Something went wrong");
                        Log.e("SalesReview:", "Due Failed To Update");
                    }

                } else {
                    saveProvider.finishTransaction();
                    showToast("Something went wrong");
                    Log.e("SalesReview:", "Amount Update Cursor Zero");
                }

            } else {
                saveProvider.finishTransaction();
                showToast("Failed to save");
            }
        } catch (Error error) {
            saveProvider.finishTransaction();
            showToast("Failed to save");
        }

    }

}
