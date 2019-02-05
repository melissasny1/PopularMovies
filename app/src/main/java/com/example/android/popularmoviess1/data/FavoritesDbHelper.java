package com.example.android.popularmoviess1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmoviess1.data.FavoritesContract.FavoritesEntry;

import static com.example.android.popularmoviess1.data.FavoritesContract.FavoritesEntry.TABLE_NAME;

/**
 * SQLite Database helper.
 */

class FavoritesDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "movies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME +
                "( " + FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_AVG_RATING + " DOUBLE NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_SUMMARY + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_API_ID + " INT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_MOVIE_IS_FAVORITE + " INT NOT NULL, " +
                "UNIQUE(" + FavoritesEntry.COLUMN_MOVIE_API_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
