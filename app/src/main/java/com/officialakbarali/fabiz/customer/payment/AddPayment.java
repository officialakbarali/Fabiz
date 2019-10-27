package com.officialakbarali.fabiz.customer.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.adapter.SalesReviewAdapter;
import com.officialakbarali.fabiz.customer.sale.data.SalesReviewDetail;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;
import com.officialakbarali.fabiz.network.syncInfo.SetupSync;
import com.officialakbarali.fabiz.network.syncInfo.data.SyncLogDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.GET_DATE_FORMAT_REAL;
import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;
import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToDisplayFormat;
import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToSearchFormat;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_CODE_PAY;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_INSERT;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_UPDATE;

public class AddPayment extends AppCompatActivity implements SalesReviewAdapter.SalesReviewAdapterOnClickListener {
    private Toast toast;

    SalesReviewAdapter salesReviewAdapter;
    private String custId;

    private TextView dateV;

    String fromDateTime, currentTime;

    private double dueA;

    SalesReviewDetail mSalesReviewDetail;
    Dialog paymentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        custId = getIntent().getStringExtra("id");

        RecyclerView recyclerView = findViewById(R.id.sales_review_recycler);
        salesReviewAdapter = new SalesReviewAdapter(this, this, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesReviewAdapter);

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
        setPaymentsDetail();
        showBills(null, null);
    }


    private void setPaymentsDetail() {
        FabizProvider providerForFetch = new FabizProvider(this, false);
        TextView custDue = findViewById(R.id.cust_payment_due_total);
        dueA = providerForFetch.getCount(FabizContract.BillDetail.TABLE_NAME, FabizContract.BillDetail.COLUMN_DUE, FabizContract.BillDetail.COLUMN_CUST_ID + "=?",
                new String[]{custId + ""});
        custDue.setText("Total Due Amount :" + TruncateDecimal(dueA + ""));
    }

    private void showBills(String Fselection, String[] FselectionArg) {
        FabizProvider provider = new FabizProvider(this, false);

        String tableName = FabizContract.BillDetail.TABLE_NAME + " INNER JOIN " + FabizContract.Cart.TABLE_NAME
                + " ON " + FabizContract.BillDetail.FULL_COLUMN_ID + " = " + FabizContract.Cart.FULL_COLUMN_BILL_ID;

        String selection = FabizContract.BillDetail.FULL_COLUMN_CUST_ID + "=? AND " +
                FabizContract.BillDetail.FULL_COLUMN_DUE + " NOT LIKE ?";

        String[] selectionArg;

        if (Fselection != null) {
            selection += " AND " + Fselection;
            selectionArg = new String[]{custId + "", "0.0%", FselectionArg[0]};
        } else {
            selectionArg = new String[]{custId + "", "0.0%"};
        }

        Cursor cursorBills = provider.queryExplicit(true,
                tableName,
                new String[]{FabizContract.BillDetail.FULL_COLUMN_ID, FabizContract.BillDetail.FULL_COLUMN_DATE,
                        FabizContract.BillDetail.FULL_COLUMN_QTY, FabizContract.BillDetail.FULL_COLUMN_PRICE,
                        FabizContract.BillDetail.FULL_COLUMN_PAID, FabizContract.BillDetail.FULL_COLUMN_DUE,
                        FabizContract.BillDetail.FULL_COLUMN_RETURNED_TOTAL, FabizContract.BillDetail.FULL_COLUMN_CURRENT_TOTAL},
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
                    cursorBills.getDouble(cursorBills.getColumnIndexOrThrow(FabizContract.BillDetail.COLUMN_CURRENT_TOTAL))
            ));
        }

        salesReviewAdapter.swapAdapter(salesReviewList);
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

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
    }

    @Override
    public void onClick(SalesReviewDetail salesReviewDetail) {
        mSalesReviewDetail = salesReviewDetail;
        showPaymentDialogue();
    }

    private void showPaymentDialogue() {
        paymentDialog = new Dialog(this);
        paymentDialog.setContentView(R.layout.pop_up_payment);

        TextView billDueText = paymentDialog.findViewById(R.id.cust_payment_due);
        billDueText.setText(TruncateDecimal(mSalesReviewDetail.getDue() + ""));

        dateV = paymentDialog.findViewById(R.id.cust_payment_date);
        try {
            currentTime = convertDateToDisplayFormat(getCurrentDateTime());
            dateV.setText(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button changeB = paymentDialog.findViewById(R.id.cust_payment_change);
        changeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        final EditText paidAmountV = paymentDialog.findViewById(R.id.cust_payment_to_pay);

        Button payNowB = paymentDialog.findViewById(R.id.cust_payment_pay_now);
        payNowB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPaymentAccount(paidAmountV.getText().toString());
            }
        });

        paymentDialog.show();
    }

    private void setUpPaymentAccount(String amountToUpdate) {
        if (amountToUpdate.matches("")) {
            showToast("Please enter the amount");
        } else {
            try {
                double enteredAmount = Double.parseDouble(amountToUpdate);

                if (enteredAmount == 0) {
                    showToast("Enter a valid number");
                    return;
                }


                if (dueA <= 0 && enteredAmount <= 0 && enteredAmount < dueA) {
                    showToast("You giving more amount than total credit");
                    return;
                }


                if (enteredAmount > mSalesReviewDetail.getDue()) {
                    if (mSalesReviewDetail.getDue() > 0 || enteredAmount > 0) {
                        showToast("Entered amount is greater than this bill amount");
                        return;
                    }
                }


                if (mSalesReviewDetail.getDue() > enteredAmount && mSalesReviewDetail.getDue() < 0 && enteredAmount < 0) {
                    showToast("You giving more amount than credit");
                    return;
                }


                if (mSalesReviewDetail.getDue() > 0 && enteredAmount < 0) {
                    showToast("You cannot give money back through this bill");
                    return;
                }


                if (enteredAmount > dueA) {
                    if (dueA > 0 || enteredAmount > 0) {
                        showToast("Entered Amount is greater than total due amount");
                        return;
                    }
                }


                if (dueA > enteredAmount && dueA < 0 && enteredAmount < 0) {
                    showToast("You giving more amount than the total credit");
                    return;
                }
                setPaymentToSql(enteredAmount);

            } catch (Error e) {
                showToast("Enter a valid number");
            }
        }
    }

    private void setPaymentToSql(double enteredAmount) {
        FabizProvider provider = new FabizProvider(this, true);


        double paidAmountToUpdate = mSalesReviewDetail.getPaid() + enteredAmount;
        double dueAmountToUpdate = mSalesReviewDetail.getDue() - enteredAmount;

        ContentValues accUpValues = new ContentValues();
        accUpValues.put(FabizContract.BillDetail.COLUMN_PAID, paidAmountToUpdate);
        accUpValues.put(FabizContract.BillDetail.COLUMN_DUE, dueAmountToUpdate);

        //********TRANSACTION STARTED
        provider.createTransaction();

        int upAffectedRows = provider.update(FabizContract.BillDetail.TABLE_NAME, accUpValues,
                FabizContract.BillDetail._ID + "=?", new String[]{mSalesReviewDetail.getId() + ""});


        if (upAffectedRows == 1) {
            List<SyncLogDetail> syncLogList = new ArrayList<>();
            syncLogList.add(new SyncLogDetail(mSalesReviewDetail.getId(), FabizContract.BillDetail.TABLE_NAME, OP_UPDATE));

            String  idToInsertPayment = provider.getIdForInsert(FabizContract.Payment.TABLE_NAME,"");
            if (idToInsertPayment .matches("-1")) {
                provider.finishTransaction();
                showToast("Maximum limit of offline mode reached,please contact customer support");
                return;
            }
            ContentValues logTranscValues = new ContentValues();
            logTranscValues.put(FabizContract.Payment._ID, idToInsertPayment);
            logTranscValues.put(FabizContract.Payment.COLUMN_BILL_ID, mSalesReviewDetail.getId());
            logTranscValues.put(FabizContract.Payment.COLUMN_DATE, currentTime);
            logTranscValues.put(FabizContract.Payment.COLUMN_AMOUNT, enteredAmount);
            long insertIdPayment = provider.insert(FabizContract.Payment.TABLE_NAME, logTranscValues);

            if (insertIdPayment > 0) {
                syncLogList.add(new SyncLogDetail(idToInsertPayment + "", FabizContract.Payment.TABLE_NAME, OP_INSERT));
                new SetupSync(this, syncLogList, provider, "Amount saved successful", OP_CODE_PAY);
                paymentDialog.dismiss();
                showDialogueInfo(enteredAmount, dueAmountToUpdate);
            } else {
                provider.finishTransaction();
                showToast("Failed to save");
            }
        } else {
            provider.finishTransaction();
            showToast("Something went wrong");
        }
    }

    private void showDialogueInfo(double entAmt, double dueAmt) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_for_sale_and_payment_success);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setPaymentsDetail();
                showBills(null, null);
            }
        });

        Button okayButton = dialog.findViewById(R.id.pop_up_for_payment_okay);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final TextView dateV = dialog.findViewById(R.id.pop_up_for_payment_date);
        final TextView enteredAmntV = dialog.findViewById(R.id.pop_up_for_payment_ent_amt);
        final TextView dueAmtV = dialog.findViewById(R.id.pop_up_for_payment_due);

        TextView dueLabelText = dialog.findViewById(R.id.pop_up_for_payment_due_label);
        dueLabelText.setText("Bill Due Amount");


        dateV.setText(": " + currentTime);
        enteredAmntV.setText(": " + TruncateDecimal(entAmt + ""));

        dueAmtV.setText(": " + TruncateDecimal(dueAmt + ""));
        dialog.show();
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
                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddPayment.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        //*************************

                                        fromDateTime = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth) + "T";
                                        fromDateTime += String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":30Z";
                                        try {
                                            currentTime = convertDateToDisplayFormat(fromDateTime);
                                            dateV.setText(currentTime);
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

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(GET_DATE_FORMAT_REAL());
        Log.i("Time:", sdf.format(new Date()));
        return sdf.format(new Date());
    }


    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }
}
