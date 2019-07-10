package com.example.parseinstagram;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parseinstagram.model.Post;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePhoto = itemView.findViewById(R.id.ivProfilePhoto);
            tvName = itemView.findViewById(R.id.tvName);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);


            ivPostImage.getLayoutParams().height = HomeActivity.screenWidth;
        }

        public void bind(Post post) {
            tvName.setText(post.getUser().getUsername());
            tvDescription.setText(Html.fromHtml("<b>" + post.getUser().getUsername() + "</b>"+ " " + post.getDescription()));

            ParseFile postImage = post.getImage();
            if (postImage != null) {
                Glide.with(context).load(postImage.getUrl()).into(ivPostImage);
            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
