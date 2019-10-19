package com.officialakbarali.fabiz.requestStock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;

import android.widget.Toast;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;


import com.officialakbarali.fabiz.requestStock.adapter.PickItemAdapter;
import com.officialakbarali.fabiz.requestStock.data.PickItemData;

import java.util.ArrayList;
import java.util.List;

import static com.officialakbarali.fabiz.requestStock.RequestStock.itemsForRequest;

public class RequestItem extends AppCompatActivity implements PickItemAdapter.ItemAdapterOnClickListener {
    Toast toast;

    RecyclerView recyclerView;
    PickItemAdapter itemAdapter;

    List<PickItemData> fullItem, itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_item);

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
        itemAdapter = new PickItemAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
        showItem(null, null);
    }

    @Override
    public void onClick(PickItemData itemDetail, int index) {
        if (index == -1) {
            String itemNameToUpdate = itemDetail.getName() + " / " + itemDetail.getBrand() + " / " +
                    itemDetail.getCategory();
            for (int i = 0; i < itemsForRequest.size(); i++) {
                com.officialakbarali.fabiz.requestStock.data.RequestItem checkItem = itemsForRequest.get(i);
                if (checkItem.getName().matches(itemNameToUpdate)) {
                    itemsForRequest.remove(i);
                    showToast("Removed Item:" + itemNameToUpdate);
                    break;
                }
            }

        } else {
            itemList.set(index, itemDetail);
            insertThisItem(itemDetail);
        }
    }

    private void insertThisItem(final PickItemData itemDetail) {
        String itemNameToUpdate = itemDetail.getName() + " / " + itemDetail.getBrand() + " / " +
                itemDetail.getCategory();
        boolean alreadyExist = false;
        for (int i = 0; i < itemsForRequest.size(); i++) {
            com.officialakbarali.fabiz.requestStock.data.RequestItem checkItem = itemsForRequest.get(i);
            if (checkItem.getName().matches(itemNameToUpdate)) {
                checkItem.setQty(itemDetail.getQty() + "");
                itemsForRequest.set(i, checkItem);
                showToast("Updated Item :" + itemNameToUpdate + ", QTY :" + itemDetail.getQty());
                alreadyExist = true;
                break;
            }
        }
        if (!alreadyExist) {
            itemsForRequest.add(new com.officialakbarali.fabiz.requestStock.data.RequestItem(itemNameToUpdate, itemDetail.getQty() + ""));
            showToast("Added Item :" + itemNameToUpdate + ", QTY :" + itemDetail.getQty());
        }
    }

    private void showItem(String selection, String[] selectionArg) {
        FabizProvider provider = new FabizProvider(this, false);
        String[] projection = {FabizContract.Item._ID, FabizContract.Item.COLUMN_NAME, FabizContract.Item.COLUMN_BRAND,
                FabizContract.Item.COLUMN_CATEGORY, FabizContract.Item.COLUMN_PRICE};
        Cursor iCursor = provider.query(FabizContract.Item.TABLE_NAME, projection,
                selection, selectionArg
                , FabizContract.Item.COLUMN_NAME + " ASC");

        itemList = new ArrayList<>();
        while (iCursor.moveToNext()) {
            itemList.add(new PickItemData(iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item._ID)),
                    iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_NAME)),
                    iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_BRAND)),
                    iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_CATEGORY)),
                    Double.parseDouble(iCursor.getString(iCursor.getColumnIndexOrThrow(FabizContract.Item.COLUMN_PRICE)))
            ));
        }

        if (fullItem == null) {
            setPreviousQty();
            fullItem = itemList;
        } else {
            makeQty();
        }

        itemAdapter.swapAdapter(itemList);
    }

    private void makeQty() {

        for (int j = 0; j < itemList.size(); j++) {
            PickItemData currentItem = itemList.get(j);
            for (int i = 0; i < fullItem.size(); i++) {
                PickItemData fullCheckItem = fullItem.get(i);
                if (fullCheckItem.getId() == currentItem.getId()) {
                    currentItem.setQty(fullCheckItem.getQty());
                    break;
                }
            }
        }
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

    private void setPreviousQty() {
        for (int i = 0; i < itemList.size(); i++) {
            PickItemData itemDetail = itemList.get(i);
            String itemNameToUpdate = itemDetail.getName() + " / " + itemDetail.getBrand() + " / " +
                    itemDetail.getCategory();
            for (int j = 0; j < itemsForRequest.size(); j++) {
                com.officialakbarali.fabiz.requestStock.data.RequestItem checkItem = itemsForRequest.get(j);
                if (checkItem.getName().matches(itemNameToUpdate)) {
                    itemDetail.setQty(Integer.parseInt(checkItem.getQty()));
                    itemList.set(i, itemDetail);
                    break;
                }
            }
        }
    }
}
