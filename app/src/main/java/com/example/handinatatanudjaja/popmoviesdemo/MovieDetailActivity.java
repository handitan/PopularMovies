package com.example.handinatatanudjaja.popmoviesdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handinatatanudjaja.popmoviesdemo.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MovieDetailActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

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

            Random rand = new Random();
            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt((1000 - 1) + 1) + 1;

            //TODO need to change the MOVIE_ID
            movieValues.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID, randomNum);
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
}
