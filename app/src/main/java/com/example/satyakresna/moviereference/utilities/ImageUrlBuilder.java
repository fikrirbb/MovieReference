package com.example.satyakresna.moviereference.utilities;

/**
 * Created by satyakresna on 27-Jul-17.
 */

public class ImageUrlBuilder {
    public static final String IMG_URL = "http://image.tmdb.org/t/p/";

    public static String getPosterUrl(String path) {
        return IMG_URL + "w185" + path;
    }

    public static String getBackdropUrl(String path) {
        return IMG_URL + "w500" + path;
    }
}
