package com.officialakbarali.fabiz.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.officialakbarali.fabiz.data.db.FabizDbHelper;

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

    public void deleteAllTables() {
        createTransaction();
        database.delete(FabizContract.SyncLog.TABLE_NAME, null, null);
        database.delete(FabizContract.AccountDetail.TABLE_NAME, null, null);
        database.delete(FabizContract.Item.TABLE_NAME, null, null);
        database.delete(FabizContract.Customer.TABLE_NAME, null, null);
        database.delete(FabizContract.BillDetail.TABLE_NAME, null, null);
        database.delete(FabizContract.Cart.TABLE_NAME, null, null);
        database.delete(FabizContract.SalesReturn.TABLE_NAME, null, null);
        database.delete(FabizContract.Payment.TABLE_NAME, null, null);
        successfulTransaction();
        finishTransaction();
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

    public void successfulTransaction() {
        database.setTransactionSuccessful();
    }
}
