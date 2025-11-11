package com.example.photolog_front;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout; // LinearLayout import 추가
import android.widget.TextView;
import android.widget.Toast;
import com.example.photolog_front.Diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ImageView imgDiary;
    private TextView tvDiaryTitle, tvDiaryDate, tvDiaryContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. UI 변수 초기화
        imgDiary = findViewById(R.id.img_diary);
        tvDiaryTitle = findViewById(R.id.tv_diary_title);
        tvDiaryDate = findViewById(R.id.tv_diary_date);
        tvDiaryContent = findViewById(R.id.tv_diary_content);

        // 2. 가족 일기 목록 채우기
        populateFamilyDiary();

        // 3. '+' 버튼 클릭 → 새 일기 작성으로 이동
        FrameLayout addDiaryButton = findViewById(R.id.layout_add_diary);
        addDiaryButton.setOnClickListener(v -> {
            Intent newDiaryIntent = new Intent(MainActivity.this, DiaryGenerationActivity.class);
            startActivity(newDiaryIntent);
        });

        // 4. “우리 가족 일기” 제목 영역 클릭
        LinearLayout layoutFamilyHeader = findViewById(R.id.layout_family_header);
        layoutFamilyHeader.setOnClickListener(v -> {
            Intent familyIntent = new Intent(MainActivity.this, FamilyDiaryActivity.class);
            startActivity(familyIntent);
        });

        // 5. “+” 아이콘 클릭
        ImageView imgPlusIcon = findViewById(R.id.img_plus_icon);
        imgPlusIcon.setOnClickListener(v -> {
            Intent familyIntent = new Intent(MainActivity.this, FamilyDiaryActivity.class);
            startActivity(familyIntent);
        });

        // 6. onCreate에서 전달된 Intent 처리
        handleIntent(getIntent());
    }

    // 7. MainActivity가 이미 실행 중일 때 Intent를 받으면 이 메소드가 호출됨
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    // 8. Intent를 처리하는 로직을 하나의 메소드로 통합
    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("photo_uri")) {
            String photoUriString = intent.getStringExtra("photo_uri");
            String title = intent.getStringExtra("diary_title");
            String content = intent.getStringExtra("diary_content");
            String currentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(new Date());

            if (photoUriString != null) {
                Uri photoUri = Uri.parse(photoUriString);
                updateDiaryCard(photoUri);
            }
            tvDiaryTitle.setText(title != null ? title : "무제 일기");
            tvDiaryDate.setText(currentDate);
            tvDiaryContent.setText(content != null ? content : "내용이 없습니다.");
        }
    }

    private void updateDiaryCard(Uri photoUri) {
        try {
            imgDiary.setImageURI(photoUri);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "사진을 불러올 수 없습니다. 권한 문제입니다.", Toast.LENGTH_SHORT).show();
            imgDiary.setImageResource(R.drawable.sample); // fallback
        }
    }


    // 9. 가족 일기 목록 (표 형태로 표시)
    private void populateFamilyDiary() {
        ConstraintLayout layout = findViewById(R.id.family_Diary_Layout);

        // 기존 뷰 제거
        if (layout.getChildCount() > 11) {
            layout.removeViews(11, layout.getChildCount() - 11);
        }

        // 더미 데이터 (작성자, 제목)
        List<Diary> diaryList = new ArrayList<>();
        diaryList.add(new Diary("김OO", "오늘의 일기_1"));
        diaryList.add(new Diary("최OO", "오늘의 일기_2"));
        diaryList.add(new Diary("이OO", "오늘의 일기_3"));
        diaryList.add(new Diary("한OO", "오늘의 일기_4"));
        diaryList.add(new Diary("박OO", "오늘의 일기_5"));

        int[] anchorIds = {
                R.id.row_anchor_1, R.id.row_anchor_2, R.id.row_anchor_3, R.id.row_anchor_4, R.id.row_anchor_5
        };

        List<TextView> authorViews = new ArrayList<>();
        List<TextView> titleViews = new ArrayList<>();

        // TextView 생성 및 추가
        for (Diary diary : diaryList) {
            TextView authorView = new TextView(this);
            authorView.setId(View.generateViewId());
            authorView.setText(diary.author);
            authorView.setTextColor(Color.parseColor("#665F5A"));
            authorView.setGravity(Gravity.CENTER);
            layout.addView(authorView);
            authorViews.add(authorView);

            TextView titleView = new TextView(this);
            titleView.setId(View.generateViewId());
            titleView.setText(diary.title);
            titleView.setTextColor(Color.parseColor("#665F5A"));
            titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            titleView.setPadding(24, 0, 0, 0);
            layout.addView(titleView);
            titleViews.add(titleView);
        }

        // ConstraintSet으로 위치 지정
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        for (int i = 0; i < diaryList.size(); i++) {
            View authorView = authorViews.get(i);
            View titleView = titleViews.get(i);
            int currentAnchorId = anchorIds[i];

            // 작성자 영역
            constraintSet.connect(authorView.getId(), ConstraintSet.TOP, currentAnchorId, ConstraintSet.TOP);
            constraintSet.connect(authorView.getId(), ConstraintSet.BOTTOM, currentAnchorId, ConstraintSet.BOTTOM);
            constraintSet.connect(authorView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(authorView.getId(), ConstraintSet.END, R.id.guideline_vertical, ConstraintSet.START);
            constraintSet.constrainWidth(authorView.getId(), ConstraintSet.MATCH_CONSTRAINT);
            constraintSet.constrainHeight(authorView.getId(), ConstraintSet.WRAP_CONTENT);

            // 제목 영역
            constraintSet.connect(titleView.getId(), ConstraintSet.TOP, currentAnchorId, ConstraintSet.TOP);
            constraintSet.connect(titleView.getId(), ConstraintSet.BOTTOM, currentAnchorId, ConstraintSet.BOTTOM);
            constraintSet.connect(titleView.getId(), ConstraintSet.START, R.id.guideline_vertical, ConstraintSet.END);
            constraintSet.connect(titleView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.constrainWidth(titleView.getId(), ConstraintSet.MATCH_CONSTRAINT);
            constraintSet.constrainHeight(titleView.getId(), ConstraintSet.WRAP_CONTENT);
        }

        constraintSet.applyTo(layout);
    }

    // 일기 데이터 클래스
    public static class Diary {
        String author;
        String title;

        public Diary(String author, String title) {
            this.author = author;
            this.title = title;
        }
    }
}