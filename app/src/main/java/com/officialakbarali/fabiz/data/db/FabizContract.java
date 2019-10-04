package com.officialakbarali.fabiz.data.db;

import android.provider.BaseColumns;

public class FabizContract {

    //1 TABLE SYNC LOG
    public static final class SyncLog implements BaseColumns {
        public static final String TABLE_NAME = "tb_sync_log";

        public static final String COLUMN_ROW_ID = TABLE_NAME + "_" + "row_id";
        public static final String COLUMN_TABLE_NAME = TABLE_NAME + "_" + "table_name";
        public static final String COLUMN_OPERATION = TABLE_NAME + "_" + "operation";
    }

    //2 TABLE ACCOUNT DETAIL
    public static final class AccountDetail implements BaseColumns {
        public static final String TABLE_NAME = "tb_account_detail";

        public static final String COLUMN_CUSTOMER_ID = TABLE_NAME + "_" + "customer_id";
        public static final String COLUMN_TOTAL = TABLE_NAME + "_" + "total";
        public static final String COLUMN_PAID = TABLE_NAME + "_" + "paid";
        public static final String COLUMN_DUE = TABLE_NAME + "_" + "due";

        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_CUSTOMER_ID = TABLE_NAME + "." + COLUMN_CUSTOMER_ID;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_PAID = TABLE_NAME + "." + COLUMN_PAID;
        public static final String FULL_COLUMN_DUE = TABLE_NAME + "." + COLUMN_DUE;
    }

    //3 TABLE ITEM
    public static final class Item implements BaseColumns {
        public static final String TABLE_NAME = "tb_item";

        public static final String COLUMN_BARCODE = TABLE_NAME + "_" + "co_barcode";

        public static final String COLUMN_NAME = TABLE_NAME + "_" + "name";
        public static final String COLUMN_BRAND = TABLE_NAME + "_" + "brand";
        public static final String COLUMN_CATEGORY = TABLE_NAME + "_" + "category";
        public static final String COLUMN_PRICE = TABLE_NAME + "_" + "price";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;

        public static final String FULL_COLUMN_BARCODE = TABLE_NAME + "." + COLUMN_BARCODE;

        public static final String FULL_COLUMN_NAME = TABLE_NAME + "." + COLUMN_NAME;
        public static final String FULL_COLUMN_BRAND = TABLE_NAME + "." + COLUMN_BRAND;
        public static final String FULL_COLUMN_CATAGORY = TABLE_NAME + "." + COLUMN_CATEGORY;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
    }

    //4 TABLE CUSTOMER
    public static final class Customer implements BaseColumns {
        public static final String TABLE_NAME = "tb_customer";

        public static final String COLUMN_BARCODE = TABLE_NAME + "_" + "co_barcode";

        public static final String COLUMN_DAY = TABLE_NAME + "_" + "day";

        public static final String COLUMN_NAME = TABLE_NAME + "_" + "name";
        public static final String COLUMN_PHONE = TABLE_NAME + "_" + "phone";
        public static final String COLUMN_EMAIL = TABLE_NAME + "_" + "email";
        public static final String COLUMN_ADDRESS = TABLE_NAME + "_" + "address";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;

        public static final String FULL_COLUMN_BARCODE = TABLE_NAME + "." + COLUMN_BARCODE;

        public static final String FULL_COLUMN_DAY = TABLE_NAME + "." + COLUMN_DAY;

        public static final String FULL_COLUMN_NAME = TABLE_NAME + "." + COLUMN_NAME;
        public static final String FULL_COLUMN_PHONE = TABLE_NAME + "." + COLUMN_PHONE;
        public static final String FULL_COLUMN_EMAIL = TABLE_NAME + "." + COLUMN_EMAIL;
        public static final String FULL_COLUMN_ADDRESS = TABLE_NAME + "." + COLUMN_ADDRESS;
    }

    //5 TABLE BILL DETAIL
    public static final class BillDetail implements BaseColumns {
        public static final String TABLE_NAME = "tb_bill_detail";

