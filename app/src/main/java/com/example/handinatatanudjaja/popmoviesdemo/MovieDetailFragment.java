package com.example.handinatatanudjaja.popmoviesdemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handinatatanudjaja.popmoviesdemo.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private Intent intent;
    private Context currentContext;
    private MovieTrailerItem[] mMovieTrailers;
    private MovieReviewItem[] mMovieReviews;

    private View _rootView;

    public final static String MOVIE_DETAIL_PARCEL = "movieDetailParcel";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentContext = getActivity();
        _rootView = inflater.inflate(R.layout.activity_movie_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            intent = arguments.getParcelable(MOVIE_DETAIL_PARCEL);
        }
        //ORI intent = getIntent();

        if (intent != null) {
            String moviePosterUrl = intent.getStringExtra("bigposter");

            TextView titleView = (TextView) _rootView.findViewById(R.id.movietitle);
            titleView.setText(intent.getStringExtra("title"));

            ImageView posterImageView = (ImageView) _rootView.findViewById(R.id.moviepic);
            Picasso.with(getActivity().getApplicationContext()).load(moviePosterUrl).into(posterImageView);

            TextView releaseDateView = (TextView) _rootView.findViewById(R.id.moviereleasedate);
            releaseDateView.setText(intent.getStringExtra("releasedate"));

            TextView voteavgView = (TextView) _rootView.findViewById(R.id.movievoteavg);
            voteavgView.setText(intent.getStringExtra("voteaverage").concat("/10.0"));

            TextView overviewView = (TextView) _rootView.findViewById(R.id.movieoverview);
            overviewView.setText(intent.getStringExtra("overview"));

            int selectedSortBy = intent.getIntExtra("selectedSortBy", 0);

            if (selectedSortBy == 2) {
                loadTrailers();
                loadReviews();
            } else {
                new FetchTrailersTask().execute(intent.getStringExtra("id"));
                new FetchReviewsTask().execute(intent.getStringExtra("id"));
            }

            Button markFavoriteBtn = (Button) _rootView.findViewById(R.id.moviemarkfavorite);
            markFavoriteBtn.setVisibility(View.VISIBLE);
            markFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Perform action click
                    addMovieFavorite();
                }
            });
        }

        return _rootView;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Batman getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int homeId = android.R.id.home;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else
        if (id == android.R.id.home) {
            //Batman onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /*private void favoriteCheck() {

        String[] mProjection = {MovieContract.MovieFavoriteEntry.COLUMN_TITLE};

        // Defines a string to contain the selection clause
        String mSelectionClause = null;
        mSelectionClause = MovieContract.MovieFavoriteEntry.COLUMN_TITLE + " = ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = intent.getStringExtra("title");

        Cursor retCursor = getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, null);
        if (retCursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(),"NO FAVORITE YET",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"ALREADY FAV LAAA",Toast.LENGTH_SHORT).show();
        }
    }*/

    private void loadTrailers() {

        String[] mProjection = {MovieContract.MovieTrailerEntry.COLUMN_TRAILER_ID,MovieContract.MovieTrailerEntry.COLUMN_FAV_MOVIE_ID,MovieContract.MovieTrailerEntry.COLUMN_TRAILER_KEY};

        // Defines a string to contain the selection clause
        String mSelectionClause = null;
        mSelectionClause = MovieContract.MovieTrailerEntry.COLUMN_FAV_MOVIE_ID + " = ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = intent.getStringExtra("id");

        Cursor retCursor = getActivity().getContentResolver().query(MovieContract.MovieTrailerEntry.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, null);

        if (retCursor.getCount() == 0) {
            //No trailers available
            return;
        }

        MovieTrailerItem[] movieTrailerAry = new MovieTrailerItem[retCursor.getCount()];
        int movieAryIndex = 0;
        retCursor.moveToFirst();
        while (true) {

            Boolean isLastRecord = retCursor.isLast();

            MovieTrailerItem tempTrailerItem = new MovieTrailerItem();

            int colIndex = retCursor.getColumnIndex(MovieContract.MovieTrailerEntry.COLUMN_FAV_MOVIE_ID);
            String colVal  = retCursor.getString(colIndex);
            tempTrailerItem.setTrailerID(colVal);

            colIndex = retCursor.getColumnIndex(MovieContract.MovieTrailerEntry.COLUMN_TRAILER_ID);
            colVal  = retCursor.getString(colIndex);
            tempTrailerItem.setMovieID(colVal);

            colIndex = retCursor.getColumnIndex(MovieContract.MovieTrailerEntry.COLUMN_TRAILER_KEY);
            colVal  = retCursor.getString(colIndex);
            tempTrailerItem.setKey(colVal);

            movieTrailerAry[movieAryIndex] = tempTrailerItem;
            ++movieAryIndex;
            if (!isLastRecord) {
                retCursor.moveToNext();
            }
            else {
                break;
            }
        }

        LinearLayout movieTrailerLayout = (LinearLayout) _rootView.findViewById(R.id.movietrailers);

        for (MovieTrailerItem pItem : movieTrailerAry) {
                ImageView imageView = new ImageView(currentContext);

                //imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                //http://img.youtube.com/vi/bvu-zlR5A8Q/default.jpg

                final String movieTrailerKey = pItem.getKey();

                String movieTrailerUri = "http://img.youtube.com/vi";
                Uri uriTemp = Uri.parse(movieTrailerUri)
                        .buildUpon()
                        .appendPath(movieTrailerKey)
                        .appendPath("default.jpg")
                        .build();
                String movieTrailerUriStr = uriTemp.toString();

                Picasso.with(currentContext)
                        .load(movieTrailerUriStr)
                        .resize(200, 200)
                        .into(imageView);
                movieTrailerLayout.addView(imageView);

                imageView.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + movieTrailerKey));
                        startActivity(intent);
                    }
                });
        }

        movieTrailerLayout.setVisibility(View.VISIBLE);
    }

    private void createReviewUI(MovieReviewItem[] pMovieReviewAry) {
        LinearLayout movieReviewLayout = (LinearLayout) _rootView.findViewById(R.id.moviereviews);

        for (MovieReviewItem pMovieReview : pMovieReviewAry) {
            TextView txtViewContent = new TextView(currentContext);
            txtViewContent.setText(pMovieReview.getContent());

            TextView txtViewAuthor = new TextView(currentContext);
            txtViewAuthor.setText("Reviewer: " + pMovieReview.getAuthor());
            movieReviewLayout.addView(txtViewContent);
            movieReviewLayout.addView(txtViewAuthor);

            View reviewDivider = new View(currentContext);
            movieReviewLayout.addView(reviewDivider);
            reviewDivider.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            reviewDivider.getLayoutParams().height = 1;
            reviewDivider.setBackgroundColor(getResources().getColor(R.color.primary_dark_material_dark));
        }

        movieReviewLayout.setVisibility(View.VISIBLE);
    }

    private void loadReviews() {
        String[] mProjection = {MovieContract.MovieReviewEntry.COLUMN_AUTHOR,MovieContract.MovieReviewEntry.COLUMN_CONTENT,
                                MovieContract.MovieReviewEntry.COLUMN_REVIEW_ID,MovieContract.MovieReviewEntry.COLUMN_FAV_MOVIE_ID};

        // Defines a string to contain the selection clause
        String mSelectionClause = null;
        mSelectionClause = MovieContract.MovieReviewEntry.COLUMN_FAV_MOVIE_ID + " = ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = intent.getStringExtra("id");

        Cursor retCursor = getActivity().getContentResolver().query(MovieContract.MovieReviewEntry.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, null);

        if (retCursor.getCount() == 0) {
            //No trailers available
            return;
        }

        MovieReviewItem[] movieReviewAry = new MovieReviewItem[retCursor.getCount()];
        int movieAryIndex = 0;
        retCursor.moveToFirst();
        while (true) {

            Boolean isLastRecord = retCursor.isLast();

            MovieReviewItem tempReviewItem = new MovieReviewItem();

            int colIndex = retCursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_AUTHOR);
            String colVal  = retCursor.getString(colIndex);
            tempReviewItem.setAuthor(colVal);

            colIndex = retCursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_CONTENT);
            colVal  = retCursor.getString(colIndex);
            tempReviewItem.setContent(colVal);

            colIndex = retCursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_REVIEW_ID);
            colVal  = retCursor.getString(colIndex);
            tempReviewItem.setReviewID(colVal);

            colIndex = retCursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_FAV_MOVIE_ID);
            colVal  = retCursor.getString(colIndex);
            tempReviewItem.setMovieID(colVal);

            movieReviewAry[movieAryIndex] = tempReviewItem;
            ++movieAryIndex;
            if (!isLastRecord) {
                retCursor.moveToNext();
            }
            else {
                break;
            }
        }

        createReviewUI(movieReviewAry);
    }
    //Look at FetchWeatherTask
    private void addMovieFavorite() {

        LinearLayout movieTrailerLayout = (LinearLayout) getActivity().findViewById(R.id.movietrailers);
        LinearLayout movieReviewLayout = (LinearLayout) getActivity().findViewById(R.id.moviereviews);

        try {
            if (intent != null) {

                String[] mProjection = {MovieContract.MovieFavoriteEntry.COLUMN_TITLE};

                // Defines a string to contain the selection clause
                String mSelectionClause = null;
                mSelectionClause = MovieContract.MovieFavoriteEntry.COLUMN_TITLE + " = ?";

                // Initializes an array to contain selection arguments
                String[] mSelectionArgs = {""};
                mSelectionArgs[0] = intent.getStringExtra("title");

                Cursor retCursor = getActivity().getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, null);
                if (retCursor.getCount() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Saving it as a favorite", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "It's already a favorite", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID, intent.getStringExtra("id"));
                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_TITLE, intent.getStringExtra("title"));

                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_SMALL_POSTER_PATH, intent.getStringExtra("bigposter"));
                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_BIG_POSTER_PATH, intent.getStringExtra("bigposter"));

                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW, intent.getStringExtra("overview"));

                String movReleaseDate = intent.getStringExtra("releasedate");
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                long movieMlliseconds;
                try {
                    Date d = f.parse(movReleaseDate);
                    movieMlliseconds = d.getTime();
                } catch (Exception e) {
                    movieMlliseconds = new Date().getTime();
                }

                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE, movieMlliseconds);
                movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_VOTE_AVG, intent.getStringExtra("voteaverage"));

                getActivity().getContentResolver().insert(MovieContract.MovieFavoriteEntry.CONTENT_URI, movieValues);

                //For movie trailers
            /*
               MovieContract.MovieTrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MovieContract.MovieTrailerEntry.COLUMN_FAV_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieTrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +

             */
                if (mMovieTrailers != null && mMovieTrailers.length > 0) {
                    //Do the bulk insert
                    ContentValues[] trailerValuesAry = new ContentValues[mMovieTrailers.length];

                    for (int i = 0; i < mMovieTrailers.length; ++i) {
                        MovieTrailerItem tempTrailer = mMovieTrailers[i];
                        ContentValues trailerValues = new ContentValues();
                        trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_TRAILER_ID, tempTrailer.getTrailerID());
                        trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_FAV_MOVIE_ID, tempTrailer.getMovieID());
                        trailerValues.put(MovieContract.MovieTrailerEntry.COLUMN_TRAILER_KEY, tempTrailer.getKey());
                        trailerValuesAry[i] = trailerValues;
                    }

                    getActivity().getContentResolver().bulkInsert(MovieContract.MovieTrailerEntry.CONTENT_URI, trailerValuesAry);
                }

                //For movie reviews
            /*
            MovieContract.MovieReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MovieContract.MovieReviewEntry.COLUMN_FAV_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                MovieContract.MovieReviewEntry.COLUMN_CONTENT + " TEXT, " +
             */
                if (mMovieReviews != null && mMovieReviews.length > 0) {
                    ContentValues[] reviewValuesAry = new ContentValues[mMovieReviews.length];
                    for (int i = 0; i < mMovieReviews.length; ++i) {
                        MovieReviewItem tempReview = mMovieReviews[i];
                        ContentValues reviewValues = new ContentValues();
                        reviewValues.put(MovieContract.MovieReviewEntry.COLUMN_REVIEW_ID, tempReview.getReviewID());
                        reviewValues.put(MovieContract.MovieReviewEntry.COLUMN_FAV_MOVIE_ID, tempReview.getMovieID());
                        reviewValues.put(MovieContract.MovieReviewEntry.COLUMN_AUTHOR, tempReview.getAuthor());
                        reviewValues.put(MovieContract.MovieReviewEntry.COLUMN_CONTENT, tempReview.getContent());
                        reviewValuesAry[i] = reviewValues;
                    }

                    getActivity().getContentResolver().bulkInsert(MovieContract.MovieReviewEntry.CONTENT_URI, reviewValuesAry);
                }
            }
        }catch(Exception e) {
            Log.e(LOG_TAG, "Error in addMovieFavorite", e);
        }
    }

    private class FetchReviewsTask extends AsyncTask<String, Void, MovieReviewItem[]> {

        private String movieID = "";

        private MovieReviewItem[] getMovieReviewDataFromJSON(String pMovieJsonStr) throws JSONException {
            JSONObject jsonTemp = new JSONObject(pMovieJsonStr);
            JSONArray movieAry = jsonTemp.getJSONArray("results");

            MovieReviewItem[] movieReviewAry = null;
            if (movieAry != null && movieAry.length() > 0) {
                movieReviewAry = new MovieReviewItem[movieAry.length()];

                for (int i = 0; i < movieAry.length(); ++i) {
                    JSONObject movie = movieAry.getJSONObject(i);
                    MovieReviewItem movieReviewObj = new MovieReviewItem();
                    movieReviewObj.setAuthor(movie.getString("author"));
                    movieReviewObj.setContent(movie.getString("content"));
                    movieReviewObj.setMovieID(movieID);
                    movieReviewObj.setReviewID(movie.getString("id"));
                    movieReviewAry[i] = movieReviewObj;
                }
            }
            return movieReviewAry;
        }

        @Override
        protected MovieReviewItem[] doInBackground(String... params) {
            //http://api.themoviedb.org/3/movie/135397/reviews?api_key=4023d68647cd21b2cfb5115acda7fe46
            if (params.length == 0) {
                return null;
            }

            movieID = params[0];

            String movieUri = "http://api.themoviedb.org/3/movie";
            Uri uriTemp = Uri.parse(movieUri)
                    .buildUpon()
                    .appendPath(movieID)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", MovieConstants.movieAPIKey)
                    .build();
            String uriStr = uriTemp.toString();

            //Todos:
            //- Connect the url and get the data
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                URL url = new URL(uriStr);
                // Create the request to Movie db, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie reviews JSON String: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                return null;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            MovieReviewItem[] trailerStringAry = null;
            try {
                trailerStringAry = getMovieReviewDataFromJSON(moviesJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in parsing JSON", e);
            }
            return trailerStringAry;
        }

        @Override
        protected void onPostExecute(MovieReviewItem[] pMovieReviewAry) {

            mMovieReviews = pMovieReviewAry;
            if (pMovieReviewAry != null && pMovieReviewAry.length > 0) {
                createReviewUI(pMovieReviewAry);
            }
        }
    }


    private class FetchTrailersTask extends AsyncTask<String, Void, MovieTrailerItem[]> {

        private String movieID = "";

        private MovieTrailerItem[] getMovieTrailerDataFromJSON(String pMovieJsonStr) throws JSONException {
            JSONObject jsonTemp = new JSONObject(pMovieJsonStr);
            JSONArray movieAry = jsonTemp.getJSONArray("results");

            MovieTrailerItem[] movieTrailerAry = null;
            if (movieAry != null && movieAry.length() > 0) {
                movieTrailerAry = new MovieTrailerItem[movieAry.length()];

                for (int i = 0; i < movieAry.length(); ++i) {
                    JSONObject movie = movieAry.getJSONObject(i);
                    MovieTrailerItem movieTrailerObj = new MovieTrailerItem();
                    movieTrailerObj.setMovieID(movieID);
                    movieTrailerObj.setTrailerID(movie.getString("id"));
                    movieTrailerObj.setKey(movie.getString("key"));
                    movieTrailerAry[i] = movieTrailerObj;
                }
            }
            return movieTrailerAry;
        }

        @Override
        protected MovieTrailerItem[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            movieID = params[0];

            String movieUri = "http://api.themoviedb.org/3/movie";
            Uri uriTemp = Uri.parse(movieUri)
                    .buildUpon()
                    .appendPath(movieID)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", MovieConstants.movieAPIKey)
                    .build();
            String uriStr = uriTemp.toString();

            //Todos:
            //- Connect the url and get the data
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                URL url = new URL(uriStr);
                // Create the request to Movie db, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie trailer JSON String: " + moviesJsonStr);
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                return null;
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            MovieTrailerItem[] trailerStringAry = null;
            try {
                trailerStringAry = getMovieTrailerDataFromJSON(moviesJsonStr);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error in parsing JSON", e);
            }
            return trailerStringAry;
        }

        @Override
        protected void onPostExecute(MovieTrailerItem[] pMovietrailerAry) {

            mMovieTrailers = pMovietrailerAry;
            LinearLayout movieTrailerLayout = (LinearLayout) _rootView.findViewById(R.id.movietrailers);

            if (pMovietrailerAry != null && pMovietrailerAry.length > 0) {
                //LinearLayout movieTrailerLayout = new LinearLayout(currentContext);
                //movieTrailerLayout.setOrientation(LinearLayout.HORIZONTAL);

                //LinearLayout.LayoutParams myLinearLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                //        ViewGroup.LayoutParams.WRAP_CONTENT);

                for (MovieTrailerItem pItem : pMovietrailerAry) {
                    ImageView imageView = new ImageView(currentContext);

                    //imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setAdjustViewBounds(true);
                    //http://img.youtube.com/vi/bvu-zlR5A8Q/default.jpg

                    final String movieTrailerKey = pItem.getKey();

                    String movieTrailerUri = "http://img.youtube.com/vi";
                    Uri uriTemp = Uri.parse(movieTrailerUri)
                            .buildUpon()
                            .appendPath(movieTrailerKey)
                            .appendPath("default.jpg")
                            .build();
                    String movieTrailerUriStr = uriTemp.toString();

                    Picasso.with(currentContext)
                            .load(movieTrailerUriStr)
                            .resize(200, 200)
                            .into(imageView);
                    movieTrailerLayout.addView(imageView);

                    imageView.setOnClickListener(new ImageView.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + movieTrailerKey));
                            startActivity(intent);
                        }
                    });
                }

                movieTrailerLayout.setVisibility(View.VISIBLE);

            }

        }
      }


}
