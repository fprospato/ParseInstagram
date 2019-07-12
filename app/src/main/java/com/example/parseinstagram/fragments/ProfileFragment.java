package com.example.parseinstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parseinstagram.R;
import com.example.parseinstagram.activity.EditProfileActivity;
import com.example.parseinstagram.adapter.ProfileAdapter;
import com.example.parseinstagram.model.Post;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private final static String TAG = "ProfileFragment";

    private SwipeRefreshLayout swipeContainer;

    private ImageView ivProfilePhoto;
    private TextView tvPostCount;
    private TextView tvName;
    private TextView tvBio;
    private Button btnEditProfile;

    private RecyclerView rvPosts;
    private ProfileAdapter adapter;
    private List<Post> mPosts;

    ParseObject user;
    String userId;
    boolean isCurrentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String userId = getArguments().getString("userId");
        if (userId != null && userId != ParseUser.getCurrentUser().getObjectId()) {
            isCurrentUser = false;
            this.userId = userId;
        } else {
            isCurrentUser = true;
            this.userId = ParseUser.getCurrentUser().getObjectId();
        }

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvName = view.findViewById(R.id.tvName);
        tvBio = view.findViewById(R.id.tvBio);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        if (!isCurrentUser) {
            btnEditProfile.getLayoutParams().height = 0;
        }

        swipeContainer = view.findViewById(R.id.swipeContainer);
        rvPosts = view.findViewById(R.id.rvPosts);
        mPosts = new ArrayList<>();

        adapter = new ProfileAdapter(getContext(), mPosts);
        rvPosts.setAdapter(adapter);

        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        setupPullToRefresh();

        setUserInfo();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }


    private void setupPullToRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    private void setUserInfo() {
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
        userQuery.getInBackground(userId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    user = object;

                    tvName.setText(object.getString("fullname"));

                    String bio = object.getString("bio");
                    String website = object.getString("website");

                    if (bio == "" && website != "") {
                        tvBio.setText(website);
                    } else if (bio != "" && website == "") {
                        tvBio.setText(bio);
                    } else if (bio != "" && website != "") {
                        tvBio.setText(Html.fromHtml(bio + "<br/>" + website));
                    }

                    ParseFile profileImage = object.getParseFile("profileImage");
                    if (profileImage != null) {
                        Glide.with(getContext())
                                .load(profileImage.getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfilePhoto);
                    }

                    getPosts();

                } else {
                    Log.e(TAG, "Error finding user.");
                    e.printStackTrace();
                }
            }
        });
    }


    private void getPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.whereEqualTo("user", user);
        postsQuery.orderByDescending("createdAt");

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    adapter.clear();

                    tvPostCount.setText(String.valueOf(objects.size()));

                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error getting user's posts count.");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error getting user's post count", Toast.LENGTH_SHORT).show();
                }

                swipeContainer.setRefreshing(false);
            }
        });
    }
}
