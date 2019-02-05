package com.example.android.popularmoviess1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Helper methods used by MainActivity and DetailActivity.
 */

class SharedHelper {
    /**
     * Helper method to determine whether there is an Internet connection.
     *
     * @return True if there is an Internet connection.
     */
    static boolean hasInternetConnection(Context context) {
        //Get a reference to the Connectivity Manager to check the state of network connectivity.
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network.
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Helper method to make the RecyclerView for either the movie posters, reviews or
     * trailers visible and the error message invisible.
     *
     * @param movieInfo             The RecyclerView to be made visible.
     * @param errorMessage          The error message TextView to be made invisible.
     */
    static void showMovieInfo(RecyclerView movieInfo, TextView errorMessage) {
        //Hide the error message.
        errorMessage.setVisibility(View.INVISIBLE);
        //Make the RecyclerView that contains the movie reviews or trailers visible.
        movieInfo.setVisibility(View.VISIBLE);
    }

    /**
     * Helper method to make the error message View visible and hide the RecyclerView
     * for the movie posters, reviews or trailers.
     *
     * @param messageText       The error message text to be displayed.
     * @param movieInfo         The RecyclerView to be hidden.
     * @param errorMessage      The error message TextView to be made visible.
     */
    static void showErrorMessage(String messageText, RecyclerView movieInfo,
                                  TextView errorMessage) {
        //Hide the RecyclerView that contains the movie info.
        movieInfo.setVisibility(View.INVISIBLE);
        //Make the error message visible.
        errorMessage.setText(messageText);
        errorMessage.setVisibility(View.VISIBLE);
    }
}
