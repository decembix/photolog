package com.example.photolog_front;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryResultActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private TextView tvTitle, tvDate;
    private EditText etContent;
    private AppCompatButton btnEdit, btnDone, btnRetry;
    private Uri photoUri;
    private String diaryTitle, diaryContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_result);

        ivPhoto = findViewById(R.id.iv_photo);
        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);
        etContent = findViewById(R.id.et_content);
        btnEdit = findViewById(R.id.btn_edit);
        btnDone = findViewById(R.id.btn_done);
        btnRetry = findViewById(R.id.btn_retry);

        etContent.setFocusable(false);
        etContent.setFocusableInTouchMode(false);
        etContent.setCursorVisible(false);

        // 데이터 수신
        Intent intent = getIntent();
        String photoUriString = intent.getStringExtra("photo_uri");
        diaryTitle = intent.getStringExtra("diary_title");
        diaryContent = intent.getStringExtra("diary_content");

        if (photoUriString != null) {
            photoUri = Uri.parse(photoUriString);
            ivPhoto.setImageURI(photoUri);
        }
        if (diaryTitle != null) tvTitle.setText(diaryTitle);
        if (diaryContent != null) etContent.setText(diaryContent);

        String currentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // 수정 버튼 -> 내용 수정 가능
        btnEdit.setOnClickListener(v -> {
            etContent.setFocusable(true);
            etContent.setFocusableInTouchMode(true);
            etContent.setCursorVisible(true);
            etContent.requestFocus();
            Toast.makeText(this, "내용 수정 가능", Toast.LENGTH_SHORT).show();
        });

        // 로고 클릭 시 → 나가기 확인 다이얼로그 표시
        findViewById(R.id.layout_logo).setOnClickListener(v -> showExitConfirmDialog());

        // 완료 버튼 → 메인으로 이동 + 데이터 전달
        btnDone.setOnClickListener(v -> {
            Toast.makeText(this, "일기가 저장되었습니다!", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (photoUri != null) {
                mainIntent.putExtra("photo_uri", photoUri.toString());
            }
            mainIntent.putExtra("diary_title", tvTitle.getText().toString());
            mainIntent.putExtra("diary_content", etContent.getText().toString());
            startActivity(mainIntent);
            finish();
        });

        // 다시 작성 버튼 → 기존 로직 유지
        btnRetry.setOnClickListener(v -> showRetryDialog());
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exit_confirm, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 버튼 연결
        AppCompatButton btnNo = dialogView.findViewById(R.id.btn_no);
        AppCompatButton btnYes = dialogView.findViewById(R.id.btn_yes);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });

        dialog.show();
    }

    // showRetryDialogCustom() → showRetryDialog() 로 이름 변경
    private void showRetryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_retry, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        AppCompatButton btnNo = dialogView.findViewById(R.id.btn_no);
        AppCompatButton btnYes = dialogView.findViewById(R.id.btn_yes);

        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            showRetryOptions();
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void showRetryOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_retry_options, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        AppCompatButton btnSelectPhoto = dialogView.findViewById(R.id.btn_select_photo);
        AppCompatButton btnRewriteDiary = dialogView.findViewById(R.id.btn_rewrite_diary);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnSelectPhoto.setOnClickListener(v -> {
            dialog.dismiss();
            Intent photoIntent = new Intent(this, DiaryGenerationActivity.class);
            photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            photoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(photoIntent);
            finish();
        });

        btnRewriteDiary.setOnClickListener(v -> {
            dialog.dismiss();
            Intent chatIntent = new Intent(this, ChatbotActivity.class);
            chatIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (photoUri != null) {
                chatIntent.putExtra("selected_photo_uri", photoUri.toString());
            }
            chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(chatIntent);
            finish();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

}
