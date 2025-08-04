package com.example.eventplanner.ui.fragment.solutions.pricelist;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.data.model.Page;
import com.example.eventplanner.data.model.solutions.SolutionPrice;
import com.example.eventplanner.data.model.solutions.products.ProductModel;
import com.example.eventplanner.data.model.solutions.services.ServiceModel;
import com.example.eventplanner.data.model.solutions.services.UpdateService;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.ClientUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProviderPriceListViewModel extends ViewModel {
    MutableLiveData<List<SolutionPrice>> solutions = new MutableLiveData<>();
    MutableLiveData<byte[]> priceListPdf = new MutableLiveData<>();
    MutableLiveData<String> errorMsg = new MutableLiveData<>();



    public void fetchProviderSolutions() {
        if (!ClientUtils.authService.hasRole(ROLE_PROVIDER)) {
            errorMsg.postValue("Cannot get provider solutions, user is not provider");
            return;
        }

        UserModel user = Objects.requireNonNull(ClientUtils.authService.getUser());

        ClientUtils.priceListService.getProviderSolutions(user.getId(), Map.of("page", String.valueOf(0), "size", String.valueOf(100)))
                .enqueue(new Callback<Page<SolutionPrice>>() {
                    @Override
                    public void onResponse(@NonNull Call<Page<SolutionPrice>> call, @NonNull Response<Page<SolutionPrice>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            solutions.postValue(response.body().getContent());
                        else
                            errorMsg.postValue("Failed to fetch provider solutions. Code: " + response.code());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Page<SolutionPrice>> call, @NonNull Throwable t) {
                        errorMsg.postValue(t.getMessage());
                    }
                });
    }


    public void updateSolutionPrice(SolutionPrice price) {
        double value = price.getPrice();

        if (value < 0) {
            errorMsg.postValue("Price must be non-negative.");
            return;
        }

        switch (price.getSolutionType().toLowerCase()) {
            case "service":
            {
                UpdateService service = new UpdateService();
                service.setId(price.getId());
                service.setPrice(value);
                updateSolutionPrice(service);
                break;
            }
            case "product":
            {
                ProductModel product = new ProductModel();
                product.setId(price.getId());
                product.setPrice(value);
                updateSolutionPrice(product);
                break;
            }
            default:
                errorMsg.postValue("Unknown solution type: " + price.getSolutionType().toLowerCase());
        }
    }

    private void updateSolutionPrice(ProductModel product) {
        ClientUtils.productService.update(product.getId(), product).enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(@NonNull Call<ProductModel> call, @NonNull Response<ProductModel> response) {
                if (!response.isSuccessful())
                    errorMsg.postValue("Failed to update product. Code: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ProductModel> call, @NonNull Throwable t) {
                errorMsg.postValue(t.getMessage());
            }
        });
    }

    private void updateSolutionPrice(UpdateService service) {
        ClientUtils.serviceService.update(service.getId(), service).enqueue(new Callback<ServiceModel>() {
            @Override
            public void onResponse(@NonNull Call<ServiceModel> call, @NonNull Response<ServiceModel> response) {
                if (!response.isSuccessful())
                    errorMsg.postValue("Failed to update service. Code: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ServiceModel> call, @NonNull Throwable t) {
                errorMsg.postValue(t.getMessage());
            }
        });
    }


    public void export2Pdf() {
        ClientUtils.priceListService.exportProviderPriceList()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            errorMsg.postValue("Failed to export to pdf. Code: " + response.code());
                            return;
                        }

                        try (ResponseBody body = response.body()) {
                            if (body != null) {
                                priceListPdf.postValue(body.bytes());
                                return;
                            }
                        } catch (IOException e) {
                            // pass
                        }

                        errorMsg.postValue("Failed to export to pdf.");
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        errorMsg.postValue(t.getMessage());
                    }
                });
    }


}
