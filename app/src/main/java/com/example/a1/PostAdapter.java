package com.example.a1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvLocation.setText("Location: " + post.getLocation());
        holder.tvType.setText(post.getType());

        // Set text color based on status (LOST/FOUND)
        if ("LOST".equalsIgnoreCase(post.getType())) {
            holder.tvType.setTextColor(0xFFFF0000); // Red
        } else {
            holder.tvType.setTextColor(0xFF00FF00); // Green
        }

        // --- Load Image using Glide ---
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // --- Handle Click to show Details ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), activity_details.class);

            // Passing all fields to the details page
            intent.putExtra("title", post.getTitle());
            intent.putExtra("location", post.getLocation());
            intent.putExtra("type", post.getType());
            intent.putExtra("question", post.getQuestion());
            intent.putExtra("answer", post.getAnswer());
            intent.putExtra("userId", post.getUserId());
            intent.putExtra("imageUrl", post.getImageUrl());
            intent.putExtra("category", post.getCategory());
            intent.putExtra("postId", post.getPostId());

            // CRITICAL: Passing description so Edit Mode works correctly
            intent.putExtra("description", post.getDescription());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvType;
        ImageView ivPostImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLocation = itemView.findViewById(R.id.tvPostLocation);
            tvType = itemView.findViewById(R.id.tvPostType);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
        }
    }

    // Filter method for Search and Chips
    public void filterList(List<Post> filteredList) {
        this.postList = filteredList;
        notifyDataSetChanged();
    }
}
