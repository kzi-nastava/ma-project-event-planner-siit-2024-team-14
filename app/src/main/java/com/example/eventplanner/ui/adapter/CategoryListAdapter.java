package com.example.eventplanner.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.databinding.CategoryCardBinding;

//import java.util.Arrays;
//import java.util.Collection;
import java.util.List;


public class CategoryListAdapter extends ArrayAdapter<Category> {

    //private List<Category> categories;
    //private Context context;


    public CategoryListAdapter(Context context, List<Category> categories) {
        super(context, R.layout.category_card, categories);
        //this.categories = categories;
        //this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null ?
                convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.category_card, parent, false);

        CategoryCardBinding cardViewBinding = CategoryCardBinding.bind(view);
        Category category = getItem(position);

        if (category != null) {
            cardViewBinding.twCategoryName.setText(category.getName());
            cardViewBinding.twCategoryDescription.setText(category.getDescription());

            // TODO: Check if the adapter should set up these listeners
            cardViewBinding.actionDelete.setOnClickListener(v -> {
                Log.i("EventPlanner", String.format("Delete category [%d].", category.getId()));
            });

            cardViewBinding.actionEdit.setOnClickListener(v -> {
                Log.i("EventPlanner", String.format("Edit category [%d].", category.getId()));
            });
        }

        return cardViewBinding.getRoot();
    }

    /*
    @Override
    public int getCount() {
        return categories.size();
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        try {
            return categories.get(position);
        } catch (IndexOutOfBoundsException ex) {
            Log.w("EventPlanner", "Tried to access category at position [" + position + "], but position index out of bounds.");
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void clear() {
        categories.clear();
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Category... categories) {
        this.categories.addAll(Arrays.asList(categories));
        notifyDataSetChanged();
    }

    @Override
    public void addAll(@NonNull Collection<? extends Category> categories) {
        this.categories.addAll(categories);
        notifyDataSetChanged();
    }

    @Override
    public void add(@Nullable Category category) {
        this.categories.add(category);
        notifyDataSetChanged();
    }*/
}
