package com.example.photolog_front;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.photolog_front.network.ApiService;
import com.example.photolog_front.network.RetrofitClient;
import com.example.photolog_front.model.DiaryStartResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaryGenerationActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView guideTextView;
    private Button selectPhotoButton;
    private boolean isPhotoSelected = false;
    private Uri selectedImageUri = null;

    private static final int REQUEST_MEDIA_PERMISSION = 101;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImageSelected);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_generation);

        // 로고 클릭 → 홈 이동
        LinearLayout logoLayout = findViewById(R.id.layout_logo);
        logoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        imageView = findViewById(R.id.img_placeholder);
        selectPhotoButton = findViewById(R.id.btn_select_photo);
        guideTextView = findViewById(R.id.tv_guide);

        selectPhotoButton.setOnClickListener(v -> {
            if (isPhotoSelected) {
                uploadImageToServer(selectedImageUri);
            } else {
                openGalleryWithPermission();
            }
        });

        imageView.setOnClickListener(v -> {
            if (!isPhotoSelected) openGalleryWithPermission();
        });
    }

    private void openGalleryWithPermission() {
        if (checkMediaPermission()) {
            galleryLauncher.launch("image/*");
        } else {
            requestMediaPermission();
        }
    }

    private void onImageSelected(Uri uri) {
        if (uri != null) {
            isPhotoSelected = true;
            selectedImageUri = uri;

            imageView.setImageURI(uri);

            try {
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}

            guideTextView.setText("사진 업로드 준비 완료!\n일기 생성을 시작하세요.");
            guideTextView.setGravity(Gravity.CENTER);
            selectPhotoButton.setText("일기 생성 시작");
        }
    }

    private boolean checkMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_MEDIA_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_MEDIA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_MEDIA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            galleryLauncher.launch("image/*");

        } else {
            Toast.makeText(this, "사진 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 🔥 InputStream → byte[] 변환
    private byte[] convertToBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    /* ⭐ 사진 업로드 API */
    private void uploadImageToServer(Uri uri) {

        try {
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String token = prefs.getString("token", null);

            if (token == null) {
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream is = getContentResolver().openInputStream(uri);
            byte[] bytes = convertToBytes(is);

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    bytes
            );

            MultipartBody.Part filePart =
                    MultipartBody.Part.createFormData("file", "photo.png", requestFile);

            ApiService api = RetrofitClient.getApiService(this);

            api.uploadPhoto("Bearer " + token, filePart)
                    .enqueue(new Callback<DiaryStartResponse>() {
                        @Override
                        public void onResponse(Call<DiaryStartResponse> call, Response<DiaryStartResponse> response) {

                            if (!response.isSuccessful()) {
                                Toast.makeText(DiaryGenerationActivity.this,
                                        "업로드 실패 (401 인증 필요?)", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            DiaryStartResponse result = response.body();

                            // ChatbotActivity로 이동
                            Intent intent = new Intent(DiaryGenerationActivity.this, ChatbotActivity.class);
                            intent.putExtra("session_id", result.session_id);
                            intent.putExtra("question", result.question);

                            // ⭐⭐ 반드시 사진 URI 전달!!
                            if (selectedImageUri != null) {
                                intent.putExtra("selected_photo_uri", selectedImageUri.toString());
                            }

                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<DiaryStartResponse> call, Throwable t) {
                            Toast.makeText(DiaryGenerationActivity.this,
                                    "서버 연결 실패!", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "파일 처리 중 오류", Toast.LENGTH_SHORT).show();
        }
    }
}
