package com.example.android.popularmoviess1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Content Provider for the favorites database.
 */

public class FavoritesProvider extends ContentProvider {

    //Database helper object
    private FavoritesDbHelper mDbHelper;

    /** URI matcher code for the content URI for the favorites table */
    private static final int FAVORITES = 100;

    /** URI matcher code for the content URI for a specific movie in the favorites table */
    private static final int FAVORITE_WITH_API_ID = 101;

    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_FAVORITES,
                FAVORITES);
        sUriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_FAVORITES +
                "/#", FAVORITE_WITH_API_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor favoritesCursor;

        switch(match){
            case FAVORITES:
                favoritesCursor = db.query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_WITH_API_ID:
                selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_API_ID + "=?";
                String id = uri.getPathSegments().get(1);
                selectionArgs = new String[]{id};
                favoritesCursor = db.query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        favoritesCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return favoritesCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch(match){
            case FAVORITES:
                long id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);
                if(id > 0){
                    returnUri = ContentUris
                            .withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI,id);
                } else{
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch(match){
            case FAVORITE_WITH_API_ID:
                selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_API_ID + "=?";
                String id = uri.getPathSegments().get(1);
                selectionArgs = new String[]{id};
                rowsDeleted = db.delete(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    //The update method is not used by the app.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return FavoritesContract.FavoritesEntry.CONTENT_LIST_TYPE;
            case FAVORITE_WITH_API_ID:
                return FavoritesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
