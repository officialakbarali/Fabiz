package com.officialakbarali.fabiz.item;

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

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.data.FabizContract;
import com.officialakbarali.fabiz.data.FabizProvider;
import com.officialakbarali.fabiz.item.adapter.ItemAdapter;
import com.officialakbarali.fabiz.item.data.ItemDetail;

import java.util.ArrayList;
import java.util.List;

public class Item extends AppCompatActivity implements ItemAdapter.ItemAdapterOnClickListener {
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

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
    public void onClick(String mItemCurrentRaw, String mItemSelectedName) {
        Toast.makeText(Item.this, "Item Name:" + mItemSelectedName, Toast.LENGTH_SHORT).show();
    }

    private void showItem(String selection, String[] selectionArg) {
        FabizProvider provider = new FabizProvider(this);
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
            case "Name":
                caseSelection = FabizContract.Item.COLUMN_NAME;
                break;
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
}
