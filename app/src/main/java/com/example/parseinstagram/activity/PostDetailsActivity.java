package com.example.parseinstagram.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parseinstagram.R;
import com.example.parseinstagram.adapter.PostsAdapter;
import com.example.parseinstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    private final static String TAG = "PostDetailsFragment";

    RecyclerView rvPosts;

    private String postId;
    protected PostsAdapter adapter;
    protected List<Post> mPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        postId = getIntent().getStringExtra("postId");

        rvPosts = findViewById(R.id.rvPosts);
        mPosts = new ArrayList<>();

        adapter = new PostsAdapter(this, mPosts);
        rvPosts.setAdapter(adapter);

        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        setupActionBar();

        loadPost();
    }


    private void setupActionBar() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.insta_grey)));

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.insta_action_bar);
        getSupportActionBar().setElevation(2);

        View view = getSupportActionBar().getCustomView();
        ImageView ivLeft = view.findViewById(R.id.ivLeft);
        ImageView ivCenter = view.findViewById(R.id.ivCenter);
        ImageView ivRight = view.findViewById(R.id.ivRight);
        TextView tvCenter = view.findViewById(R.id.tvCenter);

        ivLeft.setVisibility(View.INVISIBLE);
        ivCenter.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
        tvCenter.setVisibility(View.VISIBLE);

        tvCenter.setText("Post");
    }


    private void loadPost() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();
        postsQuery.whereEqualTo("objectId", postId);

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {

                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();

                    for (int i = 0; i < objects.size(); i++) {
                        Log.d(TAG, "Post [ " + i + " ] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername()
                        );
                    }
                } else {
                    Log.e(TAG, "Error getting posts.");
                    e.printStackTrace();
                    //Toast.makeText(this, "Error getting posts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
