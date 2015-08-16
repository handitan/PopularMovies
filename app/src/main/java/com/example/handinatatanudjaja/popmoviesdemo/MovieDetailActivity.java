package com.example.handinatatanudjaja.popmoviesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
