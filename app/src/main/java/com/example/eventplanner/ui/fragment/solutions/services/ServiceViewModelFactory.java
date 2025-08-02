package com.example.eventplanner.ui.fragment.solutions.services;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.data.network.ClientUtils;


public class ServiceViewModelFactory implements ViewModelProvider.Factory {

    private final int _serviceId;



    public ServiceViewModelFactory(int serviceId) {
        _serviceId = serviceId;
    }


    public String getKey() {
        return ServiceViewModelFactory.getKey(_serviceId);
    }

    @SuppressLint("DefaultLocale")
    public static String getKey(Integer serviceId) {
        return String.format("%s:%d", ServiceViewModel.class.getSimpleName(), serviceId);
    }


    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ServiceViewModel.class)) {
            return (T) new ServiceViewModel(_serviceId, ClientUtils.serviceService);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

}
