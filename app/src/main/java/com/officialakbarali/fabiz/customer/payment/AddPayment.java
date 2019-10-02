package com.officialakbarali.fabiz.customer.payment;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.network.syncInfo.SetupSync;
import com.officialakbarali.fabiz.network.syncInfo.data.SyncLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.officialakbarali.fabiz.data.CommonInformation.GET_DATE_FORMAT_REAL;
import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;
import static com.officialakbarali.fabiz.data.CommonInformation.convertDateToDisplayFormat;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_UPDATE;

public class AddPayment extends AppCompatActivity {
    private Toast toast;

    private int custId;

    private TextView dateV;

    String fromDateTime, currentTime;

    private double totalA, paidA, dueA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        custId = Integer.parseInt(getIntent().getStringExtra("id"));

        dateV = findViewById(R.id.cust_payment_date);
        try {
            currentTime = convertDateToDisplayFormat(getCurrentDateTime());
            dateV.setText(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Button changeB = findViewById(R.id.cust_payment_change);
        changeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        final EditText paidAmountV = findViewById(R.id.cust_payment_to_pay);

        Button payNowB = findViewById(R.id.cust_payment_pay_now);
        payNowB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPaymentAccount(paidAmountV.getText().toString());
            }
        });

        setPaymentsDetail();

    }

    private void setUpPaymentAccount(String amountToUpdate) {
        if (amountToUpdate.matches("")) {
            showToast("Please enter the amount");
        } else {
            try {
                double enteredAmount = Double.parseDouble(amountToUpdate);
                if (enteredAmount <= dueA) {
                    if (enteredAmount != 0) {
                        //SUCCESS
                        setPaymentToSql(enteredAmount);
                    } else {
                        showToast("Enter a valid amount");
                    }
                } else {
                    if (dueA < 0) {
                        if (enteredAmount != 0) {
                            setPaymentToSql(enteredAmount);
                        }
                    }
                    showToast("Entered amount is greater than Due amount");
                }

            } catch (Error e) {
                showToast("Enter a valid number");
            }
        }


    }

    private void setPaymentToSql(double enteredAmount) {
        FabizProvider provider = new FabizProvider(this, true);
        Cursor amountUpdateCursor = provider.query(FabizContract.AccountDetail.TABLE_NAME,
                new String[]{FabizContract.AccountDetail._ID, FabizContract.AccountDetail.COLUMN_TOTAL,
                        FabizContract.AccountDetail.COLUMN_PAID, FabizContract.AccountDetail.COLUMN_DUE}
                , FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""}, null);

        if (amountUpdateCursor.moveToNext()) {
            double totUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL));
            double paidUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_PAID));
            double dueUpdate = amountUpdateCursor.getDouble(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE));

            int thatRowOfAcUp = amountUpdateCursor.getInt(amountUpdateCursor.getColumnIndexOrThrow(FabizContract.AccountDetail._ID));

            paidUpdate += enteredAmount;
            dueUpdate -= enteredAmount;

            ContentValues accUpValues = new ContentValues();
            accUpValues.put(FabizContract.AccountDetail.COLUMN_PAID, paidUpdate);
            accUpValues.put(FabizContract.AccountDetail.COLUMN_DUE, dueUpdate);

            //********TRANSACTION STARTED
            provider.createTransaction();

            int upAffectedRows = provider.update(FabizContract.AccountDetail.TABLE_NAME, accUpValues,
                    FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""});

            if (upAffectedRows == 1) {
                List<SyncLog> syncLogList = new ArrayList<>();
                syncLogList.add(new SyncLog(thatRowOfAcUp, FabizContract.AccountDetail.TABLE_NAME, OP_UPDATE));


                ContentValues logTranscValues = new ContentValues();
                logTranscValues.put(FabizContract.Payment.COLUMN_CUST_ID, custId);
                logTranscValues.put(FabizContract.Payment.COLUMN_DATE, currentTime);
                logTranscValues.put(FabizContract.Payment.COLUMN_AMOUNT, enteredAmount);
                logTranscValues.put(FabizContract.Payment.COLUMN_TOTAL, totUpdate);
                logTranscValues.put(FabizContract.Payment.COLUMN_PAID, paidUpdate);
                logTranscValues.put(FabizContract.Payment.COLUMN_DUE, dueUpdate);

                long insertIdPayment = provider.insert(FabizContract.Payment.TABLE_NAME, logTranscValues);

                if (insertIdPayment > 0) {
                    new SetupSync(this, syncLogList, provider,"Amount saved successful");
                    showDialogueInfo(enteredAmount, totUpdate, paidUpdate, dueUpdate);
                } else {
                    provider.finishTransaction();
                    showToast("Failed to save");
                }
            } else {
                provider.finishTransaction();
                showToast("Something went wrong");
            }
        } else {
            showToast("Something went wrong");
        }
    }

    private void showDialogueInfo(double entAmt, double totAmt, double paidAmt, double dueAmt) {
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

        final TextView dateV = dialog.findViewById(R.id.pop_up_for_payment_date);
        final TextView enteredAmntV = dialog.findViewById(R.id.pop_up_for_payment_ent_amt);
        final TextView totAmntV = dialog.findViewById(R.id.pop_up_for_payment_tot);
        final TextView paidAmtV = dialog.findViewById(R.id.pop_up_for_payment_paid);
        final TextView dueAmtV = dialog.findViewById(R.id.pop_up_for_payment_due);

        dateV.setText(": " + currentTime);
        enteredAmntV.setText(": " + TruncateDecimal(entAmt + ""));
        totAmntV.setText(": " + TruncateDecimal(totAmt + ""));
        paidAmtV.setText(": " + TruncateDecimal(paidAmt + ""));
        dueAmtV.setText(": " + TruncateDecimal(dueAmt + ""));
        dialog.show();
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

    private void setPaymentsDetail() {
        FabizProvider providerForFetch = new FabizProvider(this, false);
        TextView custTotal, custPaid, custDue;
        custTotal = findViewById(R.id.cust_payment_total);
        custPaid = findViewById(R.id.cust_payment_paid);
        custDue = findViewById(R.id.cust_payment_due);

        Cursor paymentDetails = providerForFetch.query(FabizContract.AccountDetail.TABLE_NAME,
                new String[]{FabizContract.AccountDetail.COLUMN_TOTAL, FabizContract.AccountDetail.COLUMN_PAID, FabizContract.AccountDetail.COLUMN_DUE},
                FabizContract.AccountDetail.COLUMN_CUSTOMER_ID + "=?", new String[]{custId + ""}, null);
        if (paymentDetails.moveToNext()) {
            custTotal.setText(TruncateDecimal(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL))));
            totalA = paymentDetails.getDouble(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_TOTAL));
            custPaid.setText(TruncateDecimal(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_PAID))));
            paidA = paymentDetails.getDouble(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_PAID));
            custDue.setText(TruncateDecimal(paymentDetails.getString(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE))));
            dueA = paymentDetails.getDouble(paymentDetails.getColumnIndexOrThrow(FabizContract.AccountDetail.COLUMN_DUE));
        }
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }
}
