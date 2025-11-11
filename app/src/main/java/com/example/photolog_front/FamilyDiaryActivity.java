package com.example.photolog_front;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.photolog_front.Diary;


public class FamilyDiaryActivity extends AppCompatActivity {

    private TableLayout familyTable;
    private TextView tvPageNumber;
    private int currentPage = 1;
    // --- 1. 페이지당 항목 수를 6으로 변경 ---
    private final int ITEMS_PER_PAGE = 6;
    private List<Diary> diaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_diary);

        familyTable = findViewById(R.id.recycler_family_diary);
        tvPageNumber = findViewById(R.id.tv_page_number);

        findViewById(R.id.layout_logo).setOnClickListener(v -> finish());

        diaryList = createDummyDiaries();
        Collections.reverse(diaryList);
        displayPage(currentPage);

        findViewById(R.id.btn_prev).setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                displayPage(currentPage);
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) diaryList.size() / ITEMS_PER_PAGE);
            if (currentPage < totalPages) {
                currentPage++;
                displayPage(currentPage);
            }
        });
    }

    private List<Diary> createDummyDiaries() {
        // 더미 데이터 생성
        List<Diary> list = new ArrayList<>();
        list.add(new Diary("김OO", "오늘의 일기_1", "오늘의 일기를 썼습니다. 내용이 여기에 들어갑니다.", R.drawable.food_sample, "2025년 11월 04일"));
        list.add(new Diary("최OO", "오늘의 일기_2", "오늘의 일기를 썼습니다. 내용이 여기에 들어갑니다.", R.drawable.cat_sample, "2025년 11월 05일"));
        list.add(new Diary("이OO", "오늘의 일기_3", "오늘의 일기를 썼습니다. 내용이 여기에 들어갑니다.", R.drawable.jeju_sample, "2025년 11월 06일"));
        list.add(new Diary("한OO", "오늘의 일기_4", "오늘의 일기를 썼습니다. 내용이 여기에 들어갑니다.", R.drawable.cafe_sample, "2025년 11월 07일"));
        list.add(new Diary("박OO", "오늘의 일기_5", "오늘의 일기를 썼습니다. 내용이 여기에 들어갑니다.", R.drawable.sample, "2025년 11월 08일"));
        list.add(new Diary("정OO", "오늘의 일기_6", "오늘의 일기를 썼습니다. 내용이 여기에 들어갑니다.", R.drawable.cat_sample, "2025년 11월 09일"));
        return list;
    }

    // --- 2. displayPage 메소드 수정 ---
    private void displayPage(int page) {
        // 헤더(첫 번째 자식)를 제외하고 모든 뷰를 지웁니다.
        if (familyTable.getChildCount() > 1) {
            familyTable.removeViews(1, familyTable.getChildCount() - 1);
        }
        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, diaryList.size());

        for (int i = start; i < end; i++) {
            Diary diary = diaryList.get(i);
            TableRow row = createTableRow(diary);

            // --- 수정된 로직 ---
            // "현재 페이지의 마지막 항목"인지 확인합니다.
            if (i == end - 1) {
                // 현재 페이지의 마지막 행일 경우: 배경을 null로 설정 (테두리 없음)
                // 이렇게 해야 TableLayout의 둥근 하단 테두리가 보입니다.
                row.setBackground(null);
            } else {
                // 중간 행일 경우: 아래쪽 테두리가 있는 배경을 설정합니다.
                row.setBackgroundResource(R.drawable.row_border_bottom);
            }

            familyTable.addView(row);
        }

        tvPageNumber.setText("-" + page + "-");
    }

    // --- createTableRow 메소드는 수정할 필요 없이 원본 유지 ---
    private TableRow createTableRow(Diary diary) {
        TableRow row = new TableRow(this);

        // createTableRow에서는 배경을 설정하지 않습니다. (displayPage에서 함)

        row.setGravity(Gravity.CENTER_VERTICAL);

        // 작성자 셀
        TextView author = new TextView(this);
        author.setText(diary.author);
        author.setTextColor(Color.parseColor("#5D3316"));
        author.setGravity(Gravity.CENTER);
        author.setTextSize(16);
        author.setTypeface(author.getTypeface(), android.graphics.Typeface.BOLD);
        author.setBackgroundResource(R.drawable.cell_border_right); // 세로선은 유지
        author.setPadding(8, 8, 8, 8);
        TableRow.LayoutParams authorParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        author.setLayoutParams(authorParams);
        row.addView(author);

        // 오른쪽 일기 셀
        LinearLayout diaryLayout = new LinearLayout(this);
        diaryLayout.setOrientation(LinearLayout.HORIZONTAL);
        diaryLayout.setPadding(12, 12, 12, 12);
        diaryLayout.setBackgroundColor(Color.TRANSPARENT);
        TableRow.LayoutParams diaryParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f);
        diaryLayout.setLayoutParams(diaryParams);

        // 왼쪽 (사진 + 날짜)
        LinearLayout leftLayout = new LinearLayout(this);
        leftLayout.setOrientation(LinearLayout.VERTICAL);
        leftLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        leftLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        ImageView img = new ImageView(this);
        img.setImageResource(diary.imageRes);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int size = (int) (getResources().getDisplayMetrics().density * 90); // 90dp
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(size, size);
        img.setLayoutParams(imgParams);
        img.setAdjustViewBounds(true);
        leftLayout.addView(img);

        TextView date = new TextView(this);
        date.setText(diary.date);
        date.setTextColor(Color.parseColor("#7B5A42"));
        date.setTextSize(12);
        date.setGravity(Gravity.CENTER);
        date.setPadding(0, 4, 0, 0);
        leftLayout.addView(date);

        // 오른쪽 (제목 + 내용)
        LinearLayout rightLayout = new LinearLayout(this);
        rightLayout.setOrientation(LinearLayout.VERTICAL);
        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));

        TextView title = new TextView(this);
        title.setText(diary.title);
        title.setTextColor(Color.parseColor("#5D3316"));
        title.setTextSize(18);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
        title.setPadding(8, 0, 0, 4);
        rightLayout.addView(title);

        TextView content = new TextView(this);
        content.setText(diary.content);
        content.setTextColor(Color.parseColor("#5D3316"));
        content.setTextSize(14);
        content.setMaxLines(2);
        content.setEllipsize(TextUtils.TruncateAt.END);
        content.setPadding(8, 0, 0, 0);
        rightLayout.addView(content);

        diaryLayout.addView(leftLayout);
        diaryLayout.addView(rightLayout);
        row.addView(diaryLayout);

        return row;
    }


    public static class Diary {
        String author;
        String title;
        String content;
        int imageRes;
        String date;

        public Diary(String author, String title, String content, int imageRes, String date) {
            this.author = author;
            this.title = title;
            this.content = content;
            this.imageRes = imageRes;
            this.date = date;
        }
    }
}