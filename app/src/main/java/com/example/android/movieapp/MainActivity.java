package com.example.android.movieapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movieapp.adapter.MovieAdapter;
import com.example.android.movieapp.model.Movie;
import com.example.android.movieapp.utils.JsonUtils;
import com.example.android.movieapp.utils.UrlUtils;

import java.net.URL;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    TextView tv_error;
    ProgressBar pb_show_progress;
    Button btn_retry;
    private RecyclerView mRecyclerView;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String SAVED_TITLE= "savedTitle";
    private static final String SAVED_QUERY = "savedQuery";

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    MenuItem menuItem;

    private String queryMovie = "popular";
    private String appTitle = "Popular Movies";
    private Movie[] mMovie = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_error = findViewById(R.id.tv_error);
        pb_show_progress = findViewById(R.id.pb_show_progress);
        btn_retry = findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });

        mRecyclerView = findViewById(R.id.rv_show_movies_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        setTitle(appTitle);

        if (!isOnline()){
            networkError();
            return;
        }

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(SAVED_TITLE) || savedInstanceState.containsKey(SAVED_QUERY)){

                selected = savedInstanceState.getInt(MENU_SELECTED);
                queryMovie = savedInstanceState.getString(SAVED_QUERY);
                appTitle = savedInstanceState.getString(SAVED_TITLE);
                setTitle(appTitle);

                new  MovieFetchTask().execute(queryMovie);
                return;
            }
        }

        new MovieFetchTask().execute(queryMovie);

    }

    private boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnectedOrConnecting();
    }

    private void networkError(){
        pb_show_progress.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.VISIBLE);
        btn_retry.setVisibility(View.VISIBLE);
    }

    private void hideViews(){
        tv_error.setVisibility(View.INVISIBLE);
        pb_show_progress.setVisibility(View.INVISIBLE);
        btn_retry.setVisibility(View.INVISIBLE);
    }

    private void retry(){

        if (!isOnline()) {
            networkError();
            return;
        }

        hideViews();

        new MovieFetchTask().execute(queryMovie);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClickMovie(int position) {

        if (!isOnline()){
            mRecyclerView.setVisibility(View.INVISIBLE);
            networkError();
            return;
        }

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("MovieTitle", mMovie[position].getMovieTitle());
        intent.putExtra("MoviePlot", mMovie[position].getMoviePLot());
        intent.putExtra("MovieRating", mMovie[position].getMovieRating());
        intent.putExtra("ReleaseDate", mMovie[position].getReleaseDate());
        intent.putExtra("MovieImage", mMovie[position].getImage());

        startActivity(intent);

    }


    @SuppressLint("StaticFieldLeak")
    private class MovieFetchTask extends AsyncTask<String, Void, Movie[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.INVISIBLE);
            pb_show_progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Movie[] doInBackground(String... strings) {

            if (!isOnline()){
                networkError();
                return null;
            }

            if (UrlUtils.API_KEY.equals("")){
                networkError();
                tv_error.setText(R.string.missing_api_key);
                btn_retry.setVisibility(View.INVISIBLE);
                return null;
            }

            String movieQueryResponse;
            URL theMovieUrl = UrlUtils.buildUrl(strings[0]);

            try {

                movieQueryResponse = UrlUtils.getResponseFromHttp(theMovieUrl);
                mMovie = JsonUtils.parseMovieJson(movieQueryResponse);

            } catch (Exception e){
                e.printStackTrace();
            }

            return mMovie;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            new MovieFetchTask().cancel(true);

            if (movies !=null){

                mMovie = movies;
                MovieAdapter adapter = new MovieAdapter(movies, MainActivity.this, MainActivity.this);
                mRecyclerView.setAdapter(adapter);

                mRecyclerView.setVisibility(View.VISIBLE);
                hideViews();

            } else {
                Log.e(LOG_TAG, "Problems with the adapter");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        if (selected == -1){
            return true;
        }

        switch (selected){

            case R.id.popularity:
                menuItem = menu.findItem(R.id.popularity);
                menuItem.setChecked(true);
                break;

            case R.id.top_rated:
                menuItem = menu.findItem(R.id.top_rated);
                menuItem.setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!isOnline()) return false;

        if (UrlUtils.API_KEY.equals("")) return false;

        int id = item.getItemId();

        switch (id){
            case R.id.popularity:

                selected = id;
                item.setChecked(true);

                queryMovie = "popular";
                new MovieFetchTask().execute(queryMovie);

                appTitle = "Popular Movies";
                setTitle(appTitle);

                break;

            case R.id.top_rated:

                selected = id;
                item.setChecked(true);

                queryMovie = "top_rated";
                new MovieFetchTask().execute(queryMovie);

                appTitle = "Top Rated Movies";
                setTitle(appTitle);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String currQueryMovie = queryMovie;
        String currAppTitle = appTitle;

        savedInstanceState.putString(SAVED_QUERY, currQueryMovie);
        savedInstanceState.putString(SAVED_TITLE, currAppTitle);

        savedInstanceState.putInt(MENU_SELECTED, selected);

    }
}