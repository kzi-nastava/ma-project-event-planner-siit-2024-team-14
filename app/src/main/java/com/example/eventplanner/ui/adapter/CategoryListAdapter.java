package com.example.eventplanner.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.databinding.CategoryCardBinding;

//import java.util.Arrays;
//import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        Category category = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_card, parent, false);
            convertView.setTag(CategoryCardBinding.bind(convertView));
        }

        CategoryCardBinding cardViewBinding = (CategoryCardBinding) convertView.getTag();

        if (category != null) {
            cardViewBinding.twCategoryName.setText(category.getName());
            cardViewBinding.twCategoryDescription.setText(category.getDescription());

            cardViewBinding.actionDelete.setOnClickListener(v -> {
                Log.i("EventPlanner", String.format("Delete category [%d].", category.getId()));
                new AlertDialog.Builder(parent.getContext())
                        .setMessage(String.format("Are you sure you want to delete category '%s'?", category.getName()))
                        .setPositiveButton(
                                "Yes",
                                (dialogInterface, i) -> ClientUtils.categoryService.deleteCategory(category.getId())
                                        .enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    remove(category);
                                                    Toast.makeText(parent.getContext(), "Successfully deleted category.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(parent.getContext(), "Failed to delete category. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                                Toast.makeText(parent.getContext(), "Failed to delete category: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                        )
                        .setNegativeButton("No", (dialogInterface, i) -> {

                        })
                        .show();
            });

            cardViewBinding.actionEdit.setOnClickListener(v -> {
                Log.i("EventPlanner", String.format("Edit category [%d].", category.getId()));
            });
        }

        return convertView;
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
