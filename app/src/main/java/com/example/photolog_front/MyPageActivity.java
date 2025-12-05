package com.example.photolog_front;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photolog_front.model.UserResponse;
import com.example.photolog_front.model.FamilyMemberResponse;
import com.example.photolog_front.network.ApiService;
import com.example.photolog_front.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // 가족 상세 페이지 이동 준비 (선택 구현 가능)
        familyBox1Container.setOnClickListener(v -> {
            // TODO: 가족 상세 페이지 이동
        });

        familyBox2Container.setOnClickListener(v -> {
            // TODO: 가족 상세 페이지 이동
        });
    }

    // 사용자 정보 로딩
    private void loadUserData() {

        ApiService api = RetrofitClient.getApiService(this);

        api.getUserInfo().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                UserResponse user = response.body();

                // ① 사용자 정보 UI 반영
                tvNickname.setText(user.name);
                tvDiaryCount.setText("작성한 일기 수 : " + user.diaries_count);
                tvFamilyCount.setText("추가한 가족 수 : " + user.families.size());

                // ② 가족 수 UI 조절
                int familyCount = user.families.size();

                if (familyCount == 0) {

                    // 가족 없음 → 박스 숨기고 "가족 추가" 버튼 표시
                    layoutAddFamily.setVisibility(View.VISIBLE);
                    familyBox1Container.setVisibility(View.GONE);
                    familyBox2Container.setVisibility(View.GONE);
                    return;
                }

                // 가족 1명 이상
                if (familyCount >= 1) {
                    UserResponse.FamilyInfo f1 = user.families.get(0);
                    loadFamilyMembers(f1.id, 1);
                }

                // 가족 2명 이상
                if (familyCount >= 2) {
                    UserResponse.FamilyInfo f2 = user.families.get(1);
                    loadFamilyMembers(f2.id, 2);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) { }
        });
    }

    // 가족 구성원 로딩
    private void loadFamilyMembers(int familyId, int boxIndex) {

        ApiService api = RetrofitClient.getApiService(this);

        api.getFamilyMembers(familyId).enqueue(new Callback<List<FamilyMemberResponse>>() {
            @Override
            public void onResponse(Call<List<FamilyMemberResponse>> call, Response<List<FamilyMemberResponse>> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<FamilyMemberResponse> members = response.body();

                if (members.size() == 0) return;

                // 관리자 또는 첫 번째 멤버 사용
                FamilyMemberResponse target = members.get(0);

                if (boxIndex == 1) {
                    familyBox1Container.setVisibility(View.VISIBLE);
                    tvNicknameFamily1.setText(target.name);
                    tvDiaryCountFamily1.setText("작성한 일기 수 : " + target.diaries_count);
                } else {
                    familyBox2Container.setVisibility(View.VISIBLE);
                    tvNicknameFamily2.setText(target.name);
                    tvDiaryCountFamily2.setText("작성한 일기 수 : " + target.diaries_count);
                }

                // 가족 추가 버튼 숨김
                layoutAddFamily.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<FamilyMemberResponse>> call, Throwable t) { }
        });
    }
}
