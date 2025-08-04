package com.example.eventplanner.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.network.ClientUtils;

import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesViewModel extends ViewModel {

    private final MutableLiveData<Collection<Category>> categories = new MutableLiveData<>(List.of());
    private final MutableLiveData<Integer> deleted = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");



    public LiveData<Collection<Category>> getCategories() {
        return categories;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getDeleted() {
        return deleted;
    }


    public void loadCategories() {
        ClientUtils.categoryService.getAll().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categories.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void deleteCategory(Category category) {
        ClientUtils.categoryService.deleteById(category.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful())
                    deleted.postValue(category.getId());
                else
                    errorMessage.postValue("Failed to delete category " + category.getId() + ". Code: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }
}