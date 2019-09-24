package com.officialakbarali.fabiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FabizProvider {
    private FabizDbHelper fabizDbHelper;
    private SQLiteDatabase database;

    public FabizProvider(Context context, boolean writableOperation) {
        fabizDbHelper = FabizDbHelper.getInstance(context);
        if (writableOperation) {
            database = fabizDbHelper.getWritableDatabase();
        }

    }

    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase databaseQ = fabizDbHelper.getReadableDatabase();
        Cursor returnCursor = databaseQ.query(tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return returnCursor;
    }

    public long insert(String tableName, ContentValues values) {
        return database.insert(tableName, null, values);
    }

    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(tableName, values, selection, selectionArgs);
    }

    public int delete(String tableName, String selection, String[] selectionArgs) {
        return database.delete(tableName, selection, selectionArgs);
    }

    public void createTransaction() {
        database.beginTransaction();
    }

    public boolean isItInTranscation() {
        return database.inTransaction();
    }

    public void finishTransaction() {
        database.endTransaction();
    }

    public void successfulTransaction(){
        database.setTransactionSuccessful();
    }
}
