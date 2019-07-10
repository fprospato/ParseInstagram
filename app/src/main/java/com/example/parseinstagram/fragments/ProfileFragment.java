package com.example.parseinstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parseinstagram.R;
import com.example.parseinstagram.adapter.ProfileAdapter;
import com.example.parseinstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private final static String TAG = "ProfileFragment";

    private ImageView ivProfilePhoto;
    private TextView tvPostCount;
    private TextView tvName;
    private TextView tvBio;

    private RecyclerView rvPosts;
    private ProfileAdapter adapter;
    private List<Post> mPosts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvName = view.findViewById(R.id.tvName);
        tvBio = view.findViewById(R.id.tvBio);

        rvPosts = view.findViewById(R.id.rvPosts);
        mPosts = new ArrayList<>();

        adapter = new ProfileAdapter(getContext(), mPosts);
        rvPosts.setAdapter(adapter);

        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        setUserInfo();

        getPosts();
    }


    private void setUserInfo() {
        String fullname = ParseUser.getCurrentUser().getString("fullname");
        tvName.setText(fullname);

        String bio = ParseUser.getCurrentUser().getString("bio");
        if (bio != null)
            tvBio.setText(bio);

        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (profileImage != null) {
            Glide.with(getContext())
                    .load(profileImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfilePhoto);
        }
    }


    private void getPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        postsQuery.orderByDescending("createdAt");

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    tvPostCount.setText(String.valueOf(objects.size()));

                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error getting user's posts count.");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error getting user's post count", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
