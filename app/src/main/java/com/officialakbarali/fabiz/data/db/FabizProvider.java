package com.officialakbarali.fabiz.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.officialakbarali.fabiz.LogIn;
import com.officialakbarali.fabiz.data.db.FabizDbHelper;

public class FabizProvider {
    private FabizDbHelper fabizDbHelper;
    private SQLiteDatabase database;
    private Context context;

    public FabizProvider(Context context, boolean writableOperation) {
        fabizDbHelper = FabizDbHelper.getInstance(context);
        if (writableOperation) {
            database = fabizDbHelper.getWritableDatabase();
        }
        this.context = context;
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

    public Cursor queryExplicit(boolean distinct, String tableName, String[] projection, String selection, String[] selectionArgs,
                                String groupBy,
                                String having,
                                String sortOrder
            , String limit) {
        SQLiteDatabase databaseQ = fabizDbHelper.getReadableDatabase();
        Cursor returnCursor = databaseQ.query(
                distinct,
                tableName,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
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

    public int getIdForInsert(String tableName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int precision = sharedPreferences.getInt("precision", 1);
        precision = precision * 100000;
        int maxLimit = precision + 100000;

        SQLiteDatabase databaseQ = fabizDbHelper.getReadableDatabase();
        Cursor cursor = databaseQ.query(tableName, new String[]{"_id"}, "_id BETWEEN ? AND ?"
                , new String[]{precision + "", maxLimit + ""},
                null, null,
                "_id DESC", "1");

        int currentLatest = 0;
        if (cursor.moveToNext()) {
            currentLatest = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            currentLatest++;
        } else {
            currentLatest = precision;
        }

        if (currentLatest >= maxLimit) {
            currentLatest = -1;
        }
        return currentLatest;
    }

    public void deleteAllTables() {
        createTransaction();
        database.delete(FabizContract.SyncLog.TABLE_NAME, null, null);
        database.delete(FabizContract.Item.TABLE_NAME, null, null);
        database.delete(FabizContract.Customer.TABLE_NAME, null, null);
        database.delete(FabizContract.BillDetail.TABLE_NAME, null, null);
        database.delete(FabizContract.Cart.TABLE_NAME, null, null);
        database.delete(FabizContract.SalesReturn.TABLE_NAME, null, null);
        database.delete(FabizContract.Payment.TABLE_NAME, null, null);
        successfulTransaction();
        finishTransaction();
    }

    public double getCount(String tableName, String columnName, String selection, String[] selectionArg) {
        SQLiteDatabase db = fabizDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + columnName + ") as Total FROM " + tableName + " WHERE " + selection, selectionArg);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("Total"));
        }
        return total;
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
