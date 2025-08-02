package com.example.eventplanner.ui.fragment.solutions.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.solutions.services.*;
import com.example.eventplanner.data.network.services.solutions.ServiceService;

import java.util.Optional;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceViewModel extends ViewModel {
    private final int _id;
    private final MutableLiveData<ServiceModel> _service = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Void> _deleteSuccess = new MutableLiveData<>();

    private final ServiceService _serviceService;


    public ServiceViewModel(int id, ServiceService serviceService) {
        _id = id;
        _serviceService = serviceService;
        fetchService();
    }


    public void fetchService() {
        _serviceService.getById(_id)
                .enqueue(new Callback<ServiceModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ServiceModel> call, @NonNull Response<ServiceModel> response) {
                        if (response.isSuccessful()) {
                            _service.postValue(response.body());
                        } else {
                            _errorMessage.postValue("Failed to fetch service. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServiceModel> call, @NonNull Throwable t) {
                        _errorMessage.postValue(t.getMessage());
                    }
                });
    }


    public void deleteService() {
        _serviceService.deleteById(_id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            _deleteSuccess.postValue(null);
                            _service.postValue(null);
                        } else {
                            _errorMessage.postValue("Failed to delete service. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        _errorMessage.postValue(t.getMessage());
                    }
                });
    }

/// Update without emitting a change
    public void updateService(Consumer<ServiceModel> updater) {
        ServiceModel service = Optional.ofNullable(_service.getValue()).orElseGet(ServiceModel::new);
        updater.accept(service);
    }

/// flush the update
    public void updateService() {
        Optional.ofNullable(_service.getValue())
                .map(UpdateService::new)
                .ifPresent(this::updateService);
    }


    private void updateService(UpdateService updateRequest) {
        _serviceService.update(updateRequest.getId(), updateRequest).enqueue(new Callback<ServiceModel>() {
            @Override
            public void onResponse(@NonNull Call<ServiceModel> call, @NonNull Response<ServiceModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _service.postValue(response.body());
                } else {
                    _errorMessage.postValue("Failed to update service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServiceModel> call, @NonNull Throwable t) {
                _errorMessage.postValue(t.getMessage());
            }
        });
    }



    public LiveData<ServiceModel> service() {
        return _service;
    }

    public void setService(ServiceModel service) {
        _service.postValue(service);
    }

    public LiveData<String> error() {
        return _errorMessage;
    }
}
