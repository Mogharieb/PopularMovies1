package com.example.android.movieapp.model;

public class Movie {

    private String movieTitle;
    private String moviePLot;
    private String movieRating;
    private String image;
    private String releaseDate;


    public Movie() {
    }

    public String getMovieTitle() {

        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {

        this.movieTitle = movieTitle;
    }

    public String getMoviePLot() {

        return moviePLot;
    }

    public void setMoviePLot(String moviePLot) {

        this.moviePLot = moviePLot;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {

        this.releaseDate = releaseDate;
    }

}
