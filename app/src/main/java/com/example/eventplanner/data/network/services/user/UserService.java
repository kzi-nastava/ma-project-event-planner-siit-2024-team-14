package com.example.eventplanner.data.network.services.user;

import com.example.eventplanner.data.model.login.LoginModel;
import com.example.eventplanner.data.model.login.LoginResponseModel;
import com.example.eventplanner.ui.fragment.InvitationRegisterFragment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("users/login")
    Call<LoginResponseModel> login(@Body LoginModel loginModel);

    @POST("invitations/register")
    Call<InvitationRegisterFragment.ApiResponse> registerInvitation(@Body InvitationRegisterFragment.InvitationRegisterRequest request);

}
