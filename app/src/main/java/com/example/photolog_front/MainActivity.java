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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imgDiary;
    private TextView tvDiaryTitle, tvDiaryDate, tvDiaryContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 연결
        imgDiary = findViewById(R.id.img_diary);
        tvDiaryTitle = findViewById(R.id.tv_diary_title);
        tvDiaryDate = findViewById(R.id.tv_diary_date);
        tvDiaryContent = findViewById(R.id.tv_diary_content);

        // 메인 화면 하단의 "우리 가족 일기 미리보기 테이블" 표시
        // Repository 최신 데이터가 반영되도록 수정함
        populateFamilyDiaryPreview();

        // + 새 일기 작성하기 버튼
        FrameLayout addDiaryButton = findViewById(R.id.layout_add_diary);
        addDiaryButton.setOnClickListener(v -> {
            Intent newDiaryIntent = new Intent(MainActivity.this, DiaryGenerationActivity.class);
            startActivity(newDiaryIntent);
        });

        // "우리 가족 일기" 헤더 클릭 → 전체 목록 화면으로 이동
        LinearLayout layoutFamilyHeader = findViewById(R.id.layout_family_header);
        layoutFamilyHeader.setOnClickListener(v -> {
            Intent familyIntent = new Intent(MainActivity.this, FamilyDiaryActivity.class);
            startActivity(familyIntent);
        });

        ImageView imgPlusIcon = findViewById(R.id.img_plus_icon);
        imgPlusIcon.setOnClickListener(v -> {
            Intent familyIntent = new Intent(MainActivity.this, FamilyDiaryActivity.class);
            startActivity(familyIntent);
        });

        // 메인 상단 카드뷰 클릭 → 최신 일기 상세보기 이동
        findViewById(R.id.card_random_diary).setOnClickListener(v -> openLatestDiaryDetail());
    }

    // 메인 화면으로 돌아올 때마다 최신 일기 카드뷰 갱신
    @Override
    protected void onResume() {
        super.onResume();

        List<Diary> list = DiaryRepository.getInstance().getAll();

        // 일기가 하나라도 있다면 상단 카드뷰 업데이트
        if (!list.isEmpty()) {
            Diary latest = list.get(0);

            tvDiaryTitle.setText(latest.getTitle());
            tvDiaryDate.setText(latest.getDate());
            tvDiaryContent.setText(latest.getContent());

            // URI 이미지 우선
            if (latest.getImageUri() != null) {
                imgDiary.setImageURI(Uri.parse(latest.getImageUri()));
            }
            // drawable 이미지(더미) 사용 가능
            else if (latest.getImageRes() != 0) {
                imgDiary.setImageResource(latest.getImageRes());
            }
        }

        // 아래쪽 테이블도 최신화
        populateFamilyDiaryPreview();
    }

    // 메인 카드 클릭 → 최신 일기 상세보기
    private void openLatestDiaryDetail() {
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

    // 메인 화면의 "우리 가족 일기" 미리보기 5개 표시
    private void populateFamilyDiaryPreview() {

        ConstraintLayout layout = findViewById(R.id.family_Diary_Layout);
        TextView emptyView = findViewById(R.id.tv_family_empty);

        // 기존 row들 제거 (고정 11개 제외)
        if (layout.getChildCount() > 11) {
            layout.removeViews(11, layout.getChildCount() - 11);
        }

        // Repository에서 실제 데이터 가져오기 (최신순 정렬되어 있음)
        List<Diary> fullList = DiaryRepository.getInstance().getAll();

        // 일기 없음 → 안내문 띄우기
        if (fullList.isEmpty()) {

            // 내부 항목들만 숨김 (박스는 유지)
            findViewById(R.id.header_author).setVisibility(View.GONE);
            findViewById(R.id.header_title).setVisibility(View.GONE);
            findViewById(R.id.line_horizontal).setVisibility(View.GONE);
            findViewById(R.id.line_vertical).setVisibility(View.GONE);

            // row anchors 1~5 숨기기
            findViewById(R.id.row_anchor_1).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_2).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_3).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_4).setVisibility(View.GONE);
            findViewById(R.id.row_anchor_5).setVisibility(View.GONE);

            // 안내문 표시
            emptyView.setVisibility(View.VISIBLE);

            return;
        }

        // 일기 있음 → 안내문 숨기고 헤더/줄선/anchors 보이기
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



        // 최신순 5개만 미리보기
        List<Diary> previewList = new ArrayList<>();
        int limit = Math.min(fullList.size(), 5);

        for (int i = 0; i < limit; i++) {
            previewList.add(fullList.get(i));
        }

        // 각 row(5줄)의 anchor ID
        int[] anchorIds = {
                R.id.row_anchor_1, R.id.row_anchor_2,
                R.id.row_anchor_3, R.id.row_anchor_4, R.id.row_anchor_5
        };

        List<TextView> authorViews = new ArrayList<>();
        List<TextView> titleViews = new ArrayList<>();

        ConstraintSet cs = new ConstraintSet();
        cs.clone(layout);

        // 동적 텍스트뷰 생성
        for (int i = 0; i < previewList.size(); i++) {

            Diary diary = previewList.get(i);

            // 작성자
            TextView authorView = new TextView(this);
            authorView.setId(View.generateViewId());
            authorView.setText(diary.getAuthor());
            authorView.setTextColor(Color.parseColor("#665F5A"));
            authorView.setGravity(Gravity.CENTER);
            layout.addView(authorView);
            authorViews.add(authorView);

            // 제목
            TextView titleView = new TextView(this);
            titleView.setId(View.generateViewId());
            titleView.setText(diary.getTitle());
            titleView.setTextColor(Color.parseColor("#665F5A"));
            titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            titleView.setPadding(24, 0, 0, 0);
            layout.addView(titleView);
            titleViews.add(titleView);

            int anchor = anchorIds[i];

            // 작성자 배치
            cs.connect(authorView.getId(), ConstraintSet.TOP, anchor, ConstraintSet.TOP);
            cs.connect(authorView.getId(), ConstraintSet.BOTTOM, anchor, ConstraintSet.BOTTOM);
            cs.connect(authorView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            cs.connect(authorView.getId(), ConstraintSet.END, R.id.guideline_vertical, ConstraintSet.START);
            cs.constrainWidth(authorView.getId(), ConstraintSet.MATCH_CONSTRAINT);
            cs.constrainHeight(authorView.getId(), ConstraintSet.WRAP_CONTENT);

            // 제목 배치
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
