package com.example.photolog_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;


public class SignupActivity extends AppCompatActivity {
    CheckBox chkAll, chkUse, chkPrivacy, chkAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 모두선택 구현
        chkAll = findViewById(R.id.chkAll);
        chkUse = findViewById(R.id.chkUse);
        chkPrivacy = findViewById(R.id.chkPrivacy);
        chkAd = findViewById(R.id.chkAd);


        chkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chkUse.setChecked(isChecked);
            chkPrivacy.setChecked(isChecked);
            chkAd.setChecked(isChecked);
        });


        chkUse.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllChecked());
        chkPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllChecked());
        chkAd.setOnCheckedChangeListener((buttonView, isChecked) -> updateAllChecked());

        // 🟤 포토로그 로고 클릭 시 로그인 화면 이동
        LinearLayout logoLayout = findViewById(R.id.logoLayout);
        logoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void updateAllChecked() {
        boolean allChecked = chkUse.isChecked() && chkPrivacy.isChecked() && chkAd.isChecked();
        chkAll.setChecked(allChecked);
    }
}
