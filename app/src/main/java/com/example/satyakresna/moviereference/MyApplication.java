package com.example.satyakresna.moviereference;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by satyakresna on 06-Aug-17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
