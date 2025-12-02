package com.example.photolog_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    CheckBox chkAll, chkUse, chkPrivacy, chkAd;
    EditText signName, signId, signPwd;
    TextView tvError;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 체크박스
        chkAll = findViewById(R.id.chkAll);
        chkUse = findViewById(R.id.chkUse);
        chkPrivacy = findViewById(R.id.chkPrivacy);
        chkAd = findViewById(R.id.chkAd);

        // 입력칸
        signName = findViewById(R.id.signName);
        signName = findViewById(R.id.signId);
        signPwd = findViewById(R.id.signPwd);

        // 에러 문구
        tvError = findViewById(R.id.tvError);

        // 회원가입 버튼
        btnSignup = findViewById(R.id.btnSignup);

        // 로고 클릭 → 로그인 이동
        LinearLayout logoLayout = findViewById(R.id.layout_logo);
        logoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // 전체 선택
        chkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chkUse.setChecked(isChecked);
            chkPrivacy.setChecked(isChecked);
            chkAd.setChecked(isChecked);
        });

        chkUse.setOnCheckedChangeListener((b, c) -> updateAllChecked());
        chkPrivacy.setOnCheckedChangeListener((b, c) -> updateAllChecked());
        chkAd.setOnCheckedChangeListener((b, c) -> updateAllChecked());

        // ⭐ 회원가입 버튼 클릭
        btnSignup.setOnClickListener(v -> checkSignup());
    }

    // 전체 체크 업데이트
    private void updateAllChecked() {
        boolean allChecked = chkUse.isChecked() && chkPrivacy.isChecked() && chkAd.isChecked();
        chkAll.setChecked(allChecked);
    }

    // ⭐ 입력 검증 + 경고문 띄우기
    private void checkSignup() {

        String name = signName.getText().toString().trim();
        String id = signId.getText().toString().trim();
        String pw = signPwd.getText().toString().trim();

        if (name.isEmpty()|| id.isEmpty() || pw.isEmpty()) {
            tvError.setText("모든 칸을 채워주세요!");
            tvError.setVisibility(View.VISIBLE);

            Toast.makeText(this, "모든 칸을 채워주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!chkUse.isChecked() || !chkPrivacy.isChecked()) {
            tvError.setText("필수 약관에 동의해야 합니다.");
            tvError.setVisibility(View.VISIBLE);

            Toast.makeText(this, "필수 약관 동의 필요!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 성공 → 에러 숨기기
        tvError.setVisibility(View.GONE);

        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show();

        // 로그인 페이지로 이동
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
