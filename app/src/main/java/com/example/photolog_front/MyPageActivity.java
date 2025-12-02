package com.example.photolog_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {

    // 메인 프로필 & 유저 정보
    private ImageView profile;
    private TextView tvNickname, tvDiaryCount, tvFamilyCount;

    // 가족 1
    private LinearLayout familyBox1Container;
    private ImageView profileFamily1;
    private TextView tvNicknameFamily1, tvDiaryCountFamily1;

    // 가족 2
    private LinearLayout familyBox2Container;
    private ImageView profileFamily2;
    private TextView tvNicknameFamily2, tvDiaryCountFamily2;

    // 가족 추가 버튼
    private LinearLayout layoutAddFamily;

    // 로고 → 홈 이동
    private LinearLayout layoutLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        initViews();
        setListeners();
        loadUserData();
    }

    // XML 연결
    private void initViews() {

        layoutLogo = findViewById(R.id.layout_logo);

        // 메인 프로필
        profile = findViewById(R.id.profile);
        tvNickname = findViewById(R.id.tvNickname);
        tvDiaryCount = findViewById(R.id.tvDiaryCount);
        tvFamilyCount = findViewById(R.id.tvFamilyCount);

        // 가족 1 박스와 내부 요소들
        familyBox1Container = findViewById(R.id.family_box_1);
        profileFamily1 = findViewById(R.id.profile_family1);
        tvNicknameFamily1 = findViewById(R.id.tvNickname_family1);
        tvDiaryCountFamily1 = findViewById(R.id.tvDiaryCount_family1);

        // 가족 2 박스와 내부 요소들
        familyBox2Container = findViewById(R.id.family_box_2);
        profileFamily2 = findViewById(R.id.profile_family2);
        tvNicknameFamily2 = findViewById(R.id.tvNickname_family2);
        tvDiaryCountFamily2 = findViewById(R.id.tvDiaryCount_family2);

        // 가족 추가 버튼
        layoutAddFamily = findViewById(R.id.layout_add_family);
    }

    // 클릭 기능
    private void setListeners() {

        // 로고 → MainActivity 이동
        layoutLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // 가족 추가 버튼 클릭
        layoutAddFamily.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, MakeGroupActivity.class);
            startActivity(intent);
        });

        // 가족 상세 페이지 이동 (원한다면 구현)
        familyBox1Container.setOnClickListener(v -> {
            // Example:
            // Intent intent = new Intent(MyPageActivity.this, FamilyDetailActivity.class);
            // startActivity(intent);
        });

        familyBox2Container.setOnClickListener(v -> {
            // Example:
            // Intent intent = new Intent(MyPageActivity.this, FamilyDetailActivity.class);
            // startActivity(intent);
        });
    }

    // 실제 데이터 로딩
    private void loadUserData() {

        // TODO: 서버/DB에서 실제 데이터 가져오기
        // ---- 여기서 가족 수만 바꿔주면 동작 테스트 가능 ----
        int familyCount = 0;    // ← ★ 테스트: 0, 1, 2 넣어보기

        // 유저 메인 정보
        tvNickname.setText("수희");
        tvDiaryCount.setText("작성한 일기 수 : 12");
        tvFamilyCount.setText("추가한 가족 수 : " + familyCount);

        // ---- 가족 수 조건으로 UI 조작 ----
        if (familyCount == 0) {

            familyBox1Container.setVisibility(View.GONE);
            familyBox2Container.setVisibility(View.GONE);

            layoutAddFamily.setVisibility(View.VISIBLE);

        } else if (familyCount == 1) {

            familyBox1Container.setVisibility(View.VISIBLE);
            familyBox2Container.setVisibility(View.GONE);
            layoutAddFamily.setVisibility(View.GONE);

            tvNicknameFamily1.setText("엄마");
            tvDiaryCountFamily1.setText("작성한 일기 수 : 5");

        } else {

            familyBox1Container.setVisibility(View.VISIBLE);
            familyBox2Container.setVisibility(View.VISIBLE);
            layoutAddFamily.setVisibility(View.GONE);

            tvNicknameFamily1.setText("엄마");
            tvDiaryCountFamily1.setText("작성한 일기 수 : 5");

            tvNicknameFamily2.setText("아빠");
            tvDiaryCountFamily2.setText("작성한 일기 수 : 4");
        }
    }
}
