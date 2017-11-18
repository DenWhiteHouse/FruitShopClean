package com.example.android.fruitshop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.fruitshop.data.FruitContract.FruitEntry;

/**
 * Created by casab on 13/06/2017.
 */

public class FruitDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = FruitDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "fruitshop11.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link FruitDbHelper}.
     *
     * @param context of the app
     */
    public FruitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the fruits table
        String SQL_CREATE_FRUITS_TABLE = "CREATE TABLE " + FruitEntry.TABLE_NAME + " ("
                + FruitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FruitEntry.COL_FRUIT_TYPE + " TEXT NOT NULL, "
                + FruitEntry.COL_FRUIT_SUPPLIER + " TEXT NOT NULL, "
                + FruitEntry.COL_FRUIT_PRICE + " INTEGER NOT NULL, "
                + FruitEntry.COL_FRUIT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + FruitEntry.COL_FRUIT_IMAGE + " TEXT );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_FRUITS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

