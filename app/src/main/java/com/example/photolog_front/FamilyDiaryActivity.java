package com.example.photolog_front;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class FamilyDiaryActivity extends AppCompatActivity {

    private TableLayout familyTable;
    private TextView tvPageNumber;
    private ImageView btnPrev, btnNext;

    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 6;

    private List<Diary> diaryList;

    //폰트
    private Typeface fontPaper6;
    private Typeface fontPaper5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_diary);

        familyTable = findViewById(R.id.recycler_family_diary);
        tvPageNumber = findViewById(R.id.tv_page_number);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);

        findViewById(R.id.layout_logo).setOnClickListener(v -> finish());

        // 🔤 폰트 로드 (res/font/paperlogy_6.ttf, paperlogy_5.ttf 기준)
        fontPaper6 = ResourcesCompat.getFont(this, R.font.paperlogy_6);
        fontPaper5 = ResourcesCompat.getFont(this, R.font.paperlogy_5);

        // 1) Repository 일기 + 더미 일기 합치기
        diaryList = new ArrayList<>();

        // 최신 작성된 실제 일기 먼저
        diaryList.addAll(DiaryRepository.getInstance().getAll());

        // 실제 일기 + 더미 모두 없으면 초기 안내 화면 표시
        if (diaryList.isEmpty()) {
            showEmptyView();
            return;
        }

        displayPage(currentPage);

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                displayPage(currentPage);
            }
        });

        btnNext.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) diaryList.size() / ITEMS_PER_PAGE);
            if (currentPage < totalPages) {
                currentPage++;
                displayPage(currentPage);
            }
        });
    }

    // 초기 안내 화면
    private void showEmptyView() {
        TextView empty = findViewById(R.id.tv_empty);

        empty.setVisibility(View.VISIBLE);     // 안내문 표시
        familyTable.setVisibility(View.GONE);  // 테이블 숨김
        tvPageNumber.setVisibility(View.GONE); // 페이지 번호 숨김
        btnPrev.setVisibility(View.GONE);      // 이전 버튼 숨김
        btnNext.setVisibility(View.GONE);      // 다음 버튼 숨김
    }

    // 페이지 출력
    private void displayPage(int page) {
        if (familyTable.getChildCount() > 1) {
            familyTable.removeViews(1, familyTable.getChildCount() - 1);
        }

        int totalPages = (int) Math.ceil((double) diaryList.size() / ITEMS_PER_PAGE);

        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, diaryList.size());

        for (int i = start; i < end; i++) {
            Diary diary = diaryList.get(i);
            TableRow row = createTableRow(diary);

            if (i < end - 1) {
                row.setBackgroundResource(R.drawable.row_border_bottom);
            }

            familyTable.addView(row);
        }

        tvPageNumber.setText(page + " / " + totalPages);

        btnPrev.setAlpha(page == 1 ? 0.3f : 1f);
        btnPrev.setEnabled(page != 1);

        btnNext.setAlpha(page == totalPages ? 0.3f : 1f);
        btnNext.setEnabled(page != totalPages);
    }

    // 표 한 행 만들기
    private TableRow createTableRow(Diary diary) {

        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER_VERTICAL);

        int rowMinHeight = (int) (getResources().getDisplayMetrics().density * 120);
        row.setMinimumHeight(rowMinHeight);

        // ───────── 작성자 ─────────
        TextView author = new TextView(this);
        author.setText(diary.getAuthor());
        author.setTextColor(Color.parseColor("#5D3316"));
        author.setTextSize(18); // 18sp
        author.setGravity(Gravity.CENTER);

        if (fontPaper6 != null) {
            author.setTypeface(fontPaper6, Typeface.BOLD);
        } else {
            author.setTypeface(author.getTypeface(), Typeface.BOLD);
        }

        author.setBackgroundResource(R.drawable.cell_border_right);
        author.setPadding(8, 8, 8, 8);

        TableRow.LayoutParams authorParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        author.setLayoutParams(authorParams);

        row.addView(author);

        // ───────── 오른쪽: 사진 + 텍스트 영역 ─────────
        LinearLayout diaryLayout = new LinearLayout(this);
        diaryLayout.setOrientation(LinearLayout.HORIZONTAL);
        diaryLayout.setPadding(12, 12, 12, 12);
        diaryLayout.setLayoutParams(new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 3f));

        // 이미지
        ImageView img = new ImageView(this);
        int size = (int) (getResources().getDisplayMetrics().density * 90);

        if (diary.getImageUri() != null) {
            img.setImageURI(Uri.parse(diary.getImageUri()));
        } else {
            img.setImageResource(diary.getImageRes());
        }

        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        diaryLayout.addView(img);

        // 제목 + 내용
        LinearLayout rightLayout = new LinearLayout(this);
        rightLayout.setOrientation(LinearLayout.VERTICAL);
        rightLayout.setPadding(12, 0, 0, 0);

        // ─ 제목 ─
        TextView title = new TextView(this);
        title.setText(diary.getTitle());
        title.setTextSize(18); // 18sp
        title.setTextColor(Color.parseColor("#5D3316"));
        if (fontPaper6 != null) {
            title.setTypeface(fontPaper6, Typeface.BOLD);
        } else {
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
        }

        // ─ 내용 ─
        TextView content = new TextView(this);
        content.setText(diary.getContent());
        content.setTextSize(16); // 16sp
        content.setTextColor(Color.parseColor("#5D3316"));
        content.setMaxLines(2);
        content.setEllipsize(TextUtils.TruncateAt.END);
        if (fontPaper5 != null) {
            content.setTypeface(fontPaper5);
        }

        rightLayout.addView(title);
        rightLayout.addView(content);

        diaryLayout.addView(rightLayout);
        row.addView(diaryLayout);

        // 클릭 시 상세 페이지 이동
        row.setOnClickListener(v -> {
            Intent intent = new Intent(FamilyDiaryActivity.this, FamilyDiaryDetailActivity.class);
            intent.putExtra("diary", diary);
            startActivity(intent);
        });

        return row;
    }
}
