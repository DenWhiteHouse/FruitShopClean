package com.example.android.fruitshop;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.fruitshop.data.FruitContract;
import com.example.android.fruitshop.data.FruitContract.FruitEntry;

import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_APPLE;
import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_BANANA;
import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_NOTSELECTED;
import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_PEACH;
import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_PINEAPPLE;
import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_STRAWBERRY;
import static com.example.android.fruitshop.data.FruitContract.FruitEntry.TYPE_WATERMELON;

/**
 * Created by casab on 13/06/2017.
 */

public class FruitCursorAdapter extends CursorAdapter {

    private MainActivity activity = new MainActivity();

    //Constructor
    public FruitCursorAdapter(MainActivity context, Cursor c) {
        super(context, c);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int temporaryQuantity;
        final long id;
        // get the Id
        id =cursor.getLong(cursor.getColumnIndex(FruitContract.FruitEntry._ID));
        // Find individual views that we want to modify in the list item layout
        TextView typeTextView = (TextView) view.findViewById(R.id.name);
        TextView supplierTextView = (TextView) view.findViewById(R.id.supplier);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView imageImageView=(ImageView) view.findViewById(R.id.image);
        Button salesButton=(Button) view.findViewById(R.id.sale_button);

        // Find the columns of fruit attributes that we're interested in
        int typeColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_TYPE);
        int supplierColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_SUPPLIER);
        int priceColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_QUANTITY);

        // Read the fruit attributes from the Cursor for the current fruit
        String fruitType = cursor.getString(typeColumnIndex);
        String fruitSupplier = cursor.getString(supplierColumnIndex);
        int fruitPrice = cursor.getInt(priceColumnIndex);
        final int fruitQuantity = cursor.getInt(quantityColumnIndex);
        temporaryQuantity=fruitQuantity;

        // Select the Image associated to the type to pass
        switch (fruitType){
            case TYPE_NOTSELECTED: imageImageView.setImageResource(R.drawable.noselected);
                break;
            case TYPE_APPLE: imageImageView.setImageResource(R.drawable.apple);
                break;
            case TYPE_BANANA: imageImageView.setImageResource(R.drawable.banana);
                break;
            case TYPE_PEACH: imageImageView.setImageResource(R.drawable.peach);
                break;
            case TYPE_PINEAPPLE: imageImageView.setImageResource(R.drawable.pineapple);
                break;
            case TYPE_STRAWBERRY: imageImageView.setImageResource(R.drawable.strawberry);
                break;
            case TYPE_WATERMELON: imageImageView.setImageResource(R.drawable.watermelon);
                break;
            default: imageImageView.setImageResource(R.drawable.ic_add_white);
        }

        // Update the TextViews with the attributes for the current fruit
        typeTextView.setText(fruitType);
        supplierTextView.setText(fruitSupplier);
        priceTextView.setText(String.valueOf(fruitPrice));
        quantityTextView.setText(String.valueOf(fruitQuantity));

        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               activity.onSaleClick(id,temporaryQuantity);
            }
        });

        imageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onEditClick(id);
            }
                                          });
    }
}