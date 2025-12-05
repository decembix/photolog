package com.example.photolog_front.network;

import com.example.photolog_front.model.ChatMessageRequest;
import com.example.photolog_front.model.ChatMessageResponse;
import com.example.photolog_front.model.DiaryStartResponse;
import com.example.photolog_front.model.FamilyCreateRequest;
import com.example.photolog_front.model.FamilyJoinRequest;
import com.example.photolog_front.model.LoginRequest;
import com.example.photolog_front.model.LoginResponse;
import com.example.photolog_front.model.SignupRequest;
import com.example.photolog_front.model.SignupResponse;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ---------------------- Auth ----------------------
    @POST("/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);

    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);


    // ---------------------- 사진 업로드 ----------------------
    @Multipart
    @POST("/photos/upload-start")
    Call<DiaryStartResponse> uploadPhoto(
            @Part MultipartBody.Part file
    );


    // ---------------------- 챗봇 / 세션 ----------------------
    @POST("/sessions/{session_id}/answer")
    Call<ChatMessageResponse> sendChatAnswer(
            @Path("session_id") int sessionId,
            @Body ChatMessageRequest request
    );

    @POST("/sessions/{session_id}/stop")
    Call<ChatMessageResponse> stopSession(
            @Path("session_id") int sessionId
    );

    @POST("/api/chat")
    Call<ResponseBody> sendUserMessage(@Body JsonObject body);


    // ---------------------- 가족 기능 ----------------------
    @POST("/families")
    Call<Object> createFamily(@Body FamilyCreateRequest request);

    @POST("/families/join")
    Call<Object> joinFamily(@Body FamilyJoinRequest request);
}
