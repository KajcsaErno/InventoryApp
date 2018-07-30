package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ChocolateContract.ChocolateEntry;

/**
 * Database helper for Chocolate Inventory app. Manages database creation and version management.
 */
public class ChocolateDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "products.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ChocolateDbHelper}.
     *
     * @param context of the app
     */
    ChocolateDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the chocolate table
        String SQL_CREATE_CHOCOLATES_TABLE = "CREATE TABLE " + ChocolateEntry.TABLE_NAME + "("
                + ChocolateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ChocolateEntry.COLUMN_CHOCOLATE_NAME + " TEXT NOT NULL, "
                + ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR + " TEXT, "
                + ChocolateEntry.COLUMN_CHOCOLATE_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY + " INTEGER DEFAULT 0, "
                + ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_NAME + " TEXT, "
                + ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_PHONE_NUMBER + " TEXT);";
        db.execSQL(SQL_CREATE_CHOCOLATES_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}