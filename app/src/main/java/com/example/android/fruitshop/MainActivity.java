package com.example.android.fruitshop;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.fruitshop.data.FruitContract;
import com.example.android.fruitshop.data.FruitContract.FruitEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the fruit data loader
     */
    private static final int FRUIT_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    FruitCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the fruit data
        final ListView fruitListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        fruitListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of fruit data in the Cursor.
        // There is no fruit data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new FruitCursorAdapter(this, null);
        fruitListView.setAdapter(mCursorAdapter);


        // Kick off the loader
        getLoaderManager().initLoader(FRUIT_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded fruit data into the database. For debugging purposes only.
     */
    private void insertFruit() {
        // Create a ContentValues object where column names are the keys,
        // and  attributes are the values.
        Uri uri = Uri.parse("android.resource://fruitshop/drawable/apple");
        String DummyImage = uri.toString();
        ContentValues values = new ContentValues();
        values.put(FruitEntry.COL_FRUIT_TYPE, FruitEntry.TYPE_APPLE);
        values.put(FruitEntry.COL_FRUIT_SUPPLIER, "apple@supplies.com");
        values.put(FruitEntry.COL_FRUIT_PRICE, 10);
        values.put(FruitEntry.COL_FRUIT_QUANTITY, 200);
        values.put(FruitEntry.COL_FRUIT_IMAGE, DummyImage);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {CONTENT_URI} to indicate that we want to insert
        // into the fruits database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(FruitEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all fruits in the database.
     */
    private void deleteAllFruits() {
        int rowsDeleted = getContentResolver().delete(FruitEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from fruit database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertFruit();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllFruits();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                FruitEntry._ID,
                FruitEntry.COL_FRUIT_TYPE,
                FruitEntry.COL_FRUIT_SUPPLIER, FruitEntry.COL_FRUIT_PRICE,
                FruitEntry.COL_FRUIT_QUANTITY, FruitEntry.COL_FRUIT_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                FruitEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // with this new cursor containing updated fruit data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    public void onSaleClick(long id, int quantity) {
        Uri currentFruitUri = ContentUris.withAppendedId(FruitContract.FruitEntry.CONTENT_URI, id);
        Log.v("CatalogActivity", "Uri: " + currentFruitUri);
        quantity--;
        if (quantity < 0) {
            quantity = 0;
        }
        ContentValues values = new ContentValues();
        values.put(FruitEntry.COL_FRUIT_QUANTITY, quantity);
        int rowsEffected = getContentResolver().update(currentFruitUri, values, null, null);
    }

    public void onEditClick(long id) {
        // Create new intent to go to {@link EditorActivity}
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        Uri currentFruitUri = ContentUris.withAppendedId(FruitEntry.CONTENT_URI, id);
        // Set the URI on the data field of the intent
        intent.setData(currentFruitUri);
        // Launch the {@link EditorActivity} to display the data for the current fruit.
        startActivity(intent);
    }


}
