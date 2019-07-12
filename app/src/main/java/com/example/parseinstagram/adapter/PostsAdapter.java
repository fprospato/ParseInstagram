package com.example.parseinstagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.parseinstagram.R;
import com.example.parseinstagram.activity.HomeActivity;
import com.example.parseinstagram.activity.ProfileActivity;
import com.example.parseinstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final static String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;


    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.post = post;

        holder.bind(post);
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePhoto;
        private TextView tvName;
        private ImageView ivPostImage;
        private TextView tvDescription;
        private TextView tvTime;

        private Button btnProfile;
        private TextView tvLikeCount;
        private ImageView ivLike;
        private Button btnLike;

        Post post;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePhoto = itemView.findViewById(R.id.ivProfilePhoto);
            tvName = itemView.findViewById(R.id.tvName);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivLike = itemView.findViewById(R.id.ivLike);
            btnLike = itemView.findViewById(R.id.btnLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            btnProfile = itemView.findViewById(R.id.btnProfile);

            ivPostImage.getLayoutParams().height = HomeActivity.screenWidth;

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likePost();
                }
            });
            btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToProfile();
                }
            });
        }

        public void bind(Post post) {
            tvName.setText(post.getUser().getUsername());
            tvDescription.setText(Html.fromHtml("<b>" + post.getUser().getUsername() + "</b>"+ " " + post.getDescription()));

            tvLikeCount.setText(String.valueOf(post.getLikeCount()) + ((post.getLikeCount() == 1) ? " like" : " likes"));

            String relativeDate = "";
            long dateMillis = post.getCreatedAt().getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            tvTime.setText(relativeDate);

            ParseFile profileImage = post.getUser().getParseFile("profileImage");
            if (profileImage != null) {
                Glide.with(context)
                        .load(profileImage.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfilePhoto);
            }

            ParseFile postImage = post.getImage();
            if (postImage != null) {
                Glide.with(context).load(postImage.getUrl()).into(ivPostImage);
            }

            if (post.didCurrentUserLike) {
                ivLike.setImageResource(R.drawable.ufi_heart_active);
            } else {
                ivLike.setImageResource(R.drawable.ufi_heart);
            }
        }

        private void likePost() {
            if (post.didCurrentUserLike) {
                tvLikeCount.setText(String.valueOf(post.getLikeCount() - 1) + ((post.getLikeCount() == 1) ? " like" : " likes"));
                ivLike.setImageResource(R.drawable.ufi_heart);

                post.increment("likeCount", -1);
                post.saveInBackground();

                removeLike();
            } else {
                ivLike.setImageResource(R.drawable.ufi_heart_active);
                tvLikeCount.setText(String.valueOf(post.getLikeCount() + 1) + ((post.getLikeCount() == 1) ? " like" : " likes"));

                post.increment("likeCount");
                post.saveInBackground();

                sendLike();
            }

            post.didCurrentUserLike = !post.didCurrentUserLike;
        }

        private void sendLike() {
            ParseObject newLike = new ParseObject("Like");
            newLike.put("user", ParseUser.getCurrentUser());
            newLike.put("post", post);

            newLike.saveInBackground();
        }

        private void removeLike() {
            ParseQuery<ParseObject> likeQuery = ParseQuery.getQuery("Like");
            likeQuery.whereEqualTo("user", ParseUser.getCurrentUser());
            likeQuery.whereEqualTo("post", post);

            likeQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                         for (int i = 0; i < objects.size(); i++) {
                             objects.get(i).deleteInBackground();
                         }
                    } else {
                        Log.e(TAG, "Error finding like.");
                        e.printStackTrace();
                    }
                }
            });
        }

        private void goToProfile() {
            Intent intent = new Intent(context, ProfileActivity.class);

            intent.putExtra("username", post.getUser().getUsername());
            intent.putExtra("userId", post.getUser().getObjectId());

            context.startActivity(intent);
        }
    }


    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
