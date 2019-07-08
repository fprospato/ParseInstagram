package com.example.parseinstagram;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parseinstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final static String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ParseQuery.getQuery(Post.class).findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d(TAG, "Post [ " + i + "] = " + objects.get(i).getDescription());
                    }
                } else {
                    Log.e(TAG, "Error getting posts.");
                }
            }
        });
    }
}
