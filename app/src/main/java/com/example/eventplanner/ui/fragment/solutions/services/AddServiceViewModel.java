package com.example.eventplanner.ui.fragment.solutions.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.model.solutions.services.CreateService;
import com.example.eventplanner.data.model.solutions.services.ServiceModel;
import com.example.eventplanner.data.network.ClientUtils;


import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddServiceViewModel extends ViewModel {
    private final MutableLiveData<CreateService> _service = new MutableLiveData<>(new CreateService());
    private final MutableLiveData<ServiceModel> _createdService = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();



    public AddServiceViewModel() {
        fetchCategories();
    }



    public void createService() {
        if (!validate())
            return;

        CreateService createService = _service.getValue();

        ClientUtils.serviceService.create(createService).enqueue(new Callback<ServiceModel>() {
            @Override
            public void onResponse(@NonNull Call<ServiceModel> call, @NonNull Response<ServiceModel> response) {
                if (response.isSuccessful()) {
                    _createdService.postValue(response.body());
                } else {
                    _errorMessage.postValue("Failed to create service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServiceModel> call, @NonNull Throwable t) {
                _errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void fetchCategories() {
        ClientUtils.categoryService.getAll().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    _categories.postValue(response.body());
                } else {
                    _errorMessage.postValue("Failed to fetch categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                _errorMessage.postValue(t.getMessage());
            }
        });
    }


    public boolean validate() {
        CreateService service = _service.getValue();

        if (service == null) {
            _errorMessage.postValue("No service data provided.");
            return false;
        }

        String name = service.getName();
        if (name == null || name.isBlank() || name.length() < 3) {
            _errorMessage.postValue(String.format("'%s' is not a valid service name. Name must be at least 3 characters.", name));
            return false;
        }

        Category category = service.getCategory();
        if (category == null || category.getId() == null && category.getName() == null) {
            _errorMessage.postValue("You must select an existent category or enter a valid category name.");
            return false;
        }

        Double price = service.getPrice();
        if (price == null || price < 0) {
            _errorMessage.postValue(String.format(Locale.getDefault(), "Invalid price '%.2f'. Price must be non-negative.", price != null ? price : 0.0));
            return false;
        }

        Double discount = service.getDiscount();
        if (discount == null || discount < 0 || discount > 100) {
            _errorMessage.postValue(String.format(Locale.getDefault(), "Discount must be inside interval [0, 100]. Not '%.2f'.", discount != null ? discount : -1));
            return false;
        }

        return true;
    }


    public LiveData<CreateService> service() {
        return _service;
    }

    public void setService(@NonNull CreateService service) {
        _service.postValue(Objects.requireNonNull(service));
    }

    public <T> void updateService(BiConsumer<CreateService, T> setter, T value) {
        CreateService service = _service.getValue();
        setter.accept(service, value);
    }

    public <T> void updateService(BiConsumer<CreateService, T> setter, T value, boolean notify) {
        updateService(setter, value);

        if (notify)
            _service.postValue(_service.getValue());
    }


    public LiveData<String> error() {
        return _errorMessage;
    }

    public LiveData<List<Category>> categories() {
        return _categories;
    }

    public LiveData<ServiceModel> createdService() {
        return _createdService;
    }

}
