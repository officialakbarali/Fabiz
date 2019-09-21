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


    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs,
                                String sortOrder) {
        SQLiteDatabase database = fabizDbHelper.getReadableDatabase();
        return database.query(tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public long insert(String tableName, ContentValues values) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        return database.insert(tableName, null, values);
    }

    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        return database.update(tableName, values, selection, selectionArgs);
    }

    public int delete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        return database.delete(tableName, selection, selectionArgs);
    }

}
