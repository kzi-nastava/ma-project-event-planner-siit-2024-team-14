package com.example.eventplanner.ui.fragment.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.offerings.categories.CategoryService;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCategoryViewModel extends ViewModel {
    private final MutableLiveData<Category> category = new MutableLiveData<>();
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();

    private final CategoryService categories = ClientUtils.categoryService;




    public LiveData<Category> getCategory() { return category; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<Boolean> getSuccess() { return success; }

    public void setCategory(Category category) {
        this.category.setValue(category);
    }

    public void setCategoryName(String newName) {
        Category oldState = category.getValue();
        if (oldState != null) {
            oldState.setName(newName);
            category.postValue(oldState);
        }
    }

    public void setCategoryDescription(String newDescription) {
        Category oldState = category.getValue();
        if (oldState != null) {
            oldState.setDescription(newDescription);
            category.postValue(oldState);
        }
    }

    public void setCategoryId(int id) {
        Category oldState = category.getValue();
        if (oldState != null) {
            oldState.setId(id);
            category.postValue(oldState);
        }
    }


    public void addNewCategory(Category category) {
        categories.addCategory(Objects.requireNonNull(category))
                .enqueue(new Callback<Category>() {
                    @Override
                    public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                        if (response.isSuccessful()) {
                            success.postValue(true);
                        } else {
                            errorMsg.postValue(String.format(Locale.getDefault(),"Failed to add new category. Code: %d", response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                        errorMsg.postValue(t.getMessage());
                    }
                });
    }


    public void updateCategory(Category category) {
        categories.updateCategory(Objects.requireNonNull(category))
                .enqueue(new Callback<Category>() {
                    @Override
                    public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                        if (response.isSuccessful()) {
                            success.postValue(true);
                        } else {
                            errorMsg.postValue(String.format(Locale.getDefault(),"Failed to update category. Code: %d", response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                        errorMsg.postValue(t.getMessage());
                    }
                });
    }

    public void submitCategory() {
        Category category = Objects.requireNonNull(this.category.getValue(), "No category data");

        if (category.getId() != null) {
            updateCategory(category);
        } else {
            addNewCategory(category);
        }
    }
}