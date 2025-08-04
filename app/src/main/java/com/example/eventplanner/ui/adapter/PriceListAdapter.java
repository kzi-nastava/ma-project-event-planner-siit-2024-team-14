package com.example.eventplanner.ui.adapter;

import static com.example.eventplanner.ui.util.Util.parseDouble;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.SolutionPrice;


import org.jetbrains.annotations.NotNull;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;



public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.PriceListViewHolder> {
    private static final Format NUM_FMT = NumberFormat.getCurrencyInstance();

    private List<SolutionPrice> items = new ArrayList<>();

    @Nullable
    private OnSolutionPriceChangeListener changeListener;

    @FunctionalInterface
    public interface OnSolutionPriceChangeListener {
        void onPriceChange(SolutionPrice price);
    }

    public void setOnPriceChangeListener(OnSolutionPriceChangeListener listener) {
        this.changeListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(@Nullable List<SolutionPrice> newItems) {
        this.items = Optional.ofNullable(newItems).orElseGet(ArrayList::new);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public PriceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pricelist_row, parent, false);
        return new PriceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListViewHolder holder, int position) {
        SolutionPrice item = items.get(position);

        holder.bind(item);

        holder.price.setOnClickListener(v -> {
            holder.price.setVisibility(View.GONE);
            holder.priceInput.setVisibility(View.VISIBLE);
            holder.priceInput.setText(String.valueOf(item.getPrice()));
            holder.priceInput.requestFocus();
            holder.priceInput.setSelection(holder.priceInput.getText().length());
        });

        holder.priceInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updatePrice(holder, item);
            }
        });

        holder.priceInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                holder.priceInput.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void updatePrice(PriceListViewHolder holder, SolutionPrice item) {
        String inputText = holder.priceInput.getText().toString().trim();

        double newPrice = parseDouble(inputText, -1);
        if (newPrice < 0) {
            holder.priceInput.setError("Invalid amount");
            return;
        }

        item.setPrice(newPrice);

        holder.price.setText(NUM_FMT.format(item.getPrice()));
        holder.priceWithDiscount.setText(NUM_FMT.format(item.getPriceWithDiscount()));
        holder.priceInput.setError(null);

        holder.priceInput.setVisibility(View.GONE);
        holder.price.setVisibility(View.VISIBLE);

        if (changeListener != null) {
            changeListener.onPriceChange(item);
        }
    }

    public static class PriceListViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, discount, priceWithDiscount;
        EditText priceInput;

        public PriceListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);
            priceWithDiscount = itemView.findViewById(R.id.price_with_discount);
            priceInput = itemView.findViewById(R.id.price_input);
        }

        void bind(SolutionPrice item) {
            name.setText(item.getName());
            price.setText(String.format(Locale.getDefault(), "%.2f", item.getPrice()));
            discount.setText(String.format(Locale.getDefault(), "%d%%", (int) (item.getDiscount() > 1 ? item.getDiscount() : (item.getDiscount() * 100))));
            priceWithDiscount.setText(String.format(Locale.getDefault(), "%.2f", item.getPriceWithDiscount()));
            price.setVisibility(View.VISIBLE);
            priceInput.setVisibility(View.GONE);
        }
    }
}
