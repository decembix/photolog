package com.example.photolog_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {

    // 프로필 및 정보
    private ImageView profile;
    private TextView tvNickname, tvDiaryCount, tvFamilyCount;

    // 가족 1
    private ImageView profileFamily1;
    private TextView tvNicknameFamily1, tvDiaryCountFamily1;

    // 가족 2
    private ImageView profileFamily2;
    private TextView tvNicknameFamily2, tvDiaryCountFamily2;

    // 로고 레이아웃
    private LinearLayout layoutLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        initViews();
        setListeners();
        loadUserData();
    }

    // XML과 연결
    private void initViews() {

        layoutLogo = findViewById(R.id.layout_logo);

        profile = findViewById(R.id.profile);
        tvNickname = findViewById(R.id.tvNickname);
        tvDiaryCount = findViewById(R.id.tvDiaryCount);
        tvFamilyCount = findViewById(R.id.tvFamilyCount);

        profileFamily1 = findViewById(R.id.profile_family1);
        tvNicknameFamily1 = findViewById(R.id.tvNickname_family1);
        tvDiaryCountFamily1 = findViewById(R.id.tvDiaryCount_family1);

        profileFamily2 = findViewById(R.id.profile_family2);
        tvNicknameFamily2 = findViewById(R.id.tvNickname_family2);
        tvDiaryCountFamily2 = findViewById(R.id.tvDiaryCount_family2);
    }

    // 클릭 리스너
    private void setListeners() {

        // 로고 클릭 → 메인 화면 이동
        layoutLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // TODO: 가족 프로필 클릭 시 가족 상세 페이지 이동
        // profileFamily1.setOnClickListener(...)
        // profileFamily2.setOnClickListener(...)
    }

    // 임시 데이터 로딩
    private void loadUserData() {

        // TODO: 실제 데이터는 서버/SharedPreferences에서 불러오기

        // 메인 프로필
        tvNickname.setText("수희");
        tvDiaryCount.setText("작성한 일기 수 : 12");
        tvFamilyCount.setText("추가한 가족 수 : 2");

        // 가족 1
        tvNicknameFamily1.setText("엄마");
        tvDiaryCountFamily1.setText("작성한 일기 수 : 5");

        // 가족 2
        tvNicknameFamily2.setText("아빠");
        tvDiaryCountFamily2.setText("작성한 일기 수 : 4");
    }
}
