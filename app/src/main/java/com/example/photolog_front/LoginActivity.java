package com.example.photolog_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etId, etPw;
    TextView tvError, tvFindId, tvFindPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);

        tvFindId = findViewById(R.id.tvFindId);               // 아이디 찾기 버튼
        tvFindPassword = findViewById(R.id.tvFindPassword);   // 비밀번호 찾기 버튼

        TextView joinText = findViewById(R.id.tvJoin);        // '회원 가입' 텍스트
        joinText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // 🔹 아이디 찾기 클릭 시 → activity_find_id 로 이동
        tvFindId.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
            startActivity(intent);
        });

        // 🔹 비밀번호 찾기 클릭 시 → activity_find_pwd 로 이동
        tvFindPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FindPwdActivity.class);
            startActivity(intent);
        });

        // 🔹 로그인 버튼 클릭 시
        btnLogin.setOnClickListener(v -> {

            String id = etId.getText().toString().trim();
            String pw = etPw.getText().toString().trim();

            // 임시 로그인 로직
            if (id.equals("test") && pw.equals("1234")) {
                tvError.setVisibility(View.GONE);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                tvError.setText("아이디 또는 비밀번호가 틀렸습니다.");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}
