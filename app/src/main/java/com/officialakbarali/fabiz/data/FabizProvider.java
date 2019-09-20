package com.officialakbarali.fabiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FabizProvider {
    private FabizDbHelper fabizDbHelper;

    public FabizProvider(Context context) {
        fabizDbHelper = new FabizDbHelper(context);
    }

    //SETTING CUSTOMER TABLE HANDLERS
    public Cursor customerQuery(String[] projection, String selection, String[] selectionArgs,
                                String sortOrder) {
        SQLiteDatabase database = fabizDbHelper.getReadableDatabase();
        return database.query(FabizContract.Customer.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public long customerInsert(ContentValues values) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        return database.insert(FabizContract.Customer.TABLE_NAME, null, values);
    }

    public int customerUpdate(ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        return database.update(FabizContract.Customer.TABLE_NAME, values, selection, selectionArgs);
    }

    public int customerDelete(String selection, String[] selectionArgs) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        return database.delete(FabizContract.Customer.TABLE_NAME, selection, selectionArgs);
    }

}
