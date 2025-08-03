package com.example.eventplanner.data.network.services.user;

import com.example.eventplanner.data.model.login.LoginModel;
import com.example.eventplanner.data.model.login.LoginResponseModel;
import com.example.eventplanner.ui.fragment.InvitationRegisterFragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UserService {
    @POST("users/login")
    Call<LoginResponseModel> login(@Body LoginModel loginModel);

    @POST("invitations/register")
    Call<InvitationRegisterFragment.ApiResponse> registerInvitation(@Body InvitationRegisterFragment.InvitationRegisterRequest request);

    @GET
    Call<ResponseBody> activateAccount(@Url String url);

}
