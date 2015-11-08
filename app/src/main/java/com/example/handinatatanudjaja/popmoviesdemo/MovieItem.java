package com.example.handinatatanudjaja.popmoviesdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by handinatatanudjaja on 8/23/15.
 */
public class MovieItem implements Parcelable{
    private String mTitle;
    private String mSmallPoster;
    private String mBigPoster;
    private String mReleaseDate;
    private String mOverView;
    private String mVoteAverage;
    private String mID;

    public MovieItem() {}

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmSmallPoster() {
        return mSmallPoster;
    }

    public void setmSmallPoster(String mSmallPoster) {
        this.mSmallPoster = mSmallPoster;
    }

    public String getmBigPoster() {
        return mBigPoster;
    }

    public void setmBigPoster(String mBigPoster) {
        this.mBigPoster = mBigPoster;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmOverView() {
        return mOverView;
    }

    public void setmOverView(String mOverView) {
        this.mOverView = mOverView;
    }

    public String getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getmID() { return mID; }

    public void setmID(String mID) {this.mID = mID;}

    //Parcelables implementations
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString(mSmallPoster);
        out.writeString(mBigPoster);
        out.writeString(mReleaseDate);
        out.writeString(mOverView);
        out.writeString(mVoteAverage);
        out.writeString(mID);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR
            = new Parcelable.Creator<MovieItem>() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    private MovieItem(Parcel in) {
        mTitle = in.readString();
        mSmallPoster = in.readString();
        mBigPoster = in.readString();
        mReleaseDate = in.readString();
        mOverView = in.readString();
        mVoteAverage = in.readString();
        mID = in.readString();
    }
}
