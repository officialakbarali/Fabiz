package com.officialakbarali.fabiz.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FabizDbHelper extends SQLiteOpenHelper {

    private static FabizDbHelper mInstance = null;

    private static final String DATABASE_NAME = "fabiz.db";
    private static final int DATABASE_VERSION = 1;


    public static FabizDbHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new FabizDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private FabizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATING SYNC_LOG TABLE
        String SQL_CREATE_SYNC_LOG_TABLE = "CREATE TABLE "
                + FabizContract.SyncLog.TABLE_NAME
                + " ("
                + FabizContract.SyncLog._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.SyncLog.COLUMN_ROW_ID + " INTEGER NOT NULL, "
                + FabizContract.SyncLog.COLUMN_TABLE_NAME + " TEXT NOT NULL, "
                + FabizContract.SyncLog.COLUMN_OP_CODE + " TEXT NOT NULL, "
                + FabizContract.SyncLog.COLUMN_TIMESTAMP + " DATETIME  NOT NULL, "
                + FabizContract.SyncLog.COLUMN_OPERATION + " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_SYNC_LOG_TABLE);

        //CREATING ITEM TABLE
        String SQL_CREATE_ITEM_TABLE = "CREATE TABLE "
                + FabizContract.Item.TABLE_NAME
                + " ("
                + FabizContract.Item._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.Item.COLUMN_BARCODE + " TEXT, "
                + FabizContract.Item.COLUMN_NAME + " TEXT NOT NULL, "
                + FabizContract.Item.COLUMN_BRAND + " TEXT NOT NULL, "
                + FabizContract.Item.COLUMN_CATEGORY + " TEXT NOT NULL,"
                + FabizContract.Item.COLUMN_PRICE + " REAL NOT NULL)";
        db.execSQL(SQL_CREATE_ITEM_TABLE);

        //CREATING CUSTOMER TABLE
        String SQL_CREATE_CUSTOMER_TABLE = "CREATE TABLE "
                + FabizContract.Customer.TABLE_NAME
                + " ("
                + FabizContract.Customer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.Customer.COLUMN_BARCODE + " TEXT, "
                + FabizContract.Customer.COLUMN_DAY + " TEXT, "
                + FabizContract.Customer.COLUMN_CR_NO + " TEXT NOT NULL, "
                + FabizContract.Customer.COLUMN_SHOP_NAME + " TEXT NOT NULL, "
                + FabizContract.Customer.COLUMN_NAME + " TEXT NOT NULL, "
                + FabizContract.Customer.COLUMN_PHONE + " TEXT NOT NULL, "
                + FabizContract.Customer.COLUMN_EMAIL + " TEXT NOT NULL,"
                + FabizContract.Customer.COLUMN_ADDRESS + " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_CUSTOMER_TABLE);

        //CREATING BILL DETAIL
        String SQL_CREATE_BILL_DETAIL_TABLE = "CREATE TABLE "
                + FabizContract.BillDetail.TABLE_NAME
                + " ("
                + FabizContract.BillDetail._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.BillDetail.COLUMN_DATE + " TEXT NOT NULL, "
                + FabizContract.BillDetail.COLUMN_CUST_ID + " INTEGER NOT NULL, "
                + FabizContract.BillDetail.COLUMN_PRICE + " REAL NOT NULL,"
                + FabizContract.BillDetail.COLUMN_RETURNED_TOTAL + " REAL NOT NULL,"
                + FabizContract.BillDetail.COLUMN_CURRENT_TOTAL + " REAL NOT NULL,"
                + FabizContract.BillDetail.COLUMN_PAID + " REAL NOT NULL,"
                + FabizContract.BillDetail.COLUMN_DUE + " REAL NOT NULL,"
                + FabizContract.BillDetail.COLUMN_QTY + " INTEGER NOT NULL)";
        db.execSQL(SQL_CREATE_BILL_DETAIL_TABLE);

        //CREATING CART
        String SQL_CREATE_CART_TABLE = "CREATE TABLE "
                + FabizContract.Cart.TABLE_NAME
                + " ("
                + FabizContract.Cart._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.Cart.COLUMN_BILL_ID + " INTEGER NOT NULL, "
                + FabizContract.Cart.COLUMN_ITEM_ID + " INTEGER NOT NULL, "
                + FabizContract.Cart.COLUMN_NAME + " TEXT NOT NULL,"
                + FabizContract.Cart.COLUMN_BRAND + " TEXT NOT NULL,"
                + FabizContract.Cart.COLUMN_CATEGORY + " TEXT NOT NULL,"
                + FabizContract.Cart.COLUMN_PRICE + " REAL NOT NULL,"
                + FabizContract.Cart.COLUMN_QTY + " INTEGER NOT NULL,"
                + FabizContract.Cart.COLUMN_TOTAL + " REAL NOT NULL,"
                + FabizContract.Cart.COLUMN_RETURN_QTY + " INTEGER NOT NULL)";
        db.execSQL(SQL_CREATE_CART_TABLE);

        //CREATING SALES RETURN
        String SQL_CREATE_SALES_RETURN_TABLE = "CREATE TABLE "
                + FabizContract.SalesReturn.TABLE_NAME
                + " ("
                + FabizContract.SalesReturn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.SalesReturn.COLUMN_DATE + " TEXT NOT NULL,"
                + FabizContract.SalesReturn.COLUMN_BILL_ID + " INTEGER NOT NULL, "
                + FabizContract.SalesReturn.COLUMN_ITEM_ID + " INTEGER NOT NULL, "
                + FabizContract.SalesReturn.COLUMN_PRICE + " REAL NOT NULL,"
                + FabizContract.SalesReturn.COLUMN_QTY + " INTEGER NOT NULL,"
                + FabizContract.SalesReturn.COLUMN_TOTAL + " REAL NOT NULL)";

        db.execSQL(SQL_CREATE_SALES_RETURN_TABLE);

        //CREATING PAYMENT
        String SQL_CREATE_PAYMENT = "CREATE TABLE "
                + FabizContract.Payment.TABLE_NAME
                + " ("
                + FabizContract.Payment._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FabizContract.Payment.COLUMN_BILL_ID + " INTEGER NOT NULL, "
                + FabizContract.Payment.COLUMN_DATE + " TEXT NOT NULL,"
                + FabizContract.Payment.COLUMN_AMOUNT + " REAL NOT NULL)";

        db.execSQL(SQL_CREATE_PAYMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DELETING OLD SYNC_LOG TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.SyncLog.TABLE_NAME);

        //DELETING ITEM TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.Item.TABLE_NAME);

        //DELETING OLD CUSTOMER TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.Customer.TABLE_NAME);

        //DELETING OLD BILL DETAIL TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.BillDetail.TABLE_NAME);

        //DELETING OLD CART TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.Cart.TABLE_NAME);

        //DELETING OLD SALES RETURN TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.SalesReturn.TABLE_NAME);

        //DELETING OLD PAYMENT TABLE
        db.execSQL("DROP TABLE IF EXISTS " + FabizContract.Payment.TABLE_NAME);

        //RE-CREATING EVERY TABLES
        onCreate(db);
    }
}
