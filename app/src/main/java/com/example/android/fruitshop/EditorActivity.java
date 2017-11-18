package com.example.android.fruitshop;

import android.Manifest;
import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.fruitshop.data.FruitContract.FruitEntry;

/**
 * Created by casab on 14/06/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    /**
     * Identifier for the fruit data loader
     */
    private static final int EXISTING_FRUIT_LOADER = 0;
    String mCurrentPhotoPath;
    Uri imageUri;
    private ImageView mImage;
    /**
     * Content URI for the existing fruit (null if it's a new fruit)
     */
    private Uri mCurrentFruitUri;
    private boolean mMissingField;
    private boolean mFieldsCheck;
    /**
     * EditText field to enter the fruit's name
     */
    private EditText mSupplierEditText;
    //Temporary String for options
    private int mTemporaryQuantity;
    //Temporary String for passing the e-mail to the mail intent
    private String mTemporary_String_Email;
    private String mTemporary_Type_for_Mail;
    /**
     * EditText field to enter the fruit's breed
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the fruit's weight
     */
    private EditText mQuantityEditText;
    private ImageView mTakenPic;

    /**
     * EditText field to enter the fruit's gender
     */
    private Spinner mTypeSpinner;
    private String mFruit = FruitEntry.TYPE_NOTSELECTED;
    /**
     * Boolean flag that keeps track of whether the fruit has been edited (true) or not (false)
     */
    private boolean mFruitHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mfruitHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFruitHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new fruit or editing an existing one.
        Intent intent = getIntent();
        mCurrentFruitUri = intent.getData();
        // FInd the view for the edit options provided to easily manage supplies and e-mail to providers
        LinearLayout options = (LinearLayout) findViewById(R.id.container_edit);
        //Find the Image Button
        Button imageButton = (Button) findViewById(R.id.button_image);
        //Find the Image View
        mImage = (ImageView) findViewById(R.id.custom_image);

        // If the intent DOES NOT contain a fruit content URI, then we know that we are
        // creating a new fruit.
        if (mCurrentFruitUri == null) {
            // This is a new fruit, so change the app bar to say "Add a Fruit"
            setTitle(getString(R.string.editor_activity_title_new_fruit));
            options.setVisibility(View.INVISIBLE);

            // ClickListener forA Adding Picuture
            imageButton.setOnTouchListener(mTouchListener);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                    mFruitHasChanged = true;
                }
            });

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a fruit that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing fruit, so change app bar to say "Edit Fruit"
            setTitle(getString(R.string.editor_activity_title_edit_fruit));
            options.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.INVISIBLE);

            // ClickListener for Increasing Button in Options
            Button increasebutton = (Button) findViewById(R.id.button_shipped);
            increasebutton.setOnTouchListener(mTouchListener);
            increasebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTemporaryQuantity = increaseQuantity(mTemporaryQuantity, mQuantityEditText);
                }
            });

            // ClickListener for Decreasing Button in Options
            Button decreasebutton = (Button) findViewById(R.id.button_sold);
            increasebutton.setOnTouchListener(mTouchListener);
            decreasebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTemporaryQuantity = decreaseQuantity(mTemporaryQuantity, mQuantityEditText);
                }
            });

            // ClickListener for Decreasing Button in Options
            Button contactSupplier = (Button) findViewById(R.id.contact_supplier);
            contactSupplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactSupplier(mTemporary_String_Email, mTemporary_Type_for_Mail);
                }
            });

            // Initialize a loader to read the fruit data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_FRUIT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier);
        mPriceEditText = (EditText) findViewById(R.id.edit_price_fruit);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity_fruit);
        // Update the Global Variable mTemporary_Quantity if the EditText of Quanitty is pressed/changed
        mQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*store the Edited Text in the Global Variable mTemporary used by the + and - buttons, it the user
                delete all the text, sets the values of the mTemporaryQuantity to 0 to avoid system crashes*/
                if ((mQuantityEditText.getText().toString().isEmpty())) {
                    mTemporaryQuantity = 0;
                } else {
                    try {
                        mTemporaryQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                    } catch (NumberFormatException e) {

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_fruit);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the fruit.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter fruitSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_fruit_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        fruitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(fruitSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.apple))) {
                        mFruit = FruitEntry.TYPE_APPLE;
                    } else if (selection.equals(getString(R.string.banana))) {
                        mFruit = FruitEntry.TYPE_BANANA;
                    } else if (selection.equals(getString(R.string.peach))) {
                        mFruit = FruitEntry.TYPE_PEACH;
                    } else if (selection.equals(getString(R.string.pineapple))) {
                        mFruit = FruitEntry.TYPE_PINEAPPLE;
                    } else if (selection.equals(getString(R.string.strawberry))) {
                        mFruit = FruitEntry.TYPE_STRAWBERRY;
                    } else if (selection.equals(getString(R.string.watermelon))) {
                        mFruit = FruitEntry.TYPE_WATERMELON;
                    } else {
                        mFruit = FruitEntry.TYPE_NOTSELECTED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFruit = FruitEntry.TYPE_NOTSELECTED;
            }
        });
    }

    /**
     * Get user input from editor and save fruit into database.
     */
    private boolean saveFruit() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String supplierString = mSupplierEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        // Check if this is supposed to be a new fruit
        // and check if all the fields in the editor are blank
        if (mCurrentFruitUri == null) {

            if (imageUri == null) {
                Toast.makeText(this, getString(R.string.add_picure), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (
                    TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(priceString) &&
                            TextUtils.isEmpty(quantityString) && mFruit == FruitEntry.TYPE_NOTSELECTED) {
                Toast.makeText(this, getString(R.string.empty_field),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (TextUtils.isEmpty(supplierString)) {
                Toast.makeText(this, getString(R.string.supplier_field),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, getString(R.string.price_field),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, getString(R.string.quantity_field),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (mFruit == FruitEntry.TYPE_NOTSELECTED) {
                Toast.makeText(this, getString(R.string.type_field),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        // Create a ContentValues object where column names are the keys,
        // and FRUIT attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(FruitEntry.COL_FRUIT_TYPE, mFruit);
        values.put(FruitEntry.COL_FRUIT_SUPPLIER, supplierString);
        values.put(FruitEntry.COL_FRUIT_PRICE, priceString);
        values.put(FruitEntry.COL_FRUIT_QUANTITY, quantityString);
        values.put(FruitEntry.COL_FRUIT_IMAGE, imageUri.toString());

        // Determine if this is a new or existing fruit by checking if mCurrentfruitUri is null or not
        if (mCurrentFruitUri == null) {
            // This is a NEW fruit, so insert a new fruit into the provider,
            // returning the content URI for the new fruit.
            Uri newUri = getContentResolver().insert(FruitEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_fruit_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_fruit_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            // Otherwise this is an EXISTING fruit, so update the fruit with content URI: mCurrentfruitUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentfruitUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentFruitUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_fruit_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_fruit_successful),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new fruit, hide the "Delete" menu item.
        if (mCurrentFruitUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save fruit to database
                mMissingField = saveFruit();
                // Exit activity
                if (mMissingField) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the fruit hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mFruitHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the fruit hasn't changed, continue with handling back button press
        if (!mFruitHasChanged) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all fruit attributes, define a projection that contains
        // all columns from the fruit table
        String[] projection = {
                FruitEntry._ID,
                FruitEntry.COL_FRUIT_TYPE,
                FruitEntry.COL_FRUIT_SUPPLIER,
                FruitEntry.COL_FRUIT_PRICE,
                FruitEntry.COL_FRUIT_QUANTITY,
                FruitEntry.COL_FRUIT_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentFruitUri,         // Query the content URI for the current fruit
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of fruit attributes that we're interested in
            int typeColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_TYPE);
            int supplierColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_SUPPLIER);
            int priceColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_QUANTITY);
            int pictureColumnIndex = cursor.getColumnIndex(FruitEntry.COL_FRUIT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String type = cursor.getString(typeColumnIndex);
            mTemporary_Type_for_Mail = type;
            String supplier = cursor.getString(supplierColumnIndex);
            mTemporary_String_Email = supplier;
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String imageUriString = cursor.getString(pictureColumnIndex);
            //Temporary Quantity for Gloabal Variable used in Options
            mTemporaryQuantity = quantity;

            // Update the views on the screen with the values from the database
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            imageUri = Uri.parse(imageUriString);
            mImage.setImageURI(imageUri);

            // Gender is a dropdown spinner, so map the constant value from the database
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (type) {
                case FruitEntry.TYPE_APPLE:
                    mTypeSpinner.setSelection(1);
                    break;
                case FruitEntry.TYPE_BANANA:
                    mTypeSpinner.setSelection(2);
                    break;
                case FruitEntry.TYPE_PEACH:
                    mTypeSpinner.setSelection(3);
                    break;
                case FruitEntry.TYPE_PINEAPPLE:
                    mTypeSpinner.setSelection(4);
                    break;
                case FruitEntry.TYPE_STRAWBERRY:
                    mTypeSpinner.setSelection(5);
                    break;
                case FruitEntry.TYPE_WATERMELON:
                    mTypeSpinner.setSelection(6);
                    break;
                case FruitEntry.TYPE_NOTSELECTED:
                    mTypeSpinner.setSelection(0);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mTypeSpinner.setSelection(0); // Select "NOT SELECTED"
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the fruit.
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
     * Prompt the user to confirm that they want to delete this fruit.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the fruit.
                deleteFruit();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the fruit.
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
     * Perform the deletion of the fruit in the database.
     */
    private void deleteFruit() {
        // Only perform the delete if this is an existing fruit.
        if (mCurrentFruitUri != null) {
            // Call the ContentResolver to delete the fruit at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentfruitUri
            // content URI already identifies the fruit that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentFruitUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_fruit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_fruit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    private int increaseQuantity(int quantity, EditText view) {
        int newquantity = quantity + 1;
        // no limits for adding quantity
        view.setText(String.valueOf(newquantity));
        return newquantity;
    }

    private int decreaseQuantity(int quantity, EditText view) {
        int newquantity = quantity - 1;
        if (newquantity < 0) {
            newquantity = 0;
        }
        view.setText(String.valueOf(newquantity));
        return newquantity;
    }

    private void contactSupplier(String mail, String type) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "New Order");
        intent.putExtra(Intent.EXTRA_TEXT, "I want to order more fruits " + type);

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void takePicture() {
        trySelector();
    }

    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                mImage.setImageURI(imageUri);
                mImage.invalidate();
            }
        }
    }


}
