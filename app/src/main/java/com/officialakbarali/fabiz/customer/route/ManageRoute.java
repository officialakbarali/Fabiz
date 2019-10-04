package com.officialakbarali.fabiz.customer.route;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.officialakbarali.fabiz.CommonResumeCheck;
import com.officialakbarali.fabiz.R;

import java.util.Calendar;

public class ManageRoute extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_route);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CommonResumeCheck(this);
    }

    public void monday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.MONDAY + "");
        startActivity(setDayRouteIntent);
    }

    public void tuesday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.TUESDAY + "");
        startActivity(setDayRouteIntent);
    }

    public void wednesday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.WEDNESDAY + "");
        startActivity(setDayRouteIntent);
    }

    public void thursday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.THURSDAY + "");
        startActivity(setDayRouteIntent);
    }

    public void friday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.FRIDAY + "");
        startActivity(setDayRouteIntent);
    }

    public void saturday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.SATURDAY + "");
        startActivity(setDayRouteIntent);
    }

    public void sunday(View view) {
        Intent setDayRouteIntent = new Intent(ManageRoute.this, RouteModify.class);
        setDayRouteIntent.putExtra("today", Calendar.MONDAY + "");
        startActivity(setDayRouteIntent);
    }
}
