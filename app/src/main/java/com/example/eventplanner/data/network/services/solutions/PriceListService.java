package com.example.eventplanner.data.network.services.solutions;

import com.example.eventplanner.data.model.Page;
import com.example.eventplanner.data.model.solutions.SolutionPrice;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface PriceListService {

    @GET("providers/{id}/solutions")
    Call<Page<SolutionPrice>> getProviderSolutions(@Path("id") int id);

    @GET("providers/{id}/solutions")
    Call<Page<SolutionPrice>> getProviderSolutions(@Path("id") int id, @QueryMap Map<String, String> params);

    @GET("price-list.pdf")
    @Headers("Accept: application/pdf")
    Call<ResponseBody> exportProviderPriceList();

}
