package com.officialakbarali.fabiz.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.network.syncInfo.SetupSync;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;
import com.officialakbarali.fabiz.network.syncInfo.data.SyncLogDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.officialakbarali.fabiz.data.CommonInformation.GET_PHONE_NUMBER_LENGTH;
import static com.officialakbarali.fabiz.data.CommonInformation.getNumberFromDayName;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_CODE_ADD_CUSTOMER;
import static com.officialakbarali.fabiz.network.syncInfo.SetupSync.OP_INSERT;


public class AddCustomer extends AppCompatActivity {
    EditText nameE, phoneE, emailE, addresssE, crE, shopNameE;
    private Toast toast;
    FabizProvider fabizProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        nameE = findViewById(R.id.cust_add_name);
        phoneE = findViewById(R.id.cust_add_phone);
        emailE = findViewById(R.id.cust_add_email);
        addresssE = findViewById(R.id.cust_add_address);
        crE = findViewById(R.id.cust_add_cr);
        shopNameE = findViewById(R.id.cust_add_shop_name);

        fabizProvider = new FabizProvider(this, true);

        final Button saveCustomer = findViewById(R.id.cust_add_save);
        saveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameE.getText().toString().toUpperCase().trim();
                String phone = phoneE.getText().toString().trim();
                String email = emailE.getText().toString().trim();
                String address = addresssE.getText().toString().toUpperCase().trim();
                String crNumber = crE.getText().toString().toUpperCase().trim();
                String shopName = shopNameE.getText().toString().toUpperCase().trim();

                EditText telephoneE = findViewById(R.id.cust_add_telephone);
                String telephone = telephoneE.getText().toString().trim();

                EditText vatNoE = findViewById(R.id.cust_add_vat_no);
                String vatNo = vatNoE.getText().toString().trim();

                Spinner filterSpinner = findViewById(R.id.cust_add_day_list);
                String selectedDay = "" + getNumberFromDayName(String.valueOf(filterSpinner.getSelectedItem()));

                ContentValues values = new ContentValues();

                int idOfCuustomerToInsert = fabizProvider.getIdForInsert(FabizContract.Customer.TABLE_NAME);


                values.put(FabizContract.Customer._ID, idOfCuustomerToInsert);
                values.put(FabizContract.Customer.COLUMN_BARCODE, idOfCuustomerToInsert + "");
                values.put(FabizContract.Customer.COLUMN_DAY, selectedDay);//String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
                values.put(FabizContract.Customer.COLUMN_NAME, name);
                values.put(FabizContract.Customer.COLUMN_PHONE, phone);

                if (crNumber.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_CR_NO, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_CR_NO, crNumber);
                }

                if (shopName.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_SHOP_NAME, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_SHOP_NAME, shopName);
                }

                if (email.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_EMAIL, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_EMAIL, email);
                }

                if (address.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_ADDRESS, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_ADDRESS, address);
                }

                if (telephone.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_TELEPHONE, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_TELEPHONE, telephone);
                }

                if (vatNo.matches("")) {
                    values.put(FabizContract.Customer.COLUMN_VAT_NO, "NA");
                } else {
                    values.put(FabizContract.Customer.COLUMN_VAT_NO, vatNo);
                }


                if (validateCustomerFields(values)) {
                    saveCustomer(values);
                }
            }
        });
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean validateName(String name) {
        Pattern pattern = Pattern.compile("[^A-Za-z0-9 ._-]");
        Matcher matcher = pattern.matcher(name);
        return name.length() > 0 && !matcher.find();
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("[^0-9+ ]");
        Matcher matcher = pattern.matcher(phoneNumber);
        return phoneNumber.length() >= GET_PHONE_NUMBER_LENGTH() && phoneNumber.length() <= 25 && !matcher.find();
    }

    private boolean validateEmail(String email) {
        if (email.matches("NA")) return false;
        return email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
    }


    private boolean validateVatNumber(String passedString) {
        if (passedString.matches("NA")) return true;
        Pattern pattern = Pattern.compile("[^A-Za-z0-9 ._-]");
        Matcher matcher = pattern.matcher(passedString);
        return !matcher.find();
    }

    private boolean validateCommonInformation(String passedString) {
        if (passedString.matches("NA")) return false;
        Pattern pattern = Pattern.compile("[^A-Za-z0-9 ._-]");
        Matcher matcher = pattern.matcher(passedString);
        return !matcher.find();
    }


    private boolean validateCustomerFields(ContentValues values) {
        if (validateName(values.getAsString(FabizContract.Customer.COLUMN_NAME))) {
            if (validatePhoneNumber(values.getAsString(FabizContract.Customer.COLUMN_PHONE))) {
                if (validateEmail(values.getAsString(FabizContract.Customer.COLUMN_EMAIL))) {
                    if (validateCommonInformation(values.getAsString(FabizContract.Customer.COLUMN_ADDRESS))) {
                        if (validateCommonInformation(values.getAsString(FabizContract.Customer.COLUMN_SHOP_NAME))) {
                            if (validateCommonInformation(values.getAsString(FabizContract.Customer.COLUMN_CR_NO))) {
                                if (validateVatNumber(values.getAsString(FabizContract.Customer.COLUMN_VAT_NO))) {
                                    if (validatePhoneNumber(values.getAsString(FabizContract.Customer.COLUMN_TELEPHONE))) {
                                        return true;
                                    } else {
                                        showToast("Please enter valid Telephone");
                                    }
                                } else {
                                    showToast("Please enter valid Vat Number");
                                }
                            } else {
                                showToast("Please enter valid CR Number");
                            }
                        } else {
                            showToast("Please enter valid Shop Name");
                        }
                    } else {
                        showToast("Please enter valid Address");
                    }
                } else {
                    showToast("Please enter valid Email Address");
                }
            } else {
                showToast("Please enter valid Phone Number");
            }
        } else {
            showToast("Please enter valid Name");
        }
        return false;
    }

    private void saveCustomer(ContentValues values) {
        try {
            //********TRANSACTION STARTED
            fabizProvider.createTransaction();
            long idOfCustomer = fabizProvider.insert(FabizContract.Customer.TABLE_NAME, values);

            if (idOfCustomer > 0) {
                List<SyncLogDetail> syncLogList = new ArrayList<>();
                syncLogList.add(new SyncLogDetail(values.get(FabizContract.Customer._ID) + "", FabizContract.Customer.TABLE_NAME, OP_INSERT));
                new SetupSync(this, syncLogList, fabizProvider, "Successfully Saved. Id:" + values.get(FabizContract.Customer._ID), OP_CODE_ADD_CUSTOMER);
                finish();
            } else {
                fabizProvider.finishTransaction();
                showToast("Failed to Save");
            }
        } catch (Error e) {
            fabizProvider.finishTransaction();
            showToast("Failed to Save");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
    }
}
