package com.example.photolog_front.network;

import com.example.photolog_front.model.LoginRequest;
import com.example.photolog_front.model.LoginResponse;
import com.example.photolog_front.model.SignupResponse;
import com.example.photolog_front.model.DiaryStartResponse;
import com.example.photolog_front.model.ChatMessageResponse;
import com.example.photolog_front.model.ChatMessageRequest;
import com.example.photolog_front.model.SignupRequest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/signup")
    Call<SignupResponse> signup(@Body SignupRequest request);

    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 사진 업로드 (⭐ 토큰은 Interceptor가 넣음)
    @Multipart
    @POST("/photos/upload-start")
    Call<DiaryStartResponse> uploadPhoto(
            @Part MultipartBody.Part file
    );

    @POST("/sessions/{session_id}/answer")
    Call<ChatMessageResponse> sendChatAnswer(
            @Path("session_id") int sessionId,
            @Body ChatMessageRequest request
    );
}
