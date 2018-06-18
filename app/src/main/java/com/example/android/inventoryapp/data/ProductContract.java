package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public final class ProductContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ProductContract() {
    }

    /* Inner class that defines the table contents */
    public static final class ProductEntry {
        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }

}
