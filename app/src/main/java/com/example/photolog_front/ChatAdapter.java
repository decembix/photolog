package com.example.photolog_front;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseViewHolder> {

    private final List<ChatMessage> messageList;
    private final Context context;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getViewType();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ChatMessage.VIEW_TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.item_chat_image, parent, false);
            return new ImageViewHolder(view);
        } else if (viewType == ChatMessage.VIEW_TYPE_AI_QUESTION) {
            View view = inflater.inflate(R.layout.item_chat_ai_question, parent, false);
            return new AiQuestionViewHolder(view);
        } else { // USER ANSWER
            View view = inflater.inflate(R.layout.item_chat_user_answer, parent, false);
            return new UserAnswerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ===== 공통 부모 =====
    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(@NonNull View itemView) { super(itemView); }
        abstract void bind(ChatMessage msg);
    }

    // ===== 이미지 =====
    static class ImageViewHolder extends BaseViewHolder {

        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.chat_img_view);
        }

        @Override
        void bind(ChatMessage msg) {
            Uri uri = msg.getImageUri();
            if (uri != null) imageView.setImageURI(uri);
        }
    }

    // ===== AI 질문 =====
    static class AiQuestionViewHolder extends BaseViewHolder {
        TextView tvQuestion;

        AiQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tv_ai_question);
        }

        @Override
        void bind(ChatMessage msg) {
            if (msg.getText() != null)
                tvQuestion.setText(msg.getText());
        }
    }

    // ===== 사용자 답변 =====
    static class UserAnswerViewHolder extends BaseViewHolder {

        TextView tv;

        UserAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_user_answer);

            itemView.setOnClickListener(v -> {
                Context c = v.getContext();
                if (c instanceof ChatbotActivity) {

                    ChatbotActivity activity = (ChatbotActivity) c;

                    activity.showCustomInputDialog(
                            "답변 입력",
                            tv.getText().toString(),
                            text -> {
                                // UI 반영
                                tv.setText(text);

                                // 서버 전송 & 다음 질문 생성
                                activity.addUserAnswer(text, "text");
                            }
                    );
                }
            });
        }

        @Override
        void bind(ChatMessage msg) {
            if (msg.getText() != null)
                tv.setText(msg.getText());
        }
    }
}
