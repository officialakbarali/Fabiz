package com.officialakbarali.fabiz.data;

import android.provider.BaseColumns;

public class FabizContract {

    //TABLE SYNC LOG
    public static final class SyncLog implements BaseColumns {
        public static final String TABLE_NAME = "tb_sync_log";

        public static final String COLUMN_ROW_ID = "row_id";
        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String COLUMN_OPERATION = "operation";
    }

    //TABLE ITEM
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

    //TABLE CUSTOMER
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
}
