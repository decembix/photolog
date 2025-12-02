package com.example.photolog_front.network;

import com.example.photolog_front.model.LoginRequest;
import com.example.photolog_front.model.LoginResponse;
import com.example.photolog_front.model.SignupResponse;
import com.google.gson.JsonObject;
import com.example.photolog_front.model.SignupRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/api/chat")
    Call<ResponseBody> sendUserMessage(@Body JsonObject body);

}
