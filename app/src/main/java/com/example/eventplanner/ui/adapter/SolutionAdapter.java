package com.example.eventplanner.ui.adapter;

import android.annotation.SuppressLint;
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
import com.example.eventplanner.data.model.Page;
import com.example.eventplanner.data.model.solutions.OfferingModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {

    private List<OfferingModel> solutions = new ArrayList<>();
    private OnSolutionClickedListener listener;

    public void setOnSolutionClickedListener(OnSolutionClickedListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSolutions(Collection<? extends OfferingModel> solutions) {
        this.solutions.clear();
        this.solutions.addAll(solutions);
        notifyDataSetChanged();
    }



    public SolutionAdapter() {
        this(List.of());
    }

    public SolutionAdapter(Page<? extends OfferingModel> solutions) {
        this(solutions.getContent());
    }

    public SolutionAdapter(Collection<? extends OfferingModel> solutions) {
        this.solutions = new ArrayList<>(solutions);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solution_card, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OfferingModel solution = solutions.get(position);

        holder.solutionTitle.setText(solution.getName());
        holder.solutionDescription.setText(solution.getDescription());

        holder.viewMore.setOnClickListener(view -> {
            if (listener != null)
                listener.onClick(solution);
        });

        Glide.with(holder.itemView.getContext())
                .load(solution.getImageUrl())
                .placeholder(R.drawable.card_placeholder)
                .error(R.drawable.card_placeholder)
                .into(holder.solutionImage);
    }


    @Override
    public int getItemCount() {
        return solutions.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView providerCompanyName, solutionTitle, solutionDescription;
        ImageView solutionImage;
        Button viewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            providerCompanyName = itemView.findViewById(R.id.provider_name);
            solutionTitle = itemView.findViewById(R.id.solution_title);
            solutionDescription = itemView.findViewById(R.id.solution_description);
            solutionImage = itemView.findViewById(R.id.solution_image);
            viewMore = itemView.findViewById(R.id.view_more_button_solution);
        }

    }

    @FunctionalInterface
    public interface OnSolutionClickedListener {
        void onClick(OfferingModel solution);
    }

}
