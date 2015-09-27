package com.example.handinatatanudjaja.popmoviesdemo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    private ImageAdapter posterImagesAdapter;

    private final String MOVIE_PARCEL = "movieItemParcel";
    private final int SORT_MOST_POPULAR = 0;
    private final int SORT_HIGHEST_RATED = 1;
    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private final String INACTIVE_NETWORK_WARNING = "Unable to retrieve movies due to no network connectivity.";

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(LOG_TAG, ">>> Calling onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, ">>>>>>>>> Calling onStart()");
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_movie, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        int id = item.getItemId();
        int selectedSortBy = -1;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);


        if (id == R.id.action_popular_movies) {
            selectedSortBy = SORT_MOST_POPULAR;
            //new FetchImageTask().execute(selectedSortBy);
        }
        else
        if (id == R.id.action_highest_rated_movies) {
            selectedSortBy = SORT_HIGHEST_RATED;
            //new FetchImageTask().execute(selectedSortBy);
        }

        if (selectedSortBy != -1) {

            Log.i(LOG_TAG, "Option item selected sort by " + selectedSortBy);
            if (isNetWorkAvailable()) {
                new FetchImageTask().execute(selectedSortBy);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.preference_file_key), selectedSortBy);
                editor.commit();
                return true;
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), INACTIVE_NETWORK_WARNING, Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(LOG_TAG,"========== Calling onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        GridView movieGridView = (GridView) rootView.findViewById(R.id.gridview_movie);

        posterImagesAdapter = new ImageAdapter(container.getContext());
        movieGridView.setAdapter(posterImagesAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MovieItem movieObj = (MovieItem) posterImagesAdapter.getMovieDataAt(position);
                    Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class)
                            .putExtra("title", movieObj.getmTitle())
                            .putExtra("bigposter", movieObj.getmBigPoster())
                            .putExtra("releasedate", movieObj.getmReleaseDate())
                            .putExtra("voteaverage", movieObj.getmVoteAverage())
                            .putExtra("overview", movieObj.getmOverView());
                    startActivity(detailIntent);
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        });

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int sortByDefaultValue = SORT_MOST_POPULAR;
        int sortByValue = sharedPref.getInt(getString(R.string.preference_file_key), sortByDefaultValue);

        if (savedInstanceState != null) {
            ArrayList<MovieItem> listOfThumbnails = posterImagesAdapter.getmMovieList();
            listOfThumbnails.clear();
            posterImagesAdapter.setmMovieList(null);

            ArrayList<MovieItem> moviesParcel = savedInstanceState.getParcelableArrayList(MOVIE_PARCEL);
            posterImagesAdapter.setmMovieList(moviesParcel);
            posterImagesAdapter.notifyDataSetChanged();
        }
        else {
            if (isNetWorkAvailable()) {
                new FetchImageTask().execute(sortByValue);
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), INACTIVE_NETWORK_WARNING, Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MOVIE_PARCEL, posterImagesAdapter.getmMovieList());
    }

    //Privates

    /*private boolean canCallFetchImageTask(int pSortByValue,Bundle pSavedInstanceState) {
        if (isNetWorkAvailable()) {
            if (pSavedInstanceState != null) {
                ArrayList<MovieItem> listOfThumbnails = posterImagesAdapter.getmMovieList();
                listOfThumbnails.clear();
                posterImagesAdapter.setmMovieList(null);

                ArrayList<MovieItem> moviesParcel = pSavedInstanceState.getParcelableArrayList(MOVIE_PARCEL);
                posterImagesAdapter.setmMovieList(moviesParcel);
                posterImagesAdapter.notifyDataSetChanged();
            }
            else {
                new FetchImageTask().execute(pSortByValue);
            }
            return true;
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to retrieve movies due to no network connectivity.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }*/

    private class FetchImageTask extends AsyncTask<Integer, Void, MovieItem[]> {

        final String LOG_TAG = MovieFragment.class.getSimpleName();

        private MovieItem[] getPosterThumbnailsDataFromJSON(String pMovieJsonStr) throws JSONException{

            JSONObject jsonTemp = new JSONObject(pMovieJsonStr);
            JSONArray movieAry = jsonTemp.getJSONArray("results");

            MovieItem[] photoStringAry = null;
            if (movieAry != null && movieAry.length() > 0) {
                photoStringAry = new MovieItem[movieAry.length()];

                for (int i = 0; i < movieAry.length(); ++i) {
                    JSONObject movie = movieAry.getJSONObject(i);
                    MovieItem movieDetailObj = new MovieItem();

                    movieDetailObj.setmTitle(movie.getString("title"));
                    movieDetailObj.setmSmallPoster("http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path"));
                    movieDetailObj.setmBigPoster("http://image.tmdb.org/t/p/w500/" + movie.getString("poster_path"));
                    movieDetailObj.setmReleaseDate(movie.getString("release_date"));
                    movieDetailObj.setmOverView(movie.getString("overview"));
                    movieDetailObj.setmVoteAverage(movie.getString("vote_average"));

                    photoStringAry[i] = movieDetailObj;
                }
            }
            return photoStringAry;
        }

        @Override
        protected MovieItem[] doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            String movieUri = "http://api.themoviedb.org/3/discover/movie";
            String sortByValue = null;

            //TODO: Please input the apikey here
            String apiKey = "";

            if (params[0] == SORT_MOST_POPULAR) {
                sortByValue = "popularity.desc";
            }
            else {
                sortByValue = "vote_average.desc";
            }

            Uri uriTemp = Uri.parse(movieUri)
                    .buildUpon()
                    .appendQueryParameter("sort_by", sortByValue)
                    .appendQueryParameter("api_key", apiKey)
                    .build();

            String uriStr = uriTemp.toString();

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
                Log.v(LOG_TAG, "Movie JSON String: " + moviesJsonStr);
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

            MovieItem[] photoStringAry = null;
            try {
                photoStringAry = getPosterThumbnailsDataFromJSON(moviesJsonStr);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error in parsing JSON", e);
            }
            return photoStringAry;
        }

        @Override
        protected void onPostExecute(MovieItem[] pThumbnailsAry) {

            if (pThumbnailsAry != null && pThumbnailsAry.length > 0) {

                ArrayList<MovieItem> listOfThumbnails = posterImagesAdapter.getmMovieList();
                listOfThumbnails.clear();

                for (MovieItem movieDetail : pThumbnailsAry) {
                    listOfThumbnails.add(movieDetail);
                }
                posterImagesAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean isNetWorkAvailable() {
        ConnectivityManager myConnectivityMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = myConnectivityMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class ImageAdapter extends BaseAdapter {
        final String LOG_TAG = ImageAdapter.class.getSimpleName();

        private Context mContext;
        private ArrayList<MovieItem> mMovieList = null;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            if (mMovieList == null)
            {
                return 0;
            }
            return mMovieList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public MovieItem getMovieDataAt(int position) {
            if (mMovieList == null)
            {
                return null;
            }
            return mMovieList.get(position);
        }

        public ArrayList<MovieItem> getmMovieList() {
            if (mMovieList == null) {
                mMovieList = new ArrayList<MovieItem>();
            }
            return mMovieList;
        }

        public void setmMovieList(ArrayList<MovieItem> pMovieList) {
            mMovieList = pMovieList;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;

            if (mMovieList != null && mMovieList.size() > 0) {
                if (convertView == null) {
                    imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
                    imageView.setAdjustViewBounds(true);

                    //If Using xml layout
                    //imageView = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.movie_poster, parent, false);
                } else {
                    imageView = (ImageView) convertView;
                }

                try {
                    MovieItem movieObj = mMovieList.get(position);
                    Picasso.with(mContext).load(movieObj.getmSmallPoster()).into(imageView);
                }
                catch (Exception e) {
                    Log.e(LOG_TAG, "Error ", e);
                }
            }


            return imageView;
        }


    }

}