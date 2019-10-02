package com.officialakbarali.fabiz.item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.customer.sale.data.Cart;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.item.adapter.ItemAdapter;
import com.officialakbarali.fabiz.item.data.ItemDetail;
import com.officialakbarali.fabiz.requestStock.data.RequestItem;

import java.util.ArrayList;
import java.util.List;

import static com.officialakbarali.fabiz.customer.sale.Sales.cartItems;
import static com.officialakbarali.fabiz.data.CommonInformation.TruncateDecimal;
import static com.officialakbarali.fabiz.requestStock.RequestStock.itemsForRequest;

public class Item extends AppCompatActivity implements ItemAdapter.ItemAdapterOnClickListener {
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    Toast toast;


    private boolean FOR_SALE = false;
    private boolean FOR_ITEM_REQUEST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        FOR_SALE = getIntent().getBooleanExtra("fromSales", false);
        FOR_ITEM_REQUEST = getIntent().getBooleanExtra("fromSalesRequest", false);

        Button searchButton = findViewById(R.id.search_item_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.item_search);
                if (!editText.getText().toString().trim().matches("")) {
                    Spinner filterSpinner = findViewById(R.id.item_filter);
                    String selection = getSelection(String.valueOf(filterSpinner.getSelectedItem()));
                    showItem(selection, new String[]{editText.getText().toString().trim() + "%"});
                } else {
                    showItem(null, null);
                }

            }
        });

        recyclerView = findViewById(R.id.item_recycler);
        itemAdapter = new ItemAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showItem(null, null);
    }

    @Override
    public void onClick(ItemDetail itemDetail) {
        if (FOR_SALE) {
            enterQtyDialogue(itemDetail);
        } else if (FOR_ITEM_REQUEST) {
            dialogueForItemRequest(itemDetail);
        }
    }

    private void dialogueForItemRequest(final ItemDetail itemDetail) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_customer_sale_item_qty);

        final TextView labelText = dialog.findViewById(R.id.cust_sale_add_item_label_pop);
        labelText.setText(itemDetail.getName() + " / " + itemDetail.getBrand() + " / " +
                itemDetail.getCategory());

        //WE DON'T NEED THESE VIEWS
        LinearLayout priceCont = dialog.findViewById(R.id.cust_sale_add_item_cont_price_pop);
        priceCont.setVisibility(View.GONE);
        LinearLayout totalCont = dialog.findViewById(R.id.cust_sale_add_item_cont_total_pop);
        totalCont.setVisibility(View.GONE);
        //*************************


        final EditText quantityText = dialog.findViewById(R.id.cust_sale_add_item_qty);
        quantityText.setText("1");


        Button addItemButton = dialog.findViewById(R.id.cust_sale_add_item_add);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String qtyS = quantityText.getText().toString().trim();

                if (qtyS.matches("") || qtyS.matches("0")) {
                    showToast("Please enter valid QTY");
                } else {
                    itemsForRequest.add(new RequestItem(itemDetail.getName() + " / " + itemDetail.getBrand() + " / " +
                            itemDetail.getCategory(), qtyS));
                    dialog.dismiss();
                    finish();
                }
            }
        });

        Button cancelDialogue = dialog.findViewById(R.id.cust_sale_add_item_cancel);
        cancelDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void enterQtyDialogue(final ItemDetail itemDetail) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_customer_sale_item_qty);

        final TextView labelText = dialog.findViewById(R.id.cust_sale_add_item_label_pop);
        labelText.setText(itemDetail.getName() + " / " + itemDetail.getBrand() + " / " +
                itemDetail.getCategory());

        final EditText priceText = dialog.findViewById(R.id.cust_sale_add_item_price);
        priceText.setText(TruncateDecimal(itemDetail.getPrice() + ""));
        final EditText quantityText = dialog.findViewById(R.id.cust_sale_add_item_qty);
        quantityText.setText("1");
        final TextView totalText = dialog.findViewById(R.id.cust_sale_add_item_total);
        totalText.setText(TruncateDecimal(itemDetail.getPrice() + ""));

        priceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String priceS = priceText.getText().toString().trim();
                String qtyS = quantityText.getText().toString().trim();
                String totS = totalText.getText().toString().trim();

                if (conditionsForDialogue(priceS, qtyS, totS)) {
                    double priceToCart = Double.parseDouble(priceS);
                    int quantityToCart = Integer.parseInt(qtyS);
                    double totalToCart = priceToCart * quantityToCart;
                    totalText.setText(TruncateDecimal(totalToCart + ""));
                }
            }
        });

        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String priceS = priceText.getText().toString().trim();
                String qtyS = quantityText.getText().toString().trim();
                String totS = totalText.getText().toString().trim();

                if (conditionsForDialogue(priceS, qtyS, totS)) {
                    double priceToCart = Double.parseDouble(priceS);
                    int quantityToCart = Integer.parseInt(qtyS);
                    double totalToCart = priceToCart * quantityToCart;
                    totalText.setText(TruncateDecimal(totalToCart + ""));
                }
            }
        });


        Button addItemButton = dialog.findViewById(R.id.cust_sale_add_item_add);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String priceS = priceText.getText().toString().trim();
                String qtyS = quantityText.getText().toString().trim();
                String totS = totalText.getText().toString().trim();
                if (conditionsForDialogue(priceS, qtyS, totS)) {
                    cartItems.add(new Cart(0, 0, itemDetail.getId(), itemDetail.getName(), itemDetail.getBrand(), itemDetail.getCategory(),
                            Double.parseDouble(priceS), Integer.parseInt(qtyS), Double.parseDouble(totS), 0));
                    finish();
                } else {
                    showToast("Please enter valid number");
                }
                dialog.dismiss();
            }
        });

        Button cancelDialogue = dialog.findViewById(R.id.cust_sale_add_item_cancel);
        cancelDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showItem(String selection, String[] selectionArg) {
        FabizProvider provider = new FabizProvider(this, false);
        String[] projection = {FabizContract.Item._ID, FabizContract.Item.COLUMN_NAME, FabizContract.Item.COLUMN_BRAND,
                FabizContract.Item.COLUMN_CATEGORY, FabizContract.Item.COLUMN_PRICE};
        Cursor iCursor = provider.query(FabizContract.Item.TABLE_NAME, projection,
                selection, selectionArg
                , FabizContract.Item.COLUMN_NAME + " ASC");

        List<ItemDetail> itemList = new ArrayList<>();
        while (iCursor.moveToNext()) {
            itemList.add(new ItemDetail(iCursor.getInt(iCursor.getColumnIndexOrThrow(FabizContract.Item._ID)),
                    iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_NAME)),
                    iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_BRAND)),
                    iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_CATEGORY)),
                    Double.parseDouble(iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_PRICE)))
            ));
        }
        itemAdapter.swapAdapter(itemList);
    }

    private String getSelection(String filterFromForm) {
        String caseSelection;

        switch (filterFromForm) {
            case "Id":
                caseSelection = FabizContract.Item._ID;
                break;
            case "Brand":
                caseSelection = FabizContract.Item.COLUMN_BRAND;
                break;
            case "Category":
                caseSelection = FabizContract.Item.COLUMN_CATEGORY;
                break;
            default:
                caseSelection = FabizContract.Item.COLUMN_NAME;
        }

        return caseSelection + " LIKE ?";
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean conditionsForDialogue(String s1, String s2, String s3) {
        if (s1.matches("") || s2.matches("") ||
                s3.matches("")) {
            return false;
        } else {
            try {
                double priceToCart = Double.parseDouble(s1);
                int quantityToCart = Integer.parseInt(s2);
                double totalToCart = Double.parseDouble(s3);

                if (priceToCart > 0 && quantityToCart > 0 && totalToCart > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (Error e) {
                return false;
            }
        }
    }
}
