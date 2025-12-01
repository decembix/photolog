package com.example.photolog_front;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/chat")
    Call<ResponseBody> sendUserMessage(@Body JsonObject body);
}
