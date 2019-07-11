package com.example.parseinstagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_CREATED_AT = "createdAt";

    private static boolean didCurrentUserLike = false;


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

    public boolean getIfUserLike() { return didCurrentUserLike; }
    public void setIfUserLike(boolean didLike) { didCurrentUserLike = didLike; }

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

}
