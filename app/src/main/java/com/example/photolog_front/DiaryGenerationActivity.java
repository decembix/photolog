package com.example.photolog_front;

import android.Manifest;
import android.content.Intent;
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

public class DiaryGenerationActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView guideTextView;
    private Button selectPhotoButton;
    private boolean isPhotoSelected = false;
    private Uri selectedImageUri = null;

    //갤러리 실행 런처
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImageSelected);

    // 권한 요청 코드
    private static final int REQUEST_MEDIA_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_generation);

        //로고 클릭 시 홈으로 이동
        LinearLayout logoLayout = findViewById(R.id.layout_logo);
        logoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        imageView = findViewById(R.id.img_placeholder);
        selectPhotoButton = findViewById(R.id.btn_select_photo);
        guideTextView = findViewById(R.id.tv_guide);

        //[사진 선택] 버튼 클릭
        selectPhotoButton.setOnClickListener(v -> {
            if (isPhotoSelected) {
                // 사진 선택이 완료됐다면 챗봇 페이지로 이동
                Intent intent = new Intent(DiaryGenerationActivity.this, ChatbotActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("selected_photo_uri", selectedImageUri.toString());
                startActivity(intent);
            } else {
                // 아직 사진 선택 안 했으면 권한 확인 후 갤러리 열기
                openGalleryWithPermission();
            }
        });

        // 이미지 클릭 시에도 갤러리 열기
        imageView.setOnClickListener(v -> {
            if (!isPhotoSelected) {
                openGalleryWithPermission();
            }
        });
    }

    /* 권한 확인 후 갤러리 열기 */
    private void openGalleryWithPermission() {
        if (checkMediaPermission()) {
            galleryLauncher.launch("image/*");
        } else {
            requestMediaPermission();
        }
    }

    /* 갤러리에서 이미지 선택 후 콜백*/
    private void onImageSelected(Uri uri) {
        if (uri != null) {
            isPhotoSelected = true;
            selectedImageUri = uri;
            imageView.setImageURI(uri);

            // 핵심 부분
            try {
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException | IllegalArgumentException e) {
                // 일부 URI는 persistable 권한이 허용되지 않으므로 예외 무시
                e.printStackTrace();
            }


            guideTextView.setText("사진 업로드 완료!\n일기를 작성하세요.");
            guideTextView.setGravity(Gravity.CENTER);
            selectPhotoButton.setText("일기 작성");
        }
    }


    /* 안드로이드 버전에 맞춰 미디어 권한 확인 */
    private boolean checkMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /* 권한 요청*/
    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_MEDIA_PERMISSION
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_MEDIA_PERMISSION
            );
        }
    }

    /*권한 요청 결과 처리*/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용 시 갤러리 열기
                galleryLauncher.launch("image/*");
            } else {
                Toast.makeText(this, "사진 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
