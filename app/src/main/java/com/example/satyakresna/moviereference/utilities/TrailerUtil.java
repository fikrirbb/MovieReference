package com.example.satyakresna.moviereference.utilities;

/**
 * Created by satyakresna on 27-Jul-17.
 */

public class TrailerUtil {
    public static String getVideoThumbnailUrl(String key) {
        return "http://img.youtube.com/vi/" + key + "/0.jpg";
    }

    public static String getYoutubeUrl(String key) {
        return "http://www.youtube.com/watch?v=" + key;
    }
}
