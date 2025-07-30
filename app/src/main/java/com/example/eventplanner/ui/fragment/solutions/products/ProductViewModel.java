package com.example.eventplanner.ui.fragment.solutions.products;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.solutions.products.Product;
import com.example.eventplanner.data.network.ClientUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {
    private final MutableLiveData<Product> _product = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Void> _deleteSuccess = new MutableLiveData<>();


    public void fetchProduct(int id) {
        ClientUtils.productService.getById(id)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                        if (response.isSuccessful()) {
                            _product.postValue(response.body());
                        } else {
                            _errorMessage.postValue("Failed to fetch product. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                        _errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void deleteProduct(int id) {
        ClientUtils.productService.deleteById(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            _deleteSuccess.postValue(null);
                        } else {
                            _errorMessage.postValue("Failed to delete product. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        _errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public LiveData<Product> product() {
        return _product;
    }

    public LiveData<String> errorMessage() {
        return _errorMessage;
    }

}
