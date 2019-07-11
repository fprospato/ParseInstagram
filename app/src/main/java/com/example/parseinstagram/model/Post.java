package com.example.parseinstagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject implements Comparable<Post> {

    private final static String TAG = "Post";

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_LIKE_COUNT = "likeCount";

    public boolean didCurrentUserLike;
    public Date createdAt;


    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String desription) {
        put(KEY_DESCRIPTION, desription);
    }


    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }


    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Integer getLikeCount() {
        return getInt(KEY_LIKE_COUNT);
    }
    public void setLikeCount(Integer count) {
        put(KEY_LIKE_COUNT, count);
    }


    public static class Query extends ParseQuery<Post> {

        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }

    @Override
    public int compareTo(Post post) {
        if (this.createdAt.compareTo(post.createdAt) == -1) {
            return 1;
        } else if (this.createdAt.compareTo(post.createdAt) == 1) {
            return -1;
        }

        return 0;
    }

}