        public static final String COLUMN_DATE = TABLE_NAME + "_" + "dateofbill";
        public static final String COLUMN_CUST_ID = TABLE_NAME + "_" + "custid";
        public static final String COLUMN_PRICE = TABLE_NAME + "_" + "price";
        public static final String COLUMN_QTY = TABLE_NAME + "_" + "qty";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_DATE = TABLE_NAME + "." + COLUMN_DATE;
        public static final String FULL_COLUMN_CUST_ID = TABLE_NAME + "." + COLUMN_CUST_ID;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
        public static final String FULL_COLUMN_QTY = TABLE_NAME + "." + COLUMN_QTY;
    }


    //6 TABLE CART
    public static final class Cart implements BaseColumns {
        public static final String TABLE_NAME = "tb_cart";

        public static final String COLUMN_BILL_ID = TABLE_NAME + "_" + "billid";
        public static final String COLUMN_ITEM_ID = TABLE_NAME + "_" + "itemid";
        public static final String COLUMN_NAME = TABLE_NAME + "_" + "name";
        public static final String COLUMN_BRAND = TABLE_NAME + "_" + "brand";
        public static final String COLUMN_CATEGORY = TABLE_NAME + "_" + "category";
        public static final String COLUMN_PRICE = TABLE_NAME + "_" + "price";
        public static final String COLUMN_QTY = TABLE_NAME + "_" + "qty";
        public static final String COLUMN_TOTAL = TABLE_NAME + "_" + "total";
        public static final String COLUMN_RETURN_QTY = TABLE_NAME + "_" + "returnqty";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_BILL_ID = TABLE_NAME + "." + COLUMN_BILL_ID;
        public static final String FULL_COLUMN_ITEM_ID = TABLE_NAME + "." + COLUMN_ITEM_ID;
        public static final String FULL_COLUMN_NAME = TABLE_NAME + "." + COLUMN_NAME;
        public static final String FULL_COLUMN_BRAND = TABLE_NAME + "." + COLUMN_BRAND;
        public static final String FULL_COLUMN_CATAGORY = TABLE_NAME + "." + COLUMN_CATEGORY;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
        public static final String FULL_COLUMN_QTY = TABLE_NAME + "." + COLUMN_QTY;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_RETURN_QTY = TABLE_NAME + "." + COLUMN_RETURN_QTY;
    }

    //7 TABLE SALES RETURN
    public static final class SalesReturn implements BaseColumns {
        public static final String TABLE_NAME = "tb_sales_return";

        public static final String COLUMN_DATE = TABLE_NAME + "_" + "dateofreturn";
        public static final String COLUMN_BILL_ID = TABLE_NAME + "_" + "billid";
        public static final String COLUMN_ITEM_ID = TABLE_NAME + "_" + "itemid";
        public static final String COLUMN_PRICE = TABLE_NAME + "_" + "price";
        public static final String COLUMN_QTY = TABLE_NAME + "_" + "qty";
        public static final String COLUMN_TOTAL = TABLE_NAME + "_" + "total";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_DATE = TABLE_NAME + "." + COLUMN_DATE;
        public static final String FULL_COLUMN_BILL_ID = TABLE_NAME + "." + COLUMN_BILL_ID;
        public static final String FULL_COLUMN_ITEM_ID = TABLE_NAME + "." + COLUMN_ITEM_ID;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
        public static final String FULL_COLUMN_QTY = TABLE_NAME + "." + COLUMN_QTY;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;

    }

    //8 TABLE PAYMENT
    public static final class Payment implements BaseColumns {
        public static final String TABLE_NAME = "tb_payment";

        public static final String COLUMN_CUST_ID = TABLE_NAME + "_" + "custid";
        public static final String COLUMN_DATE = TABLE_NAME + "_" + "dateofreturn";
        public static final String COLUMN_AMOUNT = TABLE_NAME + "_" + "amount";
        public static final String COLUMN_TOTAL = TABLE_NAME + "_" + "total";
        public static final String COLUMN_PAID = TABLE_NAME + "_" + "paid";
        public static final String COLUMN_DUE = TABLE_NAME + "_" + "due";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_CUST_ID = TABLE_NAME + "." + COLUMN_CUST_ID;
        public static final String FULL_COLUMN_DATE = TABLE_NAME + "." + COLUMN_DATE;
        public static final String FULL_COLUMN_AMOUNT = TABLE_NAME + "." + COLUMN_AMOUNT;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_PAID = TABLE_NAME + "." + COLUMN_PAID;
        public static final String FULL_COLUMN_DUE = TABLE_NAME + "." + COLUMN_DUE;


    }
}