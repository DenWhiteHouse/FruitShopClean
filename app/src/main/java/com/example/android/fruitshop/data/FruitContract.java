package com.example.android.fruitshop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by casab on 13/06/2017.
 */

public final class FruitContract {
    // To prevent someone from accidentally instantiating the contract class,
// give it an empty constructor.
    private FruitContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.fruitshop";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.fruits/fruits/ is a valid path for
     * looking at fruit data. content://com.example.android.fruits/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_FRUITS = "fruitshop";

    /**
     * Inner class that defines constant values for the fruits database table.
     * Each entry in the table represents a single FRUITS.
     */
    public static final class FruitEntry implements BaseColumns {

        /**
         * The content URI to access the fruit data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FRUITS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of FRUITS.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRUITS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single FRUIT.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRUITS;

        /**
         * Name of database table for FRUITS
         */
        public final static String TABLE_NAME = "fruitshop";

        /**
         * Unique ID number for the fruit (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Unique TABLE INFO for the fruits (only for use in the database table).
         * <p>
         * Type: TEXT ( type and supplier ) AND INTEGER ( price and quanity ) ( price is an INTEGER as semplification )
         */
        public final static String COL_FRUIT_TYPE = "type";
        public final static String COL_FRUIT_SUPPLIER = "supplier";
        public final static String COL_FRUIT_PRICE = "price";
        public final static String COL_FRUIT_QUANTITY = "quantity";
        public final static String COL_FRUIT_IMAGE= "image";

        /**
         * Possible values of the type of fruit of the shop
         */
        public static final String TYPE_APPLE = "Apple";
        public static final String TYPE_BANANA = "Banana";
        public static final String TYPE_PEACH = "Peach";
        public static final String TYPE_PINEAPPLE = "Pineapple";
        public static final String TYPE_STRAWBERRY = "Strawberry";
        public static final String TYPE_WATERMELON = "Watermelon";
        public static final String TYPE_NOTSELECTED = "NOT SELECTED";

        /**
         * Returns whether or not the given type is one of the defined ones
         */
        public static boolean isValidType(String type) {
            if (type == TYPE_APPLE || type == TYPE_BANANA || type == TYPE_PEACH || type == TYPE_PINEAPPLE || type == TYPE_STRAWBERRY || type == TYPE_WATERMELON || type==TYPE_NOTSELECTED) {
                return true;
            }
            return false;
        }
    }
}
