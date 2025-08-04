package com.example.eventplanner.ui.fragment.budget;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.events.budget.Amount;
import com.example.eventplanner.data.model.events.budget.BudgetItemModel;
import com.example.eventplanner.data.model.events.budget.BudgetModel;
import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.network.ClientUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventBudgetViewModel extends ViewModel {
    private int eventId;
    public final MutableLiveData<BudgetModel> budget = new MutableLiveData<>();
    public final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();


    public void fetchBudget(int eventId) {
        this.eventId = eventId;

        ClientUtils.budgetService.getEventBudget(eventId).enqueue(new Callback<BudgetModel>() {
            @Override
            public void onResponse(@NonNull Call<BudgetModel> call, @NonNull Response<BudgetModel> response) {
                if (response.isSuccessful()) {
                    budget.postValue(response.body());
                } else {
                    error.postValue("Failed to get event budget");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetModel> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void fetchCategories() {
        ClientUtils.categoryService.getAll().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categories.postValue(response.body());
                } else {
                    error.postValue("Failed to fetch categories.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });

    }

    public void addBudgetItem(int categoryId, double amount) {
        ClientUtils.budgetService.addEventBudgetItem(eventId, categoryId, new Amount(amount)).enqueue(new Callback<BudgetItemModel>() {
            @Override
            public void onResponse(@NonNull Call<BudgetItemModel> call, @NonNull Response<BudgetItemModel> response) {
                if (response.isSuccessful()) {
                    fetchBudget(eventId);
                } else {
                    error.postValue("Failed to add budget item");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetItemModel> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void updateBudgetItem(int categoryId, double amount) {
        ClientUtils.budgetService.updateEventBudgetItem(eventId, categoryId, new Amount(amount)).enqueue(new Callback<BudgetItemModel>() {
            @Override
            public void onResponse(@NonNull Call<BudgetItemModel> call, @NonNull Response<BudgetItemModel> response) {
                if (response.isSuccessful()) {
                    fetchBudget(eventId);
                } else {
                    error.postValue("Failed to update budget item");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BudgetItemModel> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void deleteBudgetItem(int categoryId) {
        ClientUtils.budgetService.deleteEventBudgetItem(eventId, categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchBudget(eventId);
                } else {
                    error.postValue("Failed to delete budget item");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

}
