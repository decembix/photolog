package com.example.photolog_front;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photolog_front.model.ChatMessageRequest;
import com.example.photolog_front.model.ChatMessageResponse;
import com.example.photolog_front.network.ApiService;
import com.example.photolog_front.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private ImageButton btnMic;
    private AppCompatButton btnFinishChat;

    private String sessionId;

    private String imageUriString;

    private static final int MIN_ANSWERS = 3;
    private int answerCount = 0;

    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            ArrayList<String> results =
                                    result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                            if (results != null && !results.isEmpty()) {
                                addUserAnswer(results.get(0), "voice");
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // ====== Intent 데이터 받아오기 ======
        int sessionId = getIntent().getIntExtra("session_id", -1);
        String firstQuestion = getIntent().getStringExtra("question");
        imageUriString = getIntent().getStringExtra("selected_photo_uri");

        // ====== UI 초기화 ======
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        btnMic = findViewById(R.id.btn_mic);
        btnFinishChat = findViewById(R.id.btn_finish_chat);

        btnFinishChat.setVisibility(View.INVISIBLE);
        btnFinishChat.setEnabled(false);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        // ====== 첫 메시지 UI 구성 ======
        Uri imageUri = imageUriString != null ? Uri.parse(imageUriString) : null;

        if (imageUri != null) {
            messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_IMAGE, null, imageUri));
        }

        if (firstQuestion != null) {
            messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_AI_QUESTION, firstQuestion, null));
        }

        messageList.add(new ChatMessage(
                ChatMessage.VIEW_TYPE_USER_ANSWER,
                "답변을 입력하려면 여기를 눌러주세요.",
                null)
        );

        chatAdapter.notifyDataSetChanged();

        // 마이크 버튼 클릭
        btnMic.setOnClickListener(v -> {
            if (checkAudioPermission()) startSpeechRecognition();
        });

        // 종료 버튼
        btnFinishChat.setOnClickListener(v -> goToDiaryResult());

        // 로고 클릭 시 종료 다이얼로그
        findViewById(R.id.layout_logo).setOnClickListener(v -> showExitConfirmDialog());
    }

    // =====================================
    //       음성 인식 권한 체크
    // =====================================
    private boolean checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            return false;
        }
        return true;
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        speechLauncher.launch(intent);
    }

    // =====================================
    //          사용자 답변 처리
    // =====================================
    public void addUserAnswer(String text, String inputType) {

        // 마지막 "답변 입력칸"을 찾아 교체
        int lastAnsIdx = -1;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(i).getViewType() == ChatMessage.VIEW_TYPE_USER_ANSWER) {
                lastAnsIdx = i;
                break;
            }
        }

        if (lastAnsIdx != -1) {
            messageList.set(lastAnsIdx, new ChatMessage(ChatMessage.VIEW_TYPE_USER_ANSWER, text, null));
            chatAdapter.notifyItemChanged(lastAnsIdx);
        }

        answerCount++;
        if (answerCount >= MIN_ANSWERS) {
            btnFinishChat.setVisibility(View.VISIBLE);
            btnFinishChat.setEnabled(true);
            btnFinishChat.setAlpha(1f);
        }

        // 서버로 전송
        sendUserMessageToServer(text);
    }

    // =====================================
    //          서버로 답변 전송
    // =====================================
    private void sendUserMessageToServer(String content) {

        int sid = Integer.parseInt(sessionId);  // String → int

        ChatMessageRequest body = new ChatMessageRequest(content);
        ApiService api = RetrofitClient.getApiService(this);

        api.sendChatAnswer(sid, body).enqueue(new Callback<ChatMessageResponse>() {
            @Override
            public void onResponse(Call<ChatMessageResponse> call,
                                   Response<ChatMessageResponse> response) {

                if (!response.isSuccessful()) {
                    Log.e("Chatbot", "서버 오류: " + response.code());
                    return;
                }

                ChatMessageResponse res = response.body();

                if (!res.completed) {
                    // 다음 질문 생성
                    addNextQuestion(res.next_question);

                } else {
                    // 일기 완성 → DiaryResultActivity 이동
                    Intent intent = new Intent(ChatbotActivity.this, DiaryResultActivity.class);
                    intent.putExtra("diary_title", res.diary.title);
                    intent.putExtra("diary_content", res.diary.content);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ChatMessageResponse> call, Throwable t) {
                Toast.makeText(ChatbotActivity.this,
                        "서버와 통신 중 오류가 발생했습니다: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
    }

    // =====================================
    //         다음 질문 UI 추가
    // =====================================
    private void addNextQuestion(String question) {
        messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_AI_QUESTION, question, null));
        messageList.add(new ChatMessage(ChatMessage.VIEW_TYPE_USER_ANSWER,
                "답변을 입력하려면 여기를 눌러주세요.", null));

        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    // =====================================
    //        일기 완성 → 화면 이동
    // =====================================
    private void goToDiaryResultWithData(ChatMessageResponse res) {

        Intent intent = new Intent(this, DiaryResultActivity.class);
        intent.putExtra("diary_title", res.diary.title);
        intent.putExtra("diary_content", res.diary.content);
        intent.putExtra("photo_uri", imageUriString);
        startActivity(intent);
    }

    private void goToDiaryResult() {
        // 강제로 만드는 임시 버전 (질문이 3개 채워졌을 때)
        Intent intent = new Intent(this, DiaryResultActivity.class);
        intent.putExtra("diary_title", "AI가 생성한 제목");
        intent.putExtra("diary_content", "입력한 답변을 기반으로 일기를 자동 생성합니다.");
        startActivity(intent);
    }

    // =====================================
    //       종료 확인 다이얼로그
    // =====================================
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
            finish();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // =====================================
    //     사용자 입력 커스텀 다이얼로그
    // =====================================
    public void showCustomInputDialog(String title,
                                      String defaultText,
                                      OnSaveListener listener) {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_custom, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etInput = dialogView.findViewById(R.id.et_dialog_input);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btn_save);

        tvTitle.setText(title);

        if (defaultText == null || defaultText.equals("답변을 입력하려면 여기를 눌러주세요.")) {
            etInput.setHint("답변을 입력하려면 여기를 눌러주세요.");
            etInput.setText("");
        } else {
            etInput.setText(defaultText);
        }

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
