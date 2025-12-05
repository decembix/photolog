package com.example.photolog_front.network;

import com.example.photolog_front.model.LoginRequest;
import com.example.photolog_front.model.LoginResponse;
import com.example.photolog_front.model.SignupResponse;
import com.example.photolog_front.model.DiaryStartResponse;
import com.example.photolog_front.model.ChatMessageResponse;
import com.example.photolog_front.model.ChatMessageRequest;
import com.google.gson.JsonObject;
import com.example.photolog_front.model.SignupRequest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/api/chat")
    Call<ResponseBody> sendUserMessage(@Body JsonObject body);
    @Multipart
    @POST("/photos/upload-start")
    Call<DiaryStartResponse> uploadPhoto(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file
    );
    @POST("/diary/message")
    Call<ChatMessageResponse> sendChatMessage(@Body ChatMessageRequest request);
    @POST("/chat")
    Call<ChatMessageResponse> sendUserMessage(
            @Body ChatMessageRequest body  // ← 여기!!!
    );
    @POST("/sessions/{session_id}/answer")
    Call<ChatMessageResponse> sendChatAnswer(
            @Path("session_id") int sessionId,
            @Body ChatMessageRequest request
    );



}
