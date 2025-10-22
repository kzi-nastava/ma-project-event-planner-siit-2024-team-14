package com.example.eventplanner.ui.fragment.solutions;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.Page;
import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.model.solutions.FilterParams;
// import com.example.eventplanner.data.model.solutions.OfferingModel;
import com.example.eventplanner.data.model.solutions.OfferingModel;
import com.example.eventplanner.data.model.solutions.services.ServiceModel;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.auth.AuthService;
import com.example.eventplanner.data.network.services.solutions.CategoryService;
import com.example.eventplanner.data.network.services.solutions.ServiceService;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SolutionsViewModel extends ViewModel {
    final MutableLiveData<Page<OfferingModel>> solutions = new MutableLiveData<>();
    MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    final MutableLiveData<String> error = new MutableLiveData<>();

    private final AuthService auth = ClientUtils.authService;
    private final ServiceService solutionService = ClientUtils.serviceService;
    private final CategoryService categoryService = ClientUtils.categoryService;



    public void fetchSolutions() {
        fetchSolutions(new FilterParams());
    }

    @SuppressWarnings("unchecked")
    public void fetchSolutions(FilterParams params) {
        solutionService.getAll(params.asMap())
                .enqueue(new Callback<Page<ServiceModel>>() {
                    @Override public void onResponse(@NonNull Call<Page<ServiceModel>> call,
                                                     @NonNull Response<Page<ServiceModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Post the whole Page; unsafe cast is OK due to type erasure and because ServiceModel extends OfferingModel
                            @SuppressWarnings("unchecked")
                            Page<OfferingModel> page = (Page<OfferingModel>)(Page<?>) response.body();
                            solutions.postValue(page);
                        } else {
                            error.postValue(String.format(Locale.getDefault(),
                                    "Failed to fetch solutions. Code: %d. Request URL: <%s>",
                                    response.code(), call.request().url()));
                        }
                    }
                    @Override public void onFailure(@NonNull Call<Page<ServiceModel>> call, @NonNull Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }

    public void fetchProviderSolutions(FilterParams params) {
        UserModel user = auth.getUser();
        if (user == null || !ROLE_PROVIDER.equalsIgnoreCase(user.getRole())) {
            error.postValue("User not provider");
            return;
        }

        params.setProvider(user.getId());
        fetchSolutions(params);
    }


    public void fetchProviderSolutions() {
        fetchProviderSolutions(new FilterParams());
    }

    public void fetchCategories() {
        categoryService.getAll().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.postValue(response.body());
                } else {
                    error.postValue("Failed to fetch categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

}