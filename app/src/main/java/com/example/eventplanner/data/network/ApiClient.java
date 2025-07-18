package com.example.eventplanner.data.network;

import com.example.eventplanner.data.network.reports.ReportUserService;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.data.network.services.profiles.OrganizerService;
import com.example.eventplanner.data.network.services.solutions.BookingServiceService;
import com.example.eventplanner.data.network.services.solutions.ServicesService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/") // Za emulator
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ServicesService getServiceApi() {
        return getClient().create(ServicesService.class);
    }

    public static BookingServiceService getBookingServiceApi() {
        return getClient().create(BookingServiceService.class);
    }

    public static EventService getEventService(){
        return getClient().create(EventService.class);
    }

    public static OrganizerService getOrganizerService(){
        return getClient().create(OrganizerService.class);
    }

    public static ReportUserService getReportUserService(){
        return getClient().create(ReportUserService.class);
    }

}
