package com.example.eventplanner.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.comments.CommentModel;
import com.example.eventplanner.data.model.comments.CommentStatusUpdateModel;
import com.example.eventplanner.data.network.services.comments.CommentApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentModel> comments;
    private Context context;

    public CommentAdapter(List<CommentModel> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel c = comments.get(position);

        holder.name.setText((c.commenterFirstName != null ? c.commenterFirstName : "Anonymous") +
                (c.commenterLastName != null ? " " + c.commenterLastName : ""));

        holder.content.setText(c.content);
        holder.solution.setText("Service / product: " + c.solution);
        holder.provider.setText("Service and product provider: " + c.solutionProvider);

        Glide.with(context)
                .load(c.commenterProfilePicture != null ? c.commenterProfilePicture : "https://via.placeholder.com/40")
                .circleCrop()
                .into(holder.avatar);

        holder.stars.setText(new String(new char[c.rating]).replace("\0", "★"));
        holder.approveBtn.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                CommentModel comment = comments.get(pos);
                CommentStatusUpdateModel update = new CommentStatusUpdateModel(comment.id, "accepted");
                CommentApiClient.getInstance().approveComment(update).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(context, "Comment approved", Toast.LENGTH_SHORT).show();
                        comments.remove(pos);
                        notifyItemRemoved(pos);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Error approving comment", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                CommentModel comment = comments.get(pos);
                CommentStatusUpdateModel update = new CommentStatusUpdateModel(comment.id, "deleted");
                CommentApiClient.getInstance().deleteComment(update).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                        comments.remove(pos);
                        notifyItemRemoved(pos);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Error deleting comment", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, content, solution, provider, stars;
        Button approveBtn, deleteBtn;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            solution = itemView.findViewById(R.id.solution);
            provider = itemView.findViewById(R.id.provider);
            stars = itemView.findViewById(R.id.stars);
            approveBtn = itemView.findViewById(R.id.approve_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }
}
