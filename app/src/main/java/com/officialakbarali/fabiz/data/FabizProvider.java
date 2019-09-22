package com.officialakbarali.fabiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FabizProvider {
    private FabizDbHelper fabizDbHelper;

    public FabizProvider(Context context) {
        fabizDbHelper =  FabizDbHelper.getInstance(context);
    }

    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = fabizDbHelper.getReadableDatabase();
        Cursor returnCursor = database.query(tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return returnCursor;
    }

    public long insert(String tableName, ContentValues values) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        long returnResult = database.insert(tableName, null, values);
        return returnResult;
    }

    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        int returnResult = database.update(tableName, values, selection, selectionArgs);
        return returnResult;
    }

    public int delete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase database = fabizDbHelper.getWritableDatabase();
        int returnResult = database.delete(tableName, selection, selectionArgs);
        return returnResult;
    }
}
