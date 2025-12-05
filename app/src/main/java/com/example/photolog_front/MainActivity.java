package com.example.photolog_front;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.example.photolog_front.model.MyDiaryItem;
import com.example.photolog_front.model.MyDiaryListResponse;
import com.example.photolog_front.network.ApiService;
import com.example.photolog_front.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView imgDiary;
    private TextView tvDiaryTitle, tvDiaryDate, tvDiaryContent;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = RetrofitClient.getApiService(this);

        // UI 요소 연결
        imgDiary = findViewById(R.id.img_diary);
        tvDiaryTitle = findViewById(R.id.tv_diary_title);
        tvDiaryDate = findViewById(R.id.tv_diary_date);
        tvDiaryContent = findViewById(R.id.tv_diary_content);

        // 상단 "내 일기 카드" 클릭 → MyDiaryActivity 이동
        ConstraintLayout myDiaryLayout = findViewById(R.id.my_diary);
        myDiaryLayout.setOnClickListener(v -> {
            Intent myDiaryIntent = new Intent(MainActivity.this, MyDiaryActivity.class);
            startActivity(myDiaryIntent);
        });

        // 새 일기 작성하기 버튼
        FrameLayout addDiaryButton = findViewById(R.id.layout_add_diary);
        addDiaryButton.setOnClickListener(v -> {
            Intent newDiaryIntent = new Intent(MainActivity.this, DiaryGenerationActivity.class);
            startActivity(newDiaryIntent);
        });

        // 우리 가족 일기 목록 열기
        LinearLayout layoutFamilyHeader = findViewById(R.id.layout_family_header);
        layoutFamilyHeader.setOnClickListener(v -> {
            Intent familyIntent = new Intent(MainActivity.this, FamilyDiaryActivity.class);
            startActivity(familyIntent);
        });

        ImageView myPage = findViewById(R.id.my_page);
        myPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
            startActivity(intent);
        });

        ImageView imgPlusIcon = findViewById(R.id.img_plus_icon);
        imgPlusIcon.setOnClickListener(v -> {
            Intent familyIntent = new Intent(MainActivity.this, FamilyDiaryActivity.class);
            startActivity(familyIntent);
        });

        // 상단 카드뷰 최신 일기 로드
        loadLatestMyDiary();

        // 하단 가족 일기 미리보기
        populateFamilyDiaryPreview();

        // card_random_diary는 그대로 가족 일기 상세 페이지 이동 유지
        findViewById(R.id.card_random_diary).setOnClickListener(v -> openLatestDiaryDetailFromRepository());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 다시 홈으로 돌아왔을 때도 최신 내 일기 다시 불러오기
        loadLatestMyDiary();

        // 가족 일기도 최신화
        populateFamilyDiaryPreview();
    }

    /**
     * 서버에서 "내가 작성한 일기 최신 1개"를 가져와서 메인 상단에 표시
     */
    private void loadLatestMyDiary() {

        api.getMyDiaries().enqueue(new Callback<MyDiaryListResponse>() {
            @Override
            public void onResponse(Call<MyDiaryListResponse> call, Response<MyDiaryListResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<MyDiaryItem> diaries = response.body().diaries;

                if (diaries == null || diaries.isEmpty()) {
                    tvDiaryTitle.setText("아직 작성된 일기가 없어요!");
                    tvDiaryDate.setText("");
                    tvDiaryContent.setText("새로 일기를 작성하러 가볼까요?");
                    imgDiary.setImageResource(R.drawable.sample);
                    return;
                }

                MyDiaryItem latest = diaries.get(0);

                tvDiaryTitle.setText(latest.title);
                tvDiaryDate.setText(latest.date);
                tvDiaryContent.setText(latest.content);

                if (latest.photo != null && latest.photo.file_path != null) {
                    Glide.with(MainActivity.this)
                            .load(RetrofitClient.BASE_URL + latest.photo.file_path)
                            .into(imgDiary);
                } else {
                    imgDiary.setImageResource(R.drawable.sample);
                }
            }

            @Override
            public void onFailure(Call<MyDiaryListResponse> call, Throwable t) { }
        });
    }

    /**
     * 이 함수는 기존 Repository 기반이므로 유지 (가족 일기 상세보기)
     */
    private void openLatestDiaryDetailFromRepository() {
        List<Diary> list = DiaryRepository.getInstance().getAll();

        if (list.isEmpty()) {
            Toast.makeText(this, "아직 작성된 일기가 없어요!", Toast.LENGTH_SHORT).show();
            return;
        }

        Diary latest = list.get(0);

        Intent intent = new Intent(MainActivity.this, FamilyDiaryDetailActivity.class);
        intent.putExtra("diary", latest);
        startActivity(intent);
    }

    /**
     *  하단 "우리 가족 일기" 미리보기 기존 유지
     */
    private void populateFamilyDiaryPreview() {

        ConstraintLayout layout = findViewById(R.id.family_Diary_Layout);
        TextView emptyView = findViewById(R.id.tv_family_empty);

        // row 영역 제거 (기존 유지)
        if (layout.getChildCount() > 11) {
            layout.removeViews(11, layout.getChildCount() - 11);
        }

        List<Diary> fullList = DiaryRepository.getInstance().getAll();

        if (fullList.isEmpty()) {
            findViewById(R.id.header_author).setVisibility(View.GONE);
            findViewById(R.id.header_title).setVisibility(View.GONE);
            findViewById(R.id.line_horizontal).setVisibility(View.GONE);
            findViewById(R.id.line_vertical).setVisibility(View.GONE);

            findViewById(R.id.row_anchor_1).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_2).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_3).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_4).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_5).setVisibility(View.GONE);

            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);

        findViewById(R.id.header_author).setVisibility(View.VISIBLE);
        findViewById(R.id.header_title).setVisibility(View.VISIBLE);
        findViewById(R.id.line_horizontal).setVisibility(View.VISIBLE);
        findViewById(R.id.line_vertical).setVisibility(View.VISIBLE);

        findViewById(R.id.row_anchor_1).setVisibility(View.VISIBLE);
        findViewById(R.id.row_anchor_2).setVisibility(View.VISIBLE);
        findViewById(R.id.row_anchor_3).setVisibility(View.VISIBLE);
        findViewById(R.id.row_anchor_4).setVisibility(View.VISIBLE);
        findViewById(R.id.row_anchor_5).setVisibility(View.VISIBLE);

        // 최신 5개만 구성
        List<Diary> previewList = new ArrayList<>();
        int limit = Math.min(fullList.size(), 5);
        for (int i = 0; i < limit; i++) previewList.add(fullList.get(i));

        int[] anchorIds = {
                R.id.row_anchor_1, R.id.row_anchor_2,
                R.id.row_anchor_3, R.id.row_anchor_4, R.id.row_anchor_5
        };

        ConstraintSet cs = new ConstraintSet();
        cs.clone(layout);

        for (int i = 0; i < previewList.size(); i++) {

            Diary diary = previewList.get(i);

            TextView authorView = new TextView(this);
            authorView.setId(View.generateViewId());
            authorView.setText(diary.getAuthor());
            authorView.setTextColor(Color.parseColor("#665F5A"));
            authorView.setGravity(Gravity.CENTER);
            layout.addView(authorView);

            TextView titleView = new TextView(this);
            titleView.setId(View.generateViewId());
            titleView.setText(diary.getTitle());
            titleView.setTextColor(Color.parseColor("#665F5A"));
            titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            titleView.setPadding(24, 0, 0, 0);
            layout.addView(titleView);

            int anchor = anchorIds[i];

            cs.connect(authorView.getId(), ConstraintSet.TOP, anchor, ConstraintSet.TOP);
            cs.connect(authorView.getId(), ConstraintSet.BOTTOM, anchor, ConstraintSet.BOTTOM);
            cs.connect(authorView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            cs.connect(authorView.getId(), ConstraintSet.END, R.id.guideline_vertical, ConstraintSet.START);
            cs.constrainWidth(authorView.getId(), ConstraintSet.MATCH_CONSTRAINT);
            cs.constrainHeight(authorView.getId(), ConstraintSet.WRAP_CONTENT);

            cs.connect(titleView.getId(), ConstraintSet.TOP, anchor, ConstraintSet.TOP);
            cs.connect(titleView.getId(), ConstraintSet.BOTTOM, anchor, ConstraintSet.BOTTOM);
            cs.connect(titleView.getId(), ConstraintSet.START, R.id.guideline_vertical, ConstraintSet.END);
            cs.connect(titleView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            cs.constrainWidth(titleView.getId(), ConstraintSet.MATCH_CONSTRAINT);
            cs.constrainHeight(titleView.getId(), ConstraintSet.WRAP_CONTENT);
        }

        cs.applyTo(layout);
    }

}
