package com.officialakbarali.fabiz.data;

import android.provider.BaseColumns;

public class FabizContract {

    //1 TABLE SYNC LOG
    public static final class SyncLog implements BaseColumns {
        public static final String TABLE_NAME = "tb_sync_log";

        public static final String COLUMN_ROW_ID = "row_id";
        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_OPERATION = "operation";
    }

    //2 TABLE ACCOUNT DETAIL
    public static final class AccountDetail implements BaseColumns {
        public static final String TABLE_NAME = "tb_account_detail";

        public static final String COLUMN_CUSTOMER_ID = "customer_id";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_PAID = "paid";
        public static final String COLUMN_DUE = "due";

        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_CUSTOMER_ID = TABLE_NAME + "." + COLUMN_CUSTOMER_ID;
        public static final String FULL_COLUMN_TOTAL = TABLE_NAME + "." + COLUMN_TOTAL;
        public static final String FULL_COLUMN_PAID = TABLE_NAME + "." + COLUMN_PAID;
        public static final String FULL_COLUMN_DUE = TABLE_NAME + "." + COLUMN_DUE;
    }

    //3 TABLE ITEM
    public static final class Item implements BaseColumns {
        public static final String TABLE_NAME = "tb_item";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PRICE = "price";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_NAME = TABLE_NAME + "." + COLUMN_NAME;
        public static final String FULL_COLUMN_BRAND = TABLE_NAME + "." + COLUMN_BRAND;
        public static final String FULL_COLUMN_CATAGORY = TABLE_NAME + "." + COLUMN_CATEGORY;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
    }

    //4 TABLE CUSTOMER
    public static final class Customer implements BaseColumns {
        public static final String TABLE_NAME = "tb_customer";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_ADDRESS = "address";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_NAME = TABLE_NAME + "." + COLUMN_NAME;
        public static final String FULL_COLUMN_PHONE = TABLE_NAME + "." + COLUMN_PHONE;
        public static final String FULL_COLUMN_EMAIL = TABLE_NAME + "." + COLUMN_EMAIL;
        public static final String FULL_COLUMN_ADDRESS = TABLE_NAME + "." + COLUMN_ADDRESS;
    }

    //5 TABLE BILL DETAIL
    public static final class BillDetail implements BaseColumns {
        public static final String TABLE_NAME = "tb_bill_detail";

        public static final String COLUMN_DATE = "dateofbill";
        public static final String COLUMN_CUST_ID = "custid";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QTY = "qty";

        //FULL COLUMN NAME
        public static final String FULL_COLUMN_ID = TABLE_NAME + "." + _ID;
        public static final String FULL_COLUMN_DATE = TABLE_NAME + "." + COLUMN_DATE;
        public static final String FULL_COLUMN_CUST_ID = TABLE_NAME + "." + COLUMN_CUST_ID;
        public static final String FULL_COLUMN_PRICE = TABLE_NAME + "." + COLUMN_PRICE;
        public static final String FULL_COLUMN_QTY =TABLE_NAME + "." + COLUMN_QTY;
    }


    //6 TABLE CART
    public static final class Cart implements BaseColumns {
        public static final String TABLE_NAME = "tb_cart";

        public static final String COLUMN_BILL_ID = "billid";
        public static final String COLUMN_ITEM_ID = "itemid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_RETURN_QTY = "returnqty";

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
}
