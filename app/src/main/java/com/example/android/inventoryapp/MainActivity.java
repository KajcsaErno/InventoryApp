package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ProductDbHelper mDbHelper;

    // Gets the data repository in write mode
   private SQLiteDatabase db = mDbHelper.getWritableDatabase();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new ProductDbHelper(this);

    }

    private void insertData() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Dummy Book Name");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 19.33);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Dummy Supplier Name");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "1234567890");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);
        Log.v(LOG_TAG, "New row ID " + newRowId);
    }

    private Cursor queryData() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME, //The table to query
                projection,              // The array of columns to return (pass null to get all)
                null,            // The columns for the WHERE clause
                null,           // The values for the WHERE clause
                null,            // don't group the rows
                null,               // don't filter by row groups
                null);              // The sort order



        // Always close the cursor when you're done reading from it. This releases all its
        // resources and makes it invalid.
        cursor.close();
        Log.v(LOG_TAG, "Cursor: " + cursor);
        return cursor;

    }
}
