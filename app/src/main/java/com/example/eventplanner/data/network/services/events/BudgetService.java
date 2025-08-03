package com.example.eventplanner.data.network.services.events;

import com.example.eventplanner.data.model.events.budget.BudgetItemModel;
import com.example.eventplanner.data.model.events.budget.BudgetModel;
import com.example.eventplanner.data.model.events.budget.Amount;

import retrofit2.Call;
import retrofit2.http.*;


public interface BudgetService {
    String BASE_URL = "events/{eventId}/budget";


    @GET(BASE_URL)
    Call<BudgetModel> getEventBudget(@Path("eventId") int eventId);

    @POST(BASE_URL)
    Call<BudgetItemModel> addEventBudgetItem(@Path("eventId") int eventId, @Path("categoryId") int categoryId, @Body Amount item);

    @PUT(BASE_URL + "/{categoryId}")
    Call<BudgetItemModel> updateEventBudgetItem(@Path("eventId") int eventId, @Path("categoryId") int categoryId, @Body Amount item);

    @DELETE(BASE_URL + "/{categoryId}")
    Call<Void> deleteEventBudgetItem(@Path("eventId") int eventId, @Path("categoryId") int categoryId);

}
