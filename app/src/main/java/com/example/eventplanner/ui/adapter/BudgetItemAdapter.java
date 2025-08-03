package com.example.eventplanner.ui.adapter;


import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.budget.BudgetItemModel;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class BudgetItemAdapter extends RecyclerView.Adapter<BudgetItemAdapter.BudgetViewHolder> {
    private static final Format NUM_FMT = NumberFormat.getCurrencyInstance();

    private final List<BudgetItemModel> items;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onUpdate(BudgetItemModel item);
        void onDelete(BudgetItemModel item);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    public BudgetItemAdapter(List<BudgetItemModel> items) {
        this.items = new ArrayList<>(items);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(Collection<? extends BudgetItemModel> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetItemModel item = items.get(position);

        holder.categoryName.setText(item.getCategory().getName());
        holder.plannedAmount.setText(NUM_FMT.format(item.getAmount()));
        holder.spentAmount.setText(NUM_FMT.format(item.getSpent()));

        holder.editAmount.setVisibility(View.GONE);
        holder.editAmount.setText("");
        holder.editAmount.setError(null);

        holder.editButton.setOnClickListener(v -> {
            holder.editAmount.setVisibility(View.VISIBLE);
            holder.editAmount.setText(String.valueOf(item.getAmount()));
            holder.editAmount.requestFocus();
            holder.editAmount.setSelection(holder.editAmount.getText().length());
        });

        holder.editAmount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {

                try {
                    double newAmount = Double.parseDouble(holder.editAmount.getText().toString());
                    item.setAmount(newAmount);
                    listener.onUpdate(item);
                    notifyItemChanged(position);
                } catch (NumberFormatException e) {
                    holder.editAmount.setError("Invalid amount");
                }

                return true;
            }
            return false;
        });

        holder.deleteButton.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, plannedAmount, spentAmount;
        EditText editAmount;
        ImageButton editButton, deleteButton;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            plannedAmount = itemView.findViewById(R.id.planned_amount);
            spentAmount = itemView.findViewById(R.id.spent_amount);
            editAmount = itemView.findViewById(R.id.edit_amount);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
