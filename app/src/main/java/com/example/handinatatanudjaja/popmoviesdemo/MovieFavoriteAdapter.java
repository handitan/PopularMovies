package com.example.handinatatanudjaja.popmoviesdemo;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.handinatatanudjaja.popmoviesdemo.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by handinatatanudjaja on 11/3/15.
 */
public class MovieFavoriteAdapter extends CursorAdapter {

    public MovieFavoriteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int columnIdx = cursor.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_SMALL_POSTER_PATH);
        String smallPosterPath = cursor.getString(columnIdx);

        ImageView currentImageView = (ImageView) view;
        Picasso.with(context).load(smallPosterPath).into(currentImageView);
    }
}
