package com.example.handinatatanudjaja.popmoviesdemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.handinatatanudjaja.popmoviesdemo.data.MovieContract.MovieFavoriteEntry;


/**
 * Created by handinatatanudjaja on 9/26/15.
 */
public class MovieDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
        Url test:
        - For getting reviews:
        http://api.themoviedb.org/3/movie/{id}/reviews?api_key=4023d68647cd21b2cfb5115acda7fe46
        http://api.themoviedb.org/3/movie/76341/reviews?api_key=4023d68647cd21b2cfb5115acda7fe46

        - For getting movie trailers:
        http://api.themoviedb.org/3/movie/{id}/videos?api_key=4023d68647cd21b2cfb5115acda7fe46
        http://api.themoviedb.org/3/movie/76341/videos?api_key=4023d68647cd21b2cfb5115acda7fe46

        - To play the movie trailer
        {"id":76341,"results":[{"id":"559198cac3a3685710000b58","iso_639_1":"en","key":"FRDdRto_3SA","name":"Trailers From Hell","site":"YouTube","size":1080,"type":"Featurette"},
        {"id":"551afc679251417fd70002b1","iso_639_1":"en","key":"jnsgdqppAYA","name":"Trailer 2","site":"YouTube","size":720,"type":"Trailer"},
        {"id":"548ce4e292514122ed002d99","iso_639_1":"en","key":"YWNWi-ZWL3c","name":"Official Trailer #1","site":"YouTube","size":1080,"type":"Trailer"}]}

        https://www.youtube.com/watch?v=<key>
        For example: https://www.youtube.com/watch?v=jnsgdqppAYA
         */
        final String SQL_CREATE_MOVIEFAVORITE_TABLE = "CREATE TABLE " + MovieFavoriteEntry.TABLE_NAME + " (" +
                MovieFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MovieFavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                //MovieFavoriteEntry.COLUMN_TRAILER_KEY + " INTEGER NOT NULL, " +
                //MovieFavoriteEntry.COLUMN_REVIEW_KEY + " INTEGER NOT NULL, " +

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


        final String SQL_CREATE_MOVIETRAILER_TABLE = "CREATE TABLE " + MovieContract.MovieTrailerEntry.TABLE_NAME + " (" +
                MovieContract.MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieTrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieTrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                " UNIQUE (" + MovieContract.MovieTrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIEREVIEW_TABLE = "CREATE TABLE " + MovieContract.MovieReviewEntry.TABLE_NAME + " (" +
                MovieContract.MovieReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                MovieContract.MovieReviewEntry.COLUMN_CONTENT + " TEXT, " +
                " UNIQUE (" + MovieContract.MovieReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        //db.execSQL(SQL_CREATE_MOVIETRAILER_TABLE);
        //db.execSQL(SQL_CREATE_MOVIEREVIEW_TABLE);
        db.execSQL(SQL_CREATE_MOVIEFAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieFavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
