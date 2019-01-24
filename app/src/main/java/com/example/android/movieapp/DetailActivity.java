package com.example.android.movieapp;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String URL_IMAGE_PATH = "http://image.tmdb.org/t/p/w185";

    ImageView iv_poster;
    TextView tv_title, tv_plot, tv_rating, tv_release_date;
    String activityTitle = "Movie Details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iv_poster = findViewById(R.id.iv_movie_poster);
        tv_title = findViewById(R.id.tv_movie_title);
        tv_plot =findViewById(R.id.tv_movie_plot);
        tv_rating = findViewById(R.id.tv_movie_rating);
        tv_release_date = findViewById(R.id.tv_movie_release_date);

        String poster = getIntent().getStringExtra("MovieImage");
        String title = getIntent().getStringExtra("MovieTitle");
        String plot = getIntent().getStringExtra("MoviePlot");
        String rating = getIntent().getStringExtra("MovieRating");
        String releaseDate = getIntent().getStringExtra("ReleaseDate");

        setTitle(activityTitle);

        Picasso.with(this)
                .load(URL_IMAGE_PATH.concat(poster))
                .placeholder(R.mipmap.ic_launcher)
                .into(iv_poster);

        if (title.isEmpty()){
            tv_title.setText(R.string.no_data);
        }else {
            tv_title.setText(title);
        }

        if (plot.isEmpty()){
            tv_plot.setText(R.string.no_data);
        }else {
            tv_plot.setText(plot);
        }

        if (rating.isEmpty()){
            tv_rating.setText(R.string.no_data);
        }else {
            tv_rating.setText(rating);
        }

        if (releaseDate.isEmpty()){
            tv_release_date.setText(R.string.no_data);
        }else {
            tv_release_date.setText(releaseDate);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
