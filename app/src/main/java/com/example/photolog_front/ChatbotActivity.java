package com.example.photolog_front;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private ImageButton btnMic;
    private AppCompatButton btnFinishChat;

    private static final int MIN_ANSWERS = 3;
    private int answerCount = 0;

    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            if (results != null && !results.isEmpty()) {
                                String recognizedText = results.get(0);
                                addUserAnswer(recognizedText);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        findViewById(R.id.layout_logo).setOnClickListener(v -> showExitConfirmDialog());

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        btnMic = findViewById(R.id.btn_mic);
        btnFinishChat = findViewById(R.id.btn_finish_chat);
        messageList = new ArrayList<>();

        btnFinishChat.setEnabled(false);
        btnFinishChat.setVisibility(View.INVISIBLE);
        btnFinishChat.setAlpha(0.4f);

        btnFinishChat.setOnClickListener(v -> goToDiaryResult());

        String imageUriString = getIntent().getStringExtra("selected_photo_uri");
        Uri imageUri = Uri.parse(imageUriString);

        messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_IMAGE, null, imageUri));
        messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_AI_QUESTION, "이 사진에 대해 이야기해볼까요?", null));
        messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_USER_ANSWER, "답변을 입력하려면 여기를 눌러주세요.", null));

        chatAdapter = new ChatAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        btnMic.setOnClickListener(v -> {
            if (checkAudioPermission())
                startSpeechRecognition();
        });
    }

    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exit_chatbot, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        AppCompatButton btnYes = dialogView.findViewById(R.id.btn_yes);
        AppCompatButton btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요...");
        speechLauncher.launch(intent);
    }

    private boolean checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            return false;
        }
        return true;
    }

    public void addUserAnswer(String text) {
        int lastUserIndex = -1;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(i).getViewType() == ChatMessage.VIEW_TYPE_USER_ANSWER) {
                lastUserIndex = i;
                break;
            }
        }

        if (lastUserIndex != -1) {
            messageList.set(lastUserIndex, new ChatMessage(ChatMessage.VIEW_TYPE_USER_ANSWER, text, null));
            chatAdapter.notifyItemChanged(lastUserIndex);
            chatRecyclerView.scrollToPosition(lastUserIndex);
        } else {
            messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_USER_ANSWER, text, null));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            chatRecyclerView.scrollToPosition(messageList.size() - 1);
        }

        answerCount++;

        if (answerCount >= MIN_ANSWERS) {
            btnFinishChat.setEnabled(true);
            btnFinishChat.setVisibility(View.VISIBLE);
            btnFinishChat.setAlpha(1f);
        }

        chatRecyclerView.postDelayed(() -> {
            String nextQuestion = generateNextQuestion();
            messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_AI_QUESTION, nextQuestion, null));
            messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_USER_ANSWER, "답변을 입력하려면 여기를 눌러주세요.", null));
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(messageList.size() - 1);
        }, 800);
    }

    private String generateNextQuestion() {
        String[] sampleQuestions = {
                "어디에서 사진을 찍었나요?",
                "누구와 함께 있었나요?",
                "기분은 어땠나요?",
                "이 사진에서 가장 기억에 남는 부분은 무엇인가요?"
        };
        int index = (int) (Math.random() * sampleQuestions.length);
        return sampleQuestions[index];
    }

    // 커스텀 입력창 사용
    public void showTextInputDialog(String currentText) {
        showCustomInputDialog(
                "답변 입력",
                currentText.equals("답변을 입력하려면 여기를 눌러주세요.") ? "" : currentText,
                this::addUserAnswer
        );
    }

    // 일기 결과 화면으로 이동
    private void goToDiaryResult() {
        ArrayList<String> answers = new ArrayList<>();
        for (ChatMessage msg : messageList) {
            if (msg.getViewType() == ChatMessage.VIEW_TYPE_USER_ANSWER &&
                    !msg.getText().equals("답변을 입력하려면 여기를 눌러주세요.")) {
                answers.add(msg.getText());
            }
        }

        Intent intent = new Intent(this, DiaryResultActivity.class);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putStringArrayListExtra("answers", answers);
        String imageUriString = getIntent().getStringExtra("selected_photo_uri");
        if (imageUriString != null) {
            intent.putExtra("photo_uri", imageUriString);
        }

        String dummyTitle = "AI가 생성한 제목";
        String dummyContent = "AI가 답변들을 요약한\n일기 내용입니다.\n\n" + String.join("\n", answers);
        intent.putExtra("diary_title", dummyTitle);
        intent.putExtra("diary_content", dummyContent);

        startActivity(intent);
    }

    // 포토로그 감성 커스텀 입력 다이얼로그
    private void showCustomInputDialog(String title, String defaultText, OnSaveListener listener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_custom, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etInput = dialogView.findViewById(R.id.et_dialog_input);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btn_save);

        tvTitle.setText(title);
        etInput.setText(defaultText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if (!text.isEmpty()) listener.onSave(text);
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public interface OnSaveListener {
        void onSave(String text);
    }
}
