package com.example.android.inventoryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ChocolateContract.ChocolateEntry;

/**
 * {@link ChocolateCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of chocolate data as its data source. This adapter knows
 * how to create list items for each row of chocolate data in the {@link Cursor}.
 */
public class ChocolateCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ChocolateCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    ChocolateCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the chocolate data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current chocolate can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //it is possible to us butter knife in this situation ?
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.chocolate_name_textView);
        TextView flavorTextView = view.findViewById(R.id.chocolate_flavor_textView);
        TextView priceTextView = view.findViewById(R.id.chocolate_price_textView);
        final TextView quantityTextView = view.findViewById(R.id.chocolate_quantity_textView);
        ImageView sellImageView = view.findViewById(R.id.sell_imageView);

        //Find the columns of chocolate attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_NAME);
        int flavorColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_FLAVOR);
        int priceColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ChocolateEntry.COLUMN_CHOCOLATE_QUANTITY);

        // Read the chocolate attributes from the Cursor for the current chocolate
        String chocolateName = cursor.getString(nameColumnIndex);
        String chocolateFlavor = cursor.getString(flavorColumnIndex);
        int chocolatePrice = cursor.getInt(priceColumnIndex);
        int chocolateQuantity = cursor.getInt(quantityColumnIndex);

        // If the chocolate flavor is empty string or null, then use some default text
        // that says "Unknown flavor", so the TextView isn't blank.
        if (TextUtils.isEmpty(chocolateFlavor)) {
            chocolateFlavor = context.getString(R.string.unknown_flavor);
        }

        // Update the TextViews with the attributes for the current chocolate
        nameTextView.setText(chocolateName);
        flavorTextView.setText(chocolateFlavor);
        priceTextView.setText(Integer.toString(chocolatePrice));
        quantityTextView.setText(Integer.toString(chocolateQuantity));

        sellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityInteger = Integer.parseInt(quantityTextView.getText().toString().trim());
                if (quantityInteger > 0) {
                    quantityTextView.setText(String.valueOf(quantityInteger - 1));
                } else {
                    Toast.makeText(context, R.string.quantity_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
