package com.example.eventplanner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.databinding.CategoryCardBinding;

import java.util.Arrays;
import java.util.stream.Collectors;


public class CategoryListAdapter extends ArrayAdapter<Category> {

    public interface OnCategoryClickedListener {
        void onCategoryDelete(Category category);
        void onCategoryUpdate(Category category);
    }

    private OnCategoryClickedListener listener;

    public void setOnCategoryClickedListener(OnCategoryClickedListener listener) {
        this.listener = listener;
    }

    //private List<Category> categories;
    //private Context context;


    public CategoryListAdapter(Context context, Category... categories) {
        super(context, R.layout.category_card, Arrays.stream(categories).collect(Collectors.toList()));
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
                if (listener != null)
                    listener.onCategoryDelete(category);
            });

            cardViewBinding.actionEdit.setOnClickListener(v -> {
                if (listener != null)
                    listener.onCategoryUpdate(category);
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
