package com.example.myfirst.android.app;

import android.app.Application;

import com.example.myfirst.R;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class Backend extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse and Back4App setup
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}