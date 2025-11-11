package com.example.photolog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 포토로그 글씨 + 로고 전체 클릭 시 로그인 화면 이동
        LinearLayout logoLayout = findViewById(R.id.logoLayout);
        logoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // 1️⃣ 회원가입 텍스트뷰 찾기 (activity_main.xml에 있는 회원가입 TextView의 id와 동일해야 함)
        TextView signUpText = findViewById(R.id.tvSignUp);

        // 2️⃣ 클릭 이벤트 설정
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3️⃣ SignUpActivity로 이동
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
