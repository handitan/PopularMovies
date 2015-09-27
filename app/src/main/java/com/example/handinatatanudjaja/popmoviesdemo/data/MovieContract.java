package com.example.handinatatanudjaja.popmoviesdemo.data;

import android.provider.BaseColumns;

/**
 * Created by handinatatanudjaja on 9/26/15.
 * Defines table and column names for the favorite movie Database
 * Need:
 * - Database table name
 * - Column name
 */
public class MovieContract {
    public static final class MovieFavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_favorite";


        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SMALL_POSTER_PATH = "small_poster_path";
        public static final String COLUMN_BIG_POSTER_PATH = "big_poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVG = "vote_average";

    }
}
