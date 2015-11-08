package com.example.handinatatanudjaja.popmoviesdemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private Intent intent;
    private Context currentContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        currentContext = this;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        if (intent != null) {
            String moviePosterUrl = intent.getStringExtra("bigposter");

            TextView titleView = (TextView) findViewById(R.id.movietitle);
            titleView.setText(intent.getStringExtra("title"));

            ImageView posterImageView = (ImageView) findViewById(R.id.moviepic);
            Picasso.with(getApplicationContext()).load(moviePosterUrl).into(posterImageView);

            TextView releaseDateView = (TextView) findViewById(R.id.moviereleasedate);
            releaseDateView.setText(intent.getStringExtra("releasedate"));

            TextView voteavgView = (TextView) findViewById(R.id.movievoteavg);
            voteavgView.setText(intent.getStringExtra("voteaverage").concat("/10.0"));

            TextView overviewView = (TextView) findViewById(R.id.movieoverview);
            overviewView.setText(intent.getStringExtra("overview"));

            new FetchTrailersTask().execute(intent.getStringExtra("id"));
            new FetchReviewsTask().execute(intent.getStringExtra("id"));
            Button markFavoriteBtn = (Button) findViewById(R.id.moviemarkfavorite);
            markFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Perform action click
                    addMovieFavorite();
                }
            });


            Button markFavoriteCheckBtn = (Button) findViewById(R.id.moviefavoriteCheck);
            markFavoriteCheckBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Perform action click
                    favoriteCheck();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

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
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void favoriteCheck() {

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
    }
    //Look at FetchWeatherTask
    private void addMovieFavorite() {

        if (intent != null) {

            String[] mProjection = {MovieContract.MovieFavoriteEntry.COLUMN_TITLE};

            // Defines a string to contain the selection clause
            String mSelectionClause = null;
            mSelectionClause = MovieContract.MovieFavoriteEntry.COLUMN_TITLE + " = ?";

            // Initializes an array to contain selection arguments
            String[] mSelectionArgs = {""};
            mSelectionArgs[0] = intent.getStringExtra("title");

            Cursor retCursor = getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, null);
            if (retCursor.getCount() == 0) {
                Toast.makeText(getApplicationContext(),"Saving to favorite",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"It's already a favorite",Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID, intent.getStringExtra("id"));
            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_TITLE, intent.getStringExtra("title"));

            //TODO poster path
            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_SMALL_POSTER_PATH, intent.getStringExtra("bigposter"));
            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_BIG_POSTER_PATH, intent.getStringExtra("bigposter"));

            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW, intent.getStringExtra("overview"));

            String movReleaseDate = intent.getStringExtra("releasedate");
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            long movieMlliseconds;
            try {
                Date d = f.parse(movReleaseDate);
                movieMlliseconds = d.getTime();
            }
            catch (Exception e){
                movieMlliseconds = new Date().getTime();
            }

            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE, movieMlliseconds);
            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_VOTE_AVG, intent.getStringExtra("voteaverage"));

            getContentResolver().insert(MovieContract.MovieFavoriteEntry.CONTENT_URI, movieValues);

        }



        /*
        public static final String COLUMN_MOVIE_ID = "movie_id";
        //Foreign keys

        //End Foreign keys

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SMALL_POSTER_PATH = "small_poster_path";
        public static final String COLUMN_BIG_POSTER_PATH = "big_poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVG = "vote_average";

        "CREATE TABLE " + MovieFavoriteEntry.TABLE_NAME + " (" +
                MovieFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MovieFavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MovieFavoriteEntry.COLUMN_TITLE + " TEXT UNIQUE NOT NULL," +
                MovieFavoriteEntry.COLUMN_SMALL_POSTER_PATH + " TEXT," +
                MovieFavoriteEntry.COLUMN_BIG_POSTER_PATH + " TEXT," +
                MovieFavoriteEntry.COLUMN_OVERVIEW + " TEXT," +
                MovieFavoriteEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL," +
                MovieFavoriteEntry.COLUMN_VOTE_AVG + " REAL, " +

                //Set up the trailer and review column as both foreign keys
                //" FOREIGN KEY (" + MovieFavoriteEntry.COLUMN_TRAILER_KEY + ") REFERENCES " +
                //MovieTrailerEntry.TABLE_NAME + " (" + MovieTrailerEntry.COLUMN_KEY + "), " +
                //" FOREIGN KEY (" + MovieFavoriteEntry.COLUMN_REVIEW_KEY + ") REFERENCES " +
                //MovieReviewEntry.TABLE_NAME + " (" + MovieReviewEntry.COLUMN_KEY + "), " +

                //Make sure movie favorite to just have one entry per column title
                " UNIQUE (" + MovieFavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
         */



    }

    private class FetchReviewsTask extends AsyncTask<String, Void, MovieReviewItem[]> {

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

            String movieID = params[0];

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

            if (pMovieReviewAry.length > 0) {
                LinearLayout movieReviewLayout = (LinearLayout) findViewById(R.id.moviereviews);

                for (MovieReviewItem pMovieReview : pMovieReviewAry) {
                    TextView txtViewContent = new TextView(currentContext);
                    txtViewContent.setText(pMovieReview.getContent());
                    movieReviewLayout.addView(txtViewContent);
                }

                movieReviewLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    private class FetchTrailersTask extends AsyncTask<String, Void, MovieTrailerItem[]> {

        private MovieTrailerItem[] getMovieTrailerDataFromJSON(String pMovieJsonStr) throws JSONException {
            JSONObject jsonTemp = new JSONObject(pMovieJsonStr);
            JSONArray movieAry = jsonTemp.getJSONArray("results");

            MovieTrailerItem[] movieTrailerAry = null;
            if (movieAry != null && movieAry.length() > 0) {
                movieTrailerAry = new MovieTrailerItem[movieAry.length()];

                for (int i = 0; i < movieAry.length(); ++i) {
                    JSONObject movie = movieAry.getJSONObject(i);
                    MovieTrailerItem movieTrailerObj = new MovieTrailerItem();
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

            String movieID = params[0];

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

            LinearLayout movieTrailerLayout = (LinearLayout) findViewById(R.id.movietrailers);

            if (pMovietrailerAry.length > 0) {
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
