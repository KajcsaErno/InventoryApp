package com.example.android.inventoryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ChocolateContract.ChocolateEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Allows user to create a new chocolate or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the chocolate data loader
     */
    private static final int EXISTING_CHOCOLATE_LOADER = 0;
    @BindView(R.id.save_button)
    Button saveButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;
    @BindView(R.id.delete_button)
    Button deleteButton;
    @BindView(R.id.name_editText)
    EditText nameEditText;
    @BindView(R.id.flavor_editText)
    EditText flavorEditText;
    @BindView(R.id.price_editText)
    EditText priceEditText;
    @BindView(R.id.quantity_editText)
    EditText quantityEditText;
    @BindView(R.id.minus_icon_imageView)
    ImageView minusImageView;
    @BindView(R.id.plus_icon_imageView)
    ImageView plusImageView;
    @BindView(R.id.supplier_name_editText)
    EditText supplierNameEditText;
    @BindView(R.id.supplier_phone_editText)
    EditText supplierPhoneEditText;
    @BindView(R.id.supplier_phone_imageView)
    ImageView supplierPhoneImageView;
    @BindView(R.id.supplier_name_imageView)
    ImageView supplierNameImageView;
    @BindView(R.id.coins_imageView)
    ImageView coinsImageView;
    @BindView(R.id.oval_camera_ImageView_style)
    ImageView cameraImageView;

    /**
     * Content URI for the existing chocolate (null if it's a new chocolate)
     */
    private Uri currentChocolateUri;
    private boolean chocolateHasChanged = false;
    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the chocolateHasChanged boolean to true.
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            chocolateHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new chocolate or editing an existing one.
        Intent intent = getIntent();
        currentChocolateUri = intent.getData();

        // If the intent DOES NOT contain a chocolate content URI, then we know that we are
        // creating a new chocolate.
        if (currentChocolateUri == null) {
            // Invalidate the  "Delete" button so it can be hidden.
            // (It doesn't make sense to delete a chocolate that hasn't been created yet.)
            deleteButton.setVisibility(View.GONE);

        } else {
            // Initialize a loader to read the chocolate data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_CHOCOLATE_LOADER, null, this);
        }

        nameEditText.setOnTouchListener(touchListener);
        flavorEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChocolate();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityInteger;
                try {
                    quantityInteger = Integer.parseInt(quantityEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), R.string.you_cant_add_one, Toast.LENGTH_LONG).show();
                    return;
                }
                quantityEditText.setText(String.valueOf(quantityInteger + 1));
            }
        });

        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityInteger;
                try {
                    quantityInteger = Integer.parseInt(quantityEditText.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), R.string.you_cant_remove_one, Toast.LENGTH_LONG).show();
                    return;
                }
                if (quantityInteger > 0) {
                    quantityEditText.setText(String.valueOf(quantityInteger - 1));
                } else {
                    Toast.makeText(EditorActivity.this, R.string.quantity_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditorActivity.this, "Adding a chocolate picture coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Get user input from editor and save chocolate into database.
     */
    private void saveChocolate() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String flavorString = flavorEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(flavorString)
                || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, R.string.please_fill_all, Toast.LENGTH_LONG).show();

        } else {

            // Create a ContentValues object where column names are the keys, and chocolate attributes from the editor are the values.
            ContentValues contentValues;
            contentValues = new ContentValues();
            contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_NAME, nameString);
            contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR, flavorString);
            contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_NAME, supplierNameString);
            contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

            // If the price is not provided by the user, don't try to parse the string into an integer value. Use 0 by default.
            int price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                try {
                    price = Integer.parseInt(priceString);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), R.string.ivalid_number, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_PRICE, price);

            // If the quantity is not provided by the user, don't try to parse the string into an integer value. Use 0 by default.
            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                try {
                    quantity = Integer.parseInt(quantityString);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), R.string.ivalid_number, Toast.LENGTH_LONG).show();
                    return;
                }
            }
            contentValues.put(ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY, quantity);

            // Determine if this is a new or existing chocolate by checking if currentChocolateUri is null or not
            if (currentChocolateUri == null) {
                // This is a NEW chocolate, so insert a new chocolate into the provider,
                // returning the content URI for the new chocolate.
                Uri newUri = getContentResolver().insert(ChocolateEntry.CONTENT_URI, contentValues);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_chocolate_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_chocolate_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING chocolate, so update the chocolate with content URI: currentChocolateUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because currentChocolateUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(currentChocolateUri, contentValues, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_chocolate_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_chocolate_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all chocolate attributes, define a projection that contains all columns from the chocolate table
        String[] projection = {
                ChocolateEntry._ID,
                ChocolateEntry.COLUMN_CHOCOLATE_NAME,
                ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR,
                ChocolateEntry.COLUMN_CHOCOLATE_PRICE,
                ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY,
                ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_NAME,
                ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentChocolateUri,         // Query the content URI for the current chocolate
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
// Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of chocolate attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_NAME);
            int flavorColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR);
            int priceColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String flavor = cursor.getString(flavorColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            //Opening phone app, with the Supplier phone number in it, if the user gives us their permission
            supplierPhoneImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(getString(R.string.tel) + supplierPhone));
                    if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(EditorActivity.this, Manifest.permission.ANSWER_PHONE_CALLS)) {
                            Toast.makeText(EditorActivity.this, R.string.permission, Toast.LENGTH_LONG).show();
                        } else {
                            ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 26);
                        }
                    } else {
                        startActivity(callIntent);
                    }
                }
            });

            //Opening contacts
            supplierNameImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    startActivity(intent);
                }
            });

            //Opening the current website for viewing today's currency
            coinsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(getString(R.string.todays_currency));
                    Intent openWebsiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                    // Verify that the intent will resolve to an activity
                    if (openWebsiteIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(openWebsiteIntent);
                    }

                    Toast.makeText(EditorActivity.this, R.string.currency, Toast.LENGTH_SHORT).show();
                }
            });


            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            flavorEditText.setText(flavor);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
// If the loader is invalidated, clear out all the data from the input fields.
        nameEditText.setText("");
        flavorEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing the chocolate.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the chocolate hasn't changed, continue with handling back button press
        if (!chocolateHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_chocolate);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the chocolate.
                deleteChocolate();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the chocolate.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the chocolate in the database.
     */
    private void deleteChocolate() {
        // Only perform the delete if this is an existing chocolate.
        if (currentChocolateUri != null) {
            // Call the ContentResolver to delete the chocolate at the given content URI.
            // Pass in null for the selection and selection args because the currentChocolateUri
            // content URI already identifies the chocolate that we want.
            int rowsDeleted = getContentResolver().delete(currentChocolateUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_chocolate_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_chocolate_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
        finish();
    }
}
