package com.example.photolog_front;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

public class FamilyDiaryDetailActivity extends AppCompatActivity {

    private ImageView imgDiary;
    private TextView tvTitle, tvAuthor, tvDate, tvContent;
    private LinearLayout commentContainer;
    private EditText etComment;
    private AppCompatButton btnSend;

    private List<String> commentList = new ArrayList<>();

    // 고정 색상 (통일)
    private static final int COLOR_TEXT_MAIN = 0xFF5D3316;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_diary_detail);

        // 바인딩
        imgDiary = findViewById(R.id.img_diary);
        tvTitle = findViewById(R.id.tv_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvDate = findViewById(R.id.tv_date);
        tvContent = findViewById(R.id.tv_content);
        commentContainer = findViewById(R.id.comment_container);
        etComment = findViewById(R.id.et_comment);
        btnSend = findViewById(R.id.btn_send);

        // 색상 통일 적용
        tvTitle.setTextColor(COLOR_TEXT_MAIN);
        tvAuthor.setTextColor(COLOR_TEXT_MAIN);
        tvDate.setTextColor(COLOR_TEXT_MAIN);
        tvContent.setTextColor(COLOR_TEXT_MAIN);
        etComment.setTextColor(COLOR_TEXT_MAIN);

        // 데이터 수신
        int imageResId = getIntent().getIntExtra("imageResId", 0);
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String content = getIntent().getStringExtra("content");
        String date = getIntent().getStringExtra("date");

        imgDiary.setImageResource(imageResId);
        tvTitle.setText(title);
        tvAuthor.setText(author);
        tvDate.setText(date);
        tvContent.setText(content);

        // 댓글 추가 버튼
        btnSend.setOnClickListener(v -> {
            String comment = etComment.getText().toString().trim();
            if (!comment.isEmpty()) {
                addComment(comment);
                etComment.setText("");
            } else {
                Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 로고 클릭 → 뒤로 가기
        findViewById(R.id.layout_logo).setOnClickListener(v -> finish());
    }

    // 댓글 추가 (색상 통일)
    private void addComment(String comment) {
        commentList.add(comment);

        TextView tvComment = new TextView(this);
        tvComment.setText(" - " + comment);
        tvComment.setTextColor(COLOR_TEXT_MAIN);
        tvComment.setTextSize(14);
        tvComment.setPadding(8, 4, 8, 4);

        commentContainer.addView(tvComment);
    }
}
