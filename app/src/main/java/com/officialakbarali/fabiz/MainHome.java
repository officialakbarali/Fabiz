package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.officialakbarali.fabiz.customer.Customer;
import com.officialakbarali.fabiz.data.db.FabizContract;
import com.officialakbarali.fabiz.data.db.FabizProvider;
import com.officialakbarali.fabiz.item.Item;
import com.officialakbarali.fabiz.network.syncInfo.SyncInformation;
import com.officialakbarali.fabiz.requestStock.RequestStock;

import java.util.ArrayList;

import static com.officialakbarali.fabiz.requestStock.RequestStock.itemsForRequest;

public class MainHome extends AppCompatActivity {
    LinearLayout customerL, stockL, requestL, syncL, settingsL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        setUpHomeButton();
    }


    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
        setUpAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    private void setUpHomeButton() {

        customerL = findViewById(R.id.home_cust);
        stockL = findViewById(R.id.home_stock);
        requestL = findViewById(R.id.home_request);
        syncL = findViewById(R.id.home_sync);
        settingsL = findViewById(R.id.home_settings);

        customerL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = findViewById(R.id.home_cust_txt);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        customerL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon_pressed));
                        textView.setTextColor(getResources().getColor(R.color.pure_white));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        customerL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon));
                        textView.setTextColor(getResources().getColor(R.color.text_color));
                        Intent custIntent = new Intent(MainHome.this, Customer.class);
                        startActivity(custIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        stockL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = findViewById(R.id.home_stock_txt);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        stockL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon_pressed));
                        textView.setTextColor(getResources().getColor(R.color.pure_white));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        stockL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon));
                        textView.setTextColor(getResources().getColor(R.color.text_color));
                        Intent itemShow = new Intent(MainHome.this, Item.class);
                        startActivity(itemShow);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        requestL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = findViewById(R.id.home_request_txt);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        requestL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon_pressed));
                        textView.setTextColor(getResources().getColor(R.color.pure_white));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        requestL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon));
                        textView.setTextColor(getResources().getColor(R.color.text_color));
                        Intent requestStockShow = new Intent(MainHome.this, RequestStock.class);
                        itemsForRequest = new ArrayList<>();
                        startActivity(requestStockShow);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        syncL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = findViewById(R.id.home_sync_txt);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        syncL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon_pressed));
                        textView.setTextColor(getResources().getColor(R.color.pure_white));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        syncL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon));
                        textView.setTextColor(getResources().getColor(R.color.text_color));
                        Intent viewSyncIntent = new Intent(MainHome.this, SyncInformation.class);
                        startActivity(viewSyncIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        settingsL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = findViewById(R.id.home_settings_txt);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        settingsL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon_pressed));
                        textView.setTextColor(getResources().getColor(R.color.pure_white));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        settingsL.setBackground(getResources().getDrawable(R.drawable.color_main_home_icon));
                        textView.setTextColor(getResources().getColor(R.color.text_color));
                        Intent intentSettings = new Intent(MainHome.this, Settings.class);
                        startActivity(intentSettings);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });


    }

    private void setUpAnimation() {
        TextView head, belowHead;
        head = findViewById(R.id.home_cust_head);
        belowHead = findViewById(R.id.home_cust_below_head);

        YoYo.with(Techniques.SlideInLeft).duration(1200).repeat(0).playOn(customerL);
        YoYo.with(Techniques.SlideInRight).duration(1300).repeat(0).playOn(stockL);

        YoYo.with(Techniques.SlideInLeft).duration(1400).repeat(0).playOn(requestL);
        YoYo.with(Techniques.SlideInRight).duration(1500).repeat(0).playOn(syncL);

        YoYo.with(Techniques.SlideInUp).duration(1600).repeat(0).playOn(settingsL);

        YoYo.with(Techniques.FadeInDown).duration(1000).repeat(0).playOn(head);

        YoYo.with(Techniques.FadeInUp).duration(1100).repeat(0).playOn(belowHead);

    }
}
