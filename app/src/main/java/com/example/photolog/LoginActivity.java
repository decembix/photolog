package com.example.photolog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etId, etPw;
    TextView tvError;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);

        TextView joinText = findViewById(R.id.tvJoin); // '회원 가입' 텍스트뷰 id
        joinText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = etId.getText().toString().trim();
                String pw = etPw.getText().toString().trim();
                // 포토로그 글씨 + 로고 전체 클릭 시 로그인 화면 이동
                LinearLayout logoLayout = findViewById(R.id.logoLayout);
                logoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                });
                // 예시 로그인 로직
                if (id.equals("test") && pw.equals("1234")) {
                    tvError.setVisibility(View.GONE); // 오류 메시지 숨김
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    tvError.setText("아이디 또는 비밀번호가 틀렸습니다.");
                    tvError.setVisibility(View.VISIBLE); // 메시지 보이기
                }
            }
        });
    }
}
