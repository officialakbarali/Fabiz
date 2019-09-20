package com.officialakbarali.fabiz.data;

import android.provider.BaseColumns;

public class FabizContract {
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
