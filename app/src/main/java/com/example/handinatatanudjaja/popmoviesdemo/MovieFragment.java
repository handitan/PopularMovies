package com.example.handinatatanudjaja.popmoviesdemo;


import android.content.Context;
import android.content.Intent;
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

    private final int SORT_MOST_POPULAR = 0;
    private final int SORT_HIGHEST_RATED = 1;
    final String LOG_TAG = MovieFragment.class.getSimpleName();

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_movie, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_popular_movies) {
            new FetchImageTask().execute(SORT_MOST_POPULAR);
            return true;
        }
        else
        if (id == R.id.action_highest_rated_movies) {
            new FetchImageTask().execute(SORT_HIGHEST_RATED);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        GridView movieGridView = (GridView) rootView.findViewById(R.id.gridview_movie);

        posterImagesAdapter = new ImageAdapter(container.getContext());
        movieGridView.setAdapter(posterImagesAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject movieObj = (JSONObject) posterImagesAdapter.getMovieDataAt(position);
                    Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class)
                            .putExtra("title", movieObj.getString("title"))
                            .putExtra("bigposter", movieObj.getString("bigposter"))
                            .putExtra("releasedate", movieObj.getString("releasedate"))
                            .putExtra("voteaverage", movieObj.getString("voteaverage"))
                            .putExtra("overview", movieObj.getString("overview"));
                    startActivity(detailIntent);
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        });

        new FetchImageTask().execute(SORT_MOST_POPULAR);

        return rootView;
    }

    private class FetchImageTask extends AsyncTask<Integer, Void, JSONObject[]> {

        final String LOG_TAG = MovieFragment.class.getSimpleName();

        private JSONObject[] getPosterThumbnailsDataFromJSON(String pMovieJsonStr) throws JSONException{

            JSONObject jsonTemp = new JSONObject(pMovieJsonStr);
            JSONArray movieAry = jsonTemp.getJSONArray("results");

            JSONObject[] photoStringAry = null;
            if (movieAry != null && movieAry.length() > 0) {
                photoStringAry = new JSONObject[movieAry.length()];
                for (int i = 0; i < movieAry.length(); ++i) {
                    JSONObject movie = movieAry.getJSONObject(i);
                    JSONObject movieDetailObj = new JSONObject();

                    movieDetailObj.put("title", movie.getString("title"));
                    movieDetailObj.put("smallposter","http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path"));
                    movieDetailObj.put("bigposter","http://image.tmdb.org/t/p/w500/" + movie.getString("poster_path"));
                    movieDetailObj.put("releasedate", movie.getString("release_date"));
                    movieDetailObj.put("overview", movie.getString("overview"));
                    movieDetailObj.put("voteaverage", movie.getString("vote_average"));
                    photoStringAry[i] = movieDetailObj;
                }
            }
            return photoStringAry;
        }

        @Override
        protected JSONObject[] doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            String movieUri = "http://api.themoviedb.org/3/discover/movie";
            String sortByValue = null;

            //TODO: Please input the apikey here
            String apiKey = "???";

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

            JSONObject[] photoStringAry = null;
            try {
                photoStringAry = getPosterThumbnailsDataFromJSON(moviesJsonStr);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error in parsing JSON", e);
            }
            return photoStringAry;
        }

        @Override
        protected void onPostExecute(JSONObject[] pThumbnailsAry) {

            if (pThumbnailsAry != null && pThumbnailsAry.length > 0) {

                ArrayList<JSONObject> listOfThumbnails = posterImagesAdapter.getmMovieList();
                listOfThumbnails.clear();

                for (JSONObject movieDetail : pThumbnailsAry) {
                    listOfThumbnails.add(movieDetail);
                }
                posterImagesAdapter.notifyDataSetChanged();
            }
        }
    }


    private class ImageAdapter extends BaseAdapter {
        final String LOG_TAG = ImageAdapter.class.getSimpleName();

        private Context mContext;
        private ArrayList<JSONObject> mMovieList = null;

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

        public JSONObject getMovieDataAt(int position) {
            if (mMovieList == null)
            {
                return null;
            }
            return mMovieList.get(position);
        }

        public ArrayList<JSONObject> getmMovieList() {
            if (mMovieList == null) {
                mMovieList = new ArrayList<JSONObject>();
            }
            return mMovieList;
        }

        /*public void setThumbsData(ArrayList<JSONObject> thumbsAryList) {
            if (mMovieList != null) {
                mMovieList.clear();
            }
            mMovieList = thumbsAryList;
        }*/

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
                    JSONObject movieObj = mMovieList.get(position);
                    Picasso.with(mContext).load(movieObj.getString("smallposter")).into(imageView);
                }
                catch (Exception e) {
                    Log.e(LOG_TAG, "Error ", e);
                }
            }


            return imageView;
        }


    }

}
