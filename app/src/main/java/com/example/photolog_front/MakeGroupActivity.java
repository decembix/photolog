package com.example.photolog_front;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MakeGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private EditText maxMemberEditText;
    private Button createGroupBtn;

    private TextView errorTextView;
    private TextView groupCodeTextView;
    private LinearLayout groupCodeLayout;  // 그룹 코드 박스(전체 레이아웃)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group);

        // XML 연결
        groupNameEditText = findViewById(R.id.group_name);
        maxMemberEditText = findViewById(R.id.max_member);
        createGroupBtn = findViewById(R.id.btnLogin);

        errorTextView = findViewById(R.id.tvGroupError);
        groupCodeTextView = findViewById(R.id.groupCode);
        groupCodeLayout = findViewById(R.id.groupCodeLayout);

        // 처음에는 그룹 코드 박스를 숨김
        groupCodeLayout.setVisibility(View.GONE);

        // 버튼 클릭 리스너
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndGenerateCode();
            }
        });
    }

    // 입력 체크 + 코드 생성 메서드
    private void checkAndGenerateCode() {

        String groupName = groupNameEditText.getText().toString().trim();
        String maxMember = maxMemberEditText.getText().toString().trim();

        // 하나라도 비었을 경우 → 오류 문구 표시 + 코드 숨김
        if (groupName.isEmpty() || maxMember.isEmpty()) {
            errorTextView.setVisibility(View.VISIBLE);  // 오류 문구 보이기
            groupCodeLayout.setVisibility(View.GONE);   // 코드 숨기기
            return;
        }

        // 모든 칸이 채워진 경우 → 오류 문구 숨기기
        errorTextView.setVisibility(View.GONE);

        // 랜덤 6자리 코드 생성
        String code = generateRandomCode(6);
        groupCodeTextView.setText(code);

        // 생성된 코드 보이기
        groupCodeLayout.setVisibility(View.VISIBLE);
    }

    // 랜덤 영문 대문자 + 숫자 6자리 생성 함수
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
