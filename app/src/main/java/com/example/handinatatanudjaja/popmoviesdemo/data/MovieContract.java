package com.example.handinatatanudjaja.popmoviesdemo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by handinatatanudjaja on 9/26/15.
 * Defines table and column names for the favorite movie Database
 * Need:
 * - Database table name
 * - Column name
 */
public class MovieContract {


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.handinatatanudjaja.popmoviesdemo.data";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_TRAILER = "movie_trailer";
    public static final String PATH_MOVIE_REVIEW = "movie_review";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class MovieFavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie_favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        //Foreign keys
        //public static final String COLUMN_TRAILER_KEY = "trailer_id";
        //public static final String COLUMN_REVIEW_KEY = "review_id";
        //End Foreign keys

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SMALL_POSTER_PATH = "small_poster_path";
        public static final String COLUMN_BIG_POSTER_PATH = "big_poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVG = "vote_average";
        public static final String COLUMN_FAV_MOVIE_ID = "fav_movie_id";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
    Request:
    http://api.themoviedb.org/3/movie/76341/videos?api_key=4023d68647cd21b2cfb5115acda7fe46

    Response:
    {"id":76341,"results":[
    {"id":"559198cac3a3685710000b58","iso_639_1":"en","key":"FRDdRto_3SA","name":"Trailers From Hell","site":"YouTube","size":1080,"type":"Featurette"},
    {"id":"551afc679251417fd70002b1","iso_639_1":"en","key":"jnsgdqppAYA","name":"Trailer 2","site":"YouTube","size":720,"type":"Trailer"},
    {"id":"548ce4e292514122ed002d99","iso_639_1":"en","key":"YWNWi-ZWL3c","name":"Official Trailer #1","site":"YouTube","size":1080,"type":"Trailer"}]}
     */
    public static final class MovieTrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILER;

        public static final String TABLE_NAME = "movie_trailer";

        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_FAV_MOVIE_ID = "fav_movie_id";
        public static final String COLUMN_TRAILER_KEY = "trailer_key";
    }

    /*
    Request:
    http://api.themoviedb.org/3/movie/76341/reviews?api_key=4023d68647cd21b2cfb5115acda7fe46

    Response:
    {"id":76341,"page":1,"results":[
    {"id":"55660928c3a3687ad7001db1","author":"Phileas Fogg","content":"Fabulous action movie. Lots of interesting characters. They don't make many movies like this. The whole movie from start to finish was entertaining I'm looking forward to seeing it again. I definitely recommend seeing it.","url":"http://j.mp/1HLTNzT"},
    {"id":"55732a53925141456e000639","author":"Andres Gomez","content":"Good action movie with a decent script for the genre. The photography is really good too but, in the end, it is quite repeating itself from beginning to end and the stormy OST is exhausting.","url":"http://j.mp/1dUnvpG"},
    {"id":"55edd26792514106d600e380","author":"extoix","content":"Awesome movie!  WITNESS ME will stick with me forever!","url":"http://j.mp/1hQIOdj"}],
    "total_pages":1,"total_results":3}


     */

    public static final class MovieReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;

        public static final String TABLE_NAME = "movie_review";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_FAV_MOVIE_ID = "fav_movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }
}
