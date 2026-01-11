package com.example.a1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        // Set color based on status
        if ("LOST".equalsIgnoreCase(post.getType())) {
            holder.tvType.setTextColor(0xFFFF0000); // Red
        } else {
            holder.tvType.setTextColor(0xFF00FF00); // Green
        }

        // --- UPDATED: PASS SECURITY INFO TO DETAILS ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), activity_details.class);
            intent.putExtra("title", post.getTitle());
            intent.putExtra("location", post.getLocation());
            intent.putExtra("type", post.getType());

            // New extras for security verification
            intent.putExtra("question", post.getQuestion());
            intent.putExtra("answer", post.getAnswer());
            intent.putExtra("userId", post.getUserId());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLocation = itemView.findViewById(R.id.tvPostLocation);
            tvType = itemView.findViewById(R.id.tvPostType);
        }
    }

    public void filterList(List<Post> filteredList) {
        this.postList = filteredList;
        notifyDataSetChanged();
    }
}
