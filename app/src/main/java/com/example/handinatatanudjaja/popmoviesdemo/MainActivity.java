package com.example.handinatatanudjaja.popmoviesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private static final String MOVIEDETAILFRAGMENT_TAG = "MOVDFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movies_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_detail_container, new MovieDetailFragment(),MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Batman todos
        /*String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }*/
    }

    @Override
    public void onMovieItemSelected(String movTitle, String movBigPoster, String movReleaseDate, String movVoteAvg,
                                    String movOverView, String movID, int movSelectedSortBy) {

        Intent detailIntent = new Intent(this, MovieDetailActivity.class)
                .putExtra("title", movTitle)
                .putExtra("bigposter", movBigPoster)
                .putExtra("releasedate", movReleaseDate)
                .putExtra("voteaverage", movVoteAvg)
                .putExtra("overview", movOverView)
                .putExtra("id", movID)
                .putExtra("selectedSortBy", movSelectedSortBy);
        if (mTwoPane) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.MOVIE_DETAIL_PARCEL, detailIntent);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_detail_container, fragment,MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        }
        else {

            startActivity(detailIntent);
        }
    }

}
