package com.example.eventplanner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;

import org.json.JSONObject;

import java.util.List;

public class OurSolutionAdapter  extends RecyclerView.Adapter<OurSolutionAdapter.SolutionViewHolder>{
    private final List<JSONObject> solutions;
    private final Context context;
    private final OurSolutionAdapter.OnSolutionClickListener listener;

    public interface OnSolutionClickListener {
        void onSolutionClick(JSONObject event);
    }

    public OurSolutionAdapter(Context context, List<JSONObject> solutions, OurSolutionAdapter.OnSolutionClickListener listener) {
        this.context = context;
        this.solutions = solutions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OurSolutionAdapter.SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solution_card, parent, false);
        return new OurSolutionAdapter.SolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OurSolutionAdapter.SolutionViewHolder holder, int position) {
        JSONObject obj = solutions.get(position);

        try {
            holder.providerCompanyName.setText(obj.getString("providerCompanyName"));
            holder.solutionTitle.setText(obj.getString("name"));
            holder.solutionDescription.setText(obj.getString("description"));

            String baseUrl = "http://10.0.2.2:8080/";

            String fullImageUrl = baseUrl + obj.getString("imageUrl");
            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.card_placeholder)
                    .error(R.drawable.card_placeholder)
                    .into(holder.solutionImage);

            holder.viewMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSolutionClick(obj);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    public void updateData(List<JSONObject> newSolutions) {
        solutions.clear();
        solutions.addAll(newSolutions);
        notifyDataSetChanged();
    }


    static class SolutionViewHolder extends RecyclerView.ViewHolder {
        TextView providerCompanyName, solutionTitle, solutionDescription;
        ImageView solutionImage;
        Button viewMore;

        public SolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            providerCompanyName = itemView.findViewById(R.id.provider_name);
            solutionTitle = itemView.findViewById(R.id.solution_title);
            solutionDescription = itemView.findViewById(R.id.solution_description);
            solutionImage = itemView.findViewById(R.id.solution_image);
            viewMore = itemView.findViewById(R.id.view_more_button2);
        }
    }
}
