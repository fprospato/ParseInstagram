package com.example.parseinstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.parseinstagram.R;
import com.example.parseinstagram.adapter.PostsAdapter;
import com.example.parseinstagram.helper.BitmapScaler;
import com.example.parseinstagram.model.Post;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostsFragment extends Fragment {

    private final static String TAG = "PostsFragment";

    private BitmapScaler.EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager linearLayoutManager;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> mPosts;

    private boolean isLoading;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        mPosts = new ArrayList<>();

        adapter = new PostsAdapter(getContext(), mPosts);
        rvPosts.setAdapter(adapter);

        linearLayoutManager = new LinearLayoutManager(getContext());

        rvPosts.setLayoutManager(linearLayoutManager);

        loadTopPosts();

        setupRecyclerViewExtras();
    }


    private void setupRecyclerViewExtras() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTopPosts();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new BitmapScaler.EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!isLoading) {
                    loadMoreTopPosts();
                }
            }
        };

        rvPosts.addOnScrollListener(scrollListener);
    }

    private void loadTopPosts() {
        isLoading = true;

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();
        postsQuery.orderByDescending("createdAt");
        postsQuery.setLimit(10);

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(final List<Post> objects, ParseException e) {
                if (e == null) {

                    adapter.clear();

                    for (int i = 0; i < objects.size(); i++) {
                        final Post post = objects.get(i);

                        post.createdAt = post.getCreatedAt();

                        ParseQuery<ParseObject> likeQuery = ParseQuery.getQuery("Like");
                        likeQuery.whereEqualTo("post", post);
                        likeQuery.whereEqualTo("user", ParseUser.getCurrentUser());

                        likeQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int count, ParseException e) {
                                if (e == null) {
                                    post.didCurrentUserLike = (count > 0) ? true : false;

                                } else {
                                    post.didCurrentUserLike = false;
                                    Log.e(TAG, "Error getting post like count.");
                                    e.printStackTrace();
                                }

                                mPosts.add(post);
                                adapter.notifyItemInserted(mPosts.size() - 1);

                                if (mPosts.size() > 1) {
                                    Collections.sort(mPosts);
                                    adapter.notifyDataSetChanged();
                                }

                                if (mPosts.size() == objects.size()) {
                                    isLoading = false;
                                }
                            }
                        });
                    }

                    for (int i = 0; i < objects.size(); i++) {
                        Log.d(TAG, "Post [ " + i + " ] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername()
                        );
                    }

                } else {
                    isLoading = false;

                    Log.e(TAG, "Error getting posts.");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error getting posts", Toast.LENGTH_SHORT).show();
                }

                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void loadMoreTopPosts() {
        isLoading = true;

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();
        postsQuery.orderByDescending("createdAt");
        postsQuery.setLimit(20);
        postsQuery.setSkip(mPosts.size());

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(final List<Post> objects, ParseException e) {
                if (e == null) {


                    for (int i = 0; i < objects.size(); i++) {
                        final Post post = objects.get(i);

                        post.createdAt = post.getCreatedAt();

                        ParseQuery<ParseObject> likeQuery = ParseQuery.getQuery("Like");
                        likeQuery.whereEqualTo("post", post);
                        likeQuery.whereEqualTo("user", ParseUser.getCurrentUser());

                        likeQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int count, ParseException e) {
                                if (e == null) {
                                    post.didCurrentUserLike = (count > 0) ? true : false;

                                } else {
                                    post.didCurrentUserLike = false;
                                    Log.e(TAG, "Error getting post like count.");
                                    e.printStackTrace();
                                }

                                mPosts.add(post);
                                adapter.notifyItemInserted(mPosts.size() - 1);

                                if (mPosts.size() > 1) {
                                    Collections.sort(mPosts);
                                    adapter.notifyDataSetChanged();
                                }

                                if (mPosts.size() == objects.size()) {
                                    isLoading = false;
                                }
                            }
                        });
                    }

                    for (int i = 0; i < objects.size(); i++) {
                        Log.d(TAG, "Post [ " + i + " ] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername()
                        );
                    }
                } else {
                    isLoading = false;

                    Log.e(TAG, "Error getting posts.");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error getting posts", Toast.LENGTH_SHORT).show();
                }

                swipeContainer.setRefreshing(false);
            }
        });
    }
}
