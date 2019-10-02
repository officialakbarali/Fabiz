package com.officialakbarali.fabiz.requestStock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.officialakbarali.fabiz.R;
import com.officialakbarali.fabiz.item.Item;
import com.officialakbarali.fabiz.requestStock.adapter.RequestStockAdapter;
import com.officialakbarali.fabiz.requestStock.data.RequestItem;

import java.util.List;

public class RequestStock extends AppCompatActivity implements RequestStockAdapter.RequestStockOnClickListener {
    public static List<RequestItem> itemsForRequest;
    private Toast toast;

    private RequestStockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_stock);

        RecyclerView recyclerView = findViewById(R.id.request_stock_recycler);
        adapter = new RequestStockAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        Button enterItemButton = findViewById(R.id.request_stock_enter_item);
        enterItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterItemDialogue();
            }
        });

        Button pickItemButton = findViewById(R.id.request_stock_pick_list);
        pickItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickItemIntent = new Intent(RequestStock.this, Item.class);
                pickItemIntent.putExtra("fromSalesRequest", true);
                startActivity(pickItemIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapAdapter(itemsForRequest);
    }

    @Override
    public void onClick(int indexToRemove) {
        itemsForRequest.remove(indexToRemove);
        adapter.swapAdapter(itemsForRequest);
    }

    private void showToast() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Some fields are empty", Toast.LENGTH_LONG);
        toast.show();
    }

    private void enterItemDialogue() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_enter_item_request);


        final EditText quantityText = dialog.findViewById(R.id.pop_up_item_request_qty);
        quantityText.setText("1");
        final EditText nameText = dialog.findViewById(R.id.pop_up_item_request_name);
        nameText.setText("");

        Button addButton = dialog.findViewById(R.id.pop_up_item_request_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameS = nameText.getText().toString().trim();
                String qtyS = quantityText.getText().toString().trim();

                if (nameS.matches("") || qtyS.matches("")) {
                    showToast();
                } else {
                    itemsForRequest.add(new RequestItem(nameS, qtyS));
                    adapter.swapAdapter(itemsForRequest);
                    dialog.dismiss();
                }
            }
        });


        Button cancelDialogue = dialog.findViewById(R.id.pop_up_item_request_cancel);
        cancelDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

