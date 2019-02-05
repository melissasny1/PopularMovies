package com.example.android.popularmoviess1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract specifics for the SQLite Database that will store favorite movie data.
 */

public final class FavoritesContract {

    static final String CONTENT_AUTHORITY = "com.example.android.popularmoviess1";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_FAVORITES = "favorites";

    private FavoritesContract(){}

    public static final class FavoritesEntry implements BaseColumns{

        final static String TABLE_NAME = "favorites";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public final static String COLUMN_MOVIE_AVG_RATING = "averageRating";
        public final static String COLUMN_MOVIE_SUMMARY = "plotSummary";
        public final static String COLUMN_MOVIE_API_ID = "movieApiId";
        public final static String COLUMN_MOVIE_POSTER_IMAGE_PATH = "moviePosterPath";
        public final static String COLUMN_MOVIE_IS_FAVORITE = "isFavorite";

        //Create a full URI for the class
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        //The MIME type for a list of favorites.
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_FAVORITES;

        //The MIME type for a single favorite.
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_FAVORITES;
    }
}
