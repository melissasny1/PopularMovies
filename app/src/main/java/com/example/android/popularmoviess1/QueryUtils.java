package com.example.android.popularmoviess1;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.android.popularmoviess1.MainActivity.LOG_TAG;

/**
 * Helper methods related to retrieving movies from the Movie Database API.
 */

final class QueryUtils {

    private static final String MOVIE_DB_SCHEME = "https";
    private static final String MOVIE_DB_AUTHORITY = "api.themoviedb.org";
    static final String MOVIE_DB_PATH_POPULAR = "3/movie/popular";
    static final String MOVIE_DB_PATH_TOP_RATED = "3/movie/top_rated";
    private static final String MOVIE_DB_PATH = "3/movie";
    private static final String MOVIE_DB_PATH_REVIEWS = "reviews";
    private static final String MOVIE_DB_PATH_VIDEOS = "videos";
    private static final String MOVIE_DB_API_KEY = "add api key here";
    private static final String QUERY_PARAM = "api_key";
    //To be used by Picasso to display the movie poster image.
    static final String MOVIE_DB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342//";
    private static final String NA = "NA";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * From the Movie Database API, fetch the most popular or top-rated movies, as specified by the
     * user.
     *
     * @param urlPathString The string containing the path, either "popular" or "top_rated",
     *                      for the Movie Database API query.
     * @return The list of movies.
     */
    static List<Movie> fetchMovies(String urlPathString) {
        try {
            //Create the query URL using the path selected by the user.
            //URL queryUrl = buildUrl(urlPathString);
            URL queryUrl = buildUrl(null, urlPathString, null);

            //Perform the network request for movies from the Movie Database API.
            String jsonResponse = getResultsFromHttpRequest(queryUrl);

            //Extract the movie data required to create the Movie objects and return the list
            //of movies to be displayed in the RecyclerView of query results.
            return getMovieInfo(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create the URL for the data request to the Movie Database API.
     *
     * @param stringMovieDbApiId The Movie Database API ID #, as a string, for the selected movie
     * @param movieDbPath The user-selected primary path for the Movie Database API URL.
     * @param appendToPath Additional information to be appended to the URL path, when
     *                     the URL is a request for either reviews or videos for a specific movie.
     *
     * @return The URL for the Movie Database API query.
     */
    private static URL buildUrl(String stringMovieDbApiId, String movieDbPath,
                                    String appendToPath){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIE_DB_SCHEME)
                .authority(MOVIE_DB_AUTHORITY)
                .path(movieDbPath);

        //This step will be included if the user has selected to display either reviews or
        //videos for a specific movie.
        if(stringMovieDbApiId != null){
            builder.appendPath(stringMovieDbApiId)
                    .appendPath(appendToPath);
        }

        builder.appendQueryParameter(QUERY_PARAM, MOVIE_DB_API_KEY);
        Uri queryUri = builder.build();

        URL queryUrl = null;
        try {
            queryUrl = new URL(String.valueOf(queryUri));
        } catch (MalformedURLException e) {
            Log.e(MainActivity.LOG_TAG, "Error creating URL.", e);
        }
        return queryUrl;
    }

    /**
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP query.
     * @throws IOException related to network and stream reading.
     */
    private static String getResultsFromHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static List<Movie> getMovieInfo(String movieJsonString) {
        //Create a list of movie objects to be populated with the data to be extracted from the
        //movieJsonString.
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject movieData = new JSONObject(movieJsonString);
            JSONArray movieArray = movieData.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject individualMovie = movieArray.getJSONObject(i);
                int movieId = individualMovie.optInt("id");
                String posterPath = individualMovie.optString("poster_path", NA);
                String originalTitle = individualMovie.optString("original_title", NA);
                String overview = individualMovie.optString("overview", NA);
                String releaseDate = individualMovie.optString("release_date", NA);
                double voteAverage = individualMovie.optDouble("vote_average");

                //Create a new movie object if the movie has both an id and a relative path to the
                //movie poster image.
                if (movieId != 0 && !posterPath.equalsIgnoreCase(NA)) {
                    movies.add(new Movie(movieId, posterPath, originalTitle,
                            overview, releaseDate, voteAverage));
                }
            }
            return movies;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON data. ", e);
            return null;
        }
    }

    static List<Review> fetchReviews(int movieDbApiId){
        try {
            //Create the review query URL for the movie selected by the user.
            //URL queryUrl = buildSelectedMovieUrl(movieDbApiId, MOVIE_DB_PATH_REVIEWS);
            URL queryUrl = buildUrl(String.valueOf(movieDbApiId), MOVIE_DB_PATH, MOVIE_DB_PATH_REVIEWS);

            //Perform the network request for reviews for the selected movie from the
            //Movie Database API.
            String jsonResponse = getResultsFromHttpRequest(queryUrl);

            //Extract the review data required to create the Review objects and return the list
            //of reviews to be displayed in the movie detail.
            return getReviewInfo(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static List<Trailer> fetchTrailers(int movieDbApiId){
        try {
            //Create the trailer query URL for the movie selected by the user.
            //URL queryUrl = buildSelectedMovieUrl(movieDbApiId, MOVIE_DB_PATH_VIDEOS);
            URL queryUrl = buildUrl(String.valueOf(movieDbApiId), MOVIE_DB_PATH, MOVIE_DB_PATH_VIDEOS);

            //Perform the network request for trailers for the selected movie from the
            //Movie Database API.
            String jsonResponse = getResultsFromHttpRequest(queryUrl);

            //Extract the trailer data required to create the Trailer objects and return the list
            //of trailers to be displayed in the movie detail.
            return getTrailerInfo(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Review> getReviewInfo(String reviewJsonString) {
        //Create a list of review objects to be populated with the data to be extracted from the
        //reviewJsonString.
        List<Review> reviews = new ArrayList<>();

        try {
            JSONObject reviewData = new JSONObject(reviewJsonString);
            JSONArray reviewArray = reviewData.getJSONArray("results");

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject individualReview = reviewArray.getJSONObject(i);
                String reviewAuthor = individualReview.optString("author", NA);
                String reviewContent = individualReview.optString("content", NA);

                //Create a new review object if the review has both an author and a URL.
                if (!reviewAuthor.equalsIgnoreCase(NA) && !reviewContent.equalsIgnoreCase(NA)) {
                    reviews.add(new Review(reviewAuthor, reviewContent));
                }
            }
            return reviews;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON data. ", e);
            return null;
        }
    }

    private static List<Trailer> getTrailerInfo(String trailerJsonString) {
        //Create a list of trailer objects to be populated with the data to be extracted from the
        //trailerJsonString.
        List<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject trailerData = new JSONObject(trailerJsonString);
            JSONArray trailerArray = trailerData.getJSONArray("results");

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject individualTrailer = trailerArray.getJSONObject(i);
                String trailerKey = individualTrailer.optString("key", NA);
                String trailerName = individualTrailer.optString("name", NA);

                //Create a new review object if the review has both an author and a URL.
                if (!trailerKey.equalsIgnoreCase(NA) && !trailerName.equalsIgnoreCase(NA)) {
                    trailers.add(new Trailer(trailerKey, trailerName));
                }
            }
            return trailers;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON data. ", e);
            return null;
        }
    }
}
