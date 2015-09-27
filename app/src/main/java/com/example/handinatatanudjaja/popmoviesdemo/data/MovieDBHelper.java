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

        final String SQL_CREATE_MOVIEFAVORITE_TABLE = "CREATE TABLE " + MovieFavoriteEntry.TABLE_NAME + " (" +
                MovieFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieFavoriteEntry.COLUMN_TITLE + " TEXT UNIQUE NOT NULL," +
                MovieFavoriteEntry.COLUMN_SMALL_POSTER_PATH + " TEXT " +
                MovieFavoriteEntry.COLUMN_BIG_POSTER_PATH + " TEXT " +
                MovieFavoriteEntry.COLUMN_OVERVIEW + " TEXT " +
                MovieFavoriteEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL " +
                MovieFavoriteEntry.COLUMN_VOTE_AVG + " REAL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIEFAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieFavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
