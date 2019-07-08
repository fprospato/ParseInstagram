package com.example.parseinstagram;

import android.app.Application;

import com.example.parseinstagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("")
                .clientKey("")
                .server("")
                .build();

        Parse.initialize(configuration);
    }
}
