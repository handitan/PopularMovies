package com.example.handinatatanudjaja.popmoviesdemo;

/**
 * Created by handinatatanudjaja on 11/8/15.
 */
public class MovieReviewItem {
    private String author = "";
    private String content = "";
    private String movieID = "";
    private String reviewID = "";

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }
}
