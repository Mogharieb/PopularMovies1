package com.example.android.movieapp.utils;

import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UrlUtils {

    public static final String API_KEY = "0df064fba52190f866fa431ec25d4bfb";
    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String MOVIE_QUERY_API = "api_key";

    private static final String LOG_TAG = UrlUtils.class.getSimpleName();

    public static URL buildUrl(String movieUrl){

        Uri uri = Uri.parse(MOVIE_BASE_URL)
                .buildUpon()
                .appendPath(movieUrl)
                .appendQueryParameter(MOVIE_QUERY_API, API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problems creating the url", e);
        }

        return url;
    }


    public static String getResponseFromHttp(URL url) throws IOException{

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scn = new Scanner(inputStream);
            scn.useDelimiter("\\A");

            boolean hasInput = scn.hasNext();
            if (hasInput) {
                return scn.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }
}
