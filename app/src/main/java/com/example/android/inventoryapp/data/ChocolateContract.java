package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public final class ChocolateContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.chocolates";
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.chocolates/chocolates/ is a valid path for
     * looking at chocolate data. content://com.example.android.chocolates/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_CHOCOLATES = "chocolates";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ChocolateContract() {
    }

    /**
     * Inner class that defines constant values for the chocolates database table.
     * Each entry in the table represents a single chocolate.
     */
    public static final class ChocolateEntry implements BaseColumns {

        /**
         * The content URI to access the chocolate data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CHOCOLATES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of chocolates.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHOCOLATES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single chocolate.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHOCOLATES;

        /**
         * Name of database table for chocolates
         */
        public final static String TABLE_NAME = "chocolates";

        /**
         * Unique ID number for the chocolate (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the chocolate.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CHOCOLATE_NAME = "name";

        /**
         * Flavor of the chocolate.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CHOCOLATE_FLAVOR = "flavor";

        /**
         * Price of the chocolate.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CHOCOLATE_PRICE = "price";

        /**
         * Quantity of the chocolate.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CHOCOLATE_QUANTITY = "quantity";

        /**
         * Name of the supplier.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CHOCOLATE_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number of the supplier.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CHOCOLATE_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }

}
