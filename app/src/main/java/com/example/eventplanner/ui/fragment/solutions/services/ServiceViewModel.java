package com.example.eventplanner.ui.fragment.solutions.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.solutions.services.*;
import com.example.eventplanner.data.network.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceViewModel extends ViewModel {
    private final MutableLiveData<ServiceModel> _service = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Void> _deleteSuccess = new MutableLiveData<>();



    public void fetchService(int id) {
        ClientUtils.serviceService.getById(id)
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


    public void deleteService(int id) {
        ClientUtils.serviceService.deleteById(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            _deleteSuccess.postValue(null);
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


    public LiveData<ServiceModel> service() {
        return _service;
    }

    public LiveData<String> errorMessage() {
        return _errorMessage;
    }
}
