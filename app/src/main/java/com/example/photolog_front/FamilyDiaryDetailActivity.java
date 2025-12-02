package com.example.photolog_front;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FamilyDiaryDetailActivity extends AppCompatActivity {

    private ImageView imgDiary;
    private TextView tvTitle, tvInfo, tvContent;
    private LinearLayout commentContainer;
    private TextView tvNoComment;
    private EditText etComment;
    private View btnSend;

    private final List<Comment> commentList = new ArrayList<>();
    private final String currentUser = "test";

    private Comment replyingTo = null;
    private static final String DEFAULT_HINT = "댓글을 입력하세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_diary_detail);

        imgDiary = findViewById(R.id.img_diary);
        tvTitle = findViewById(R.id.tv_title);
        tvInfo = findViewById(R.id.tv_info);
        tvContent = findViewById(R.id.tv_content);
        commentContainer = findViewById(R.id.comment_container);
        tvNoComment = findViewById(R.id.tv_no_comment);
        etComment = findViewById(R.id.et_comment);
        btnSend = findViewById(R.id.btn_send);

        Diary diary = (Diary) getIntent().getSerializableExtra("diary");

        tvTitle.setText(diary.getTitle());
        tvInfo.setText(diary.getAuthor() + " | " + diary.getDate());
        tvContent.setText(diary.getContent());

        if (diary.getImageUri() != null)
            imgDiary.setImageURI(Uri.parse(diary.getImageUri()));
        else if (diary.getImageRes() != 0)
            imgDiary.setImageResource(diary.getImageRes());

        findViewById(R.id.layout_logo).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        findViewById(R.id.btn_back_to_list).setOnClickListener(v -> {
            Intent intent = new Intent(this, FamilyDiaryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // 댓글 작성 버튼 → 팝업으로 입력
        btnSend.setOnClickListener(v -> submitComment());

        renderComments();

        // 하단 입력창 클릭 → 팝업 열기
        etComment.setOnClickListener(v -> {
            showCommentDialog("댓글 달기", etComment.getText().toString());
        });
    }

    // 댓글 입력 다이얼로그 (팝업)
    private void showCommentDialog(String title, String defaultText) {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_custom, null);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etInput = dialogView.findViewById(R.id.et_dialog_input);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btn_save);

        tvDialogTitle.setText(title);
        etInput.setText(defaultText);

        etInput.setSingleLine(false);
        etInput.setMaxLines(Integer.MAX_VALUE);
        etInput.setMovementMethod(new ScrollingMovementMethod());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if (!text.isEmpty()) {
                etComment.setText(text);
                etComment.setSelection(text.length()); // 커서 맨 뒤
            }
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    // 최종적으로 댓글을 실제 추가하는 부분
    private void submitComment() {
        String text = etComment.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        if (replyingTo == null) {
            commentList.add(new Comment(currentUser, text, time));
        } else {
            String replyMessage = "@" + replyingTo.user + " " + text;
            Comment reply = new Comment(currentUser, replyMessage, time);
            replyingTo.replies.add(reply);
            replyingTo = null;
        }

        etComment.setText("");
        etComment.setHint(DEFAULT_HINT);

        renderComments();
    }

    // 댓글 렌더링
    private void renderComments() {

        if (commentList.isEmpty()) {
            tvNoComment.setVisibility(View.VISIBLE);
            commentContainer.removeAllViews();
            return;
        }

        tvNoComment.setVisibility(View.GONE);
        commentContainer.removeAllViews();

        for (Comment c : commentList) {
            addCommentView(c, 0);
        }
    }

    // 개별 댓글 뷰 추가
    private void addCommentView(Comment comment, int depth) {

        int indent = depth == 0 ? 0 : dpToPx(20);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(indent, dpToPx(8), dpToPx(10), dpToPx(8));

        TextView tvUser = new TextView(this);
        tvUser.setText(comment.user + " • " + comment.time);
        tvUser.setTextColor(Color.parseColor("#5D3316"));
        tvUser.setTypeface(Typeface.DEFAULT_BOLD);
        tvUser.setTextSize(13);

        TextView tvText = new TextView(this);
        tvText.setText("- " + comment.text);
        tvText.setTextColor(Color.parseColor("#5D3316"));
        tvText.setTextSize(15);

        TextView tvReply = new TextView(this);
        tvReply.setText("답글 달기");
        tvReply.setTextColor(Color.parseColor("#8C6B56"));
        tvReply.setTextSize(12);

        // 답글 클릭 → 팝업 입력
        tvReply.setOnClickListener(v -> {
            replyingTo = comment;
            showCommentDialog("답글 달기 (@" + comment.user + ")", "");
        });

        layout.addView(tvUser);
        layout.addView(tvText);
        layout.addView(tvReply);

        commentContainer.addView(layout);

        // 대댓글 재귀적으로 추가
        for (Comment r : comment.replies) {
            addCommentView(r, 1);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // 댓글 모델
    public static class Comment {
        String user;
        String text;
        String time;
        List<Comment> replies = new ArrayList<>();

        public Comment(String u, String t, String time) {
            this.user = u;
            this.text = t;
            this.time = time;
        }
    }
}
