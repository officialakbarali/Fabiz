package com.officialakbarali.fabiz.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FabizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fabiz.db";
    private static final int DATABASE_VERSION = 1;

    FabizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATING CUSTOMER TABLE
        String SQL_CREATE_CUSTOMER_TABLE = "CREATE TABLE "
                + FabizContract.Customer.TABLE_NAME
                + " ("
                + FabizContract.Customer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.Customer.COLUMN_NAME + " TEXT NOT NULL, "
                + FabizContract.Customer.COLUMN_PHONE + " TEXT NOT NULL, "
                + FabizContract.Customer.COLUMN_EMAIL + " TEXT NOT NULL,"
                + FabizContract.Customer.COLUMN_ADDRESS + " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DELETING OLD CUSTOMER TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.Customer.TABLE_NAME);

        //RE-CREATING EVERY TABLES
        onCreate(db);
    }
}
