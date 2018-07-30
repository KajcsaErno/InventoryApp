package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ChocolateContract.ChocolateEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays list of chocolates that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the chocolate data loader */
    private static final int CHOCOLATE_LOADER = 0;
    @BindView(R.id.floating_chocolate_button)
    FloatingActionButton floatingChocolateButton;
    /** Adapter for the ListView */
    ChocolateCursorAdapter cursorAdapter;

    @BindView(R.id.chocolate_listView)
    ListView chocolateListView;

    @BindView(R.id.empty_linearLayout)
    View emptyLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        floatingChocolateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openEditorIntent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(openEditorIntent);
            }
        });
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        chocolateListView.setEmptyView(emptyLinearLayout);

        // Setup an Adapter to create a list item for each row of chocolate data in the Cursor.
        // There is no chocolate data yet (until the loader finishes) so pass in null for the Cursor.
        cursorAdapter = new ChocolateCursorAdapter(this, null);
        chocolateListView.setAdapter(cursorAdapter);

        // Setup the item click listener
        chocolateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent openEditorIntent = new Intent(MainActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific chocolate that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ChocolateEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.chocolates/chocolates/2"
                // if the chocolate with ID 2 was clicked on.
                Uri currentChocolateUri = ContentUris.withAppendedId(ChocolateEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                openEditorIntent.setData(currentChocolateUri);

                // Launch the {@link EditorActivity} to display the data for the current chocolate.
                startActivity(openEditorIntent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(CHOCOLATE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded chocolate data into the database. For debugging purposes only.
     */
    private void insertChocolate() {
        // Create a new map of contentValues, where column names are the keys
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_NAME, getString(R.string.milka));
        contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR, getString(R.string.whole_nuts));
        contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_PRICE, "5");
        contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY, "10");
        contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_NAME, getString(R.string.coocke));
        contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_PHONE_NUMBER, getString(R.string.phone));

        getContentResolver().insert(ChocolateEntry.CONTENT_URI, contentValues);
    }

    /**
     * Helper method to delete all chocolates in the database.
     */
    private void deleteAllChocolates() {
        int rowsDeleted = getContentResolver().delete(ChocolateEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from chocolate database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xmlle.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertChocolate();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllChocolates();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ChocolateEntry._ID,
                ChocolateEntry.COLUMN_CHOCOLATE_NAME,
                ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR,
                ChocolateEntry.COLUMN_CHOCOLATE_PRICE,
                ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ChocolateEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Update {@link ChocolateCursorAdapter} with this new cursor containing updated chocolate data
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        cursorAdapter.swapCursor(null);

    }

}
