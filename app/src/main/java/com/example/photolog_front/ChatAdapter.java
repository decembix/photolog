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
        } else { // VIEW_TYPE_USER_ANSWER
            View view = inflater.inflate(R.layout.item_chat_user_answer, parent, false);
            return new UserAnswerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(message.getImageUri());
        } else if (holder instanceof AiQuestionViewHolder) {
            ((AiQuestionViewHolder) holder).bind(message.getText());
        } else if (holder instanceof UserAnswerViewHolder) {
            ((UserAnswerViewHolder) holder).bind(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 모든 ViewHolder의 부모
    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(@NonNull View itemView) { super(itemView); }
        abstract void bind(Object data);
    }

    // 📸 이미지 (첫 번째 질문 사진)
    static class ImageViewHolder extends BaseViewHolder {
        ImageView imageView;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.chat_img_view);
        }
        @Override
        void bind(Object data) {
            if (data instanceof Uri) {
                imageView.setImageURI((Uri) data);
            }
        }
    }

    // 💬 AI 질문 말풍선
    static class AiQuestionViewHolder extends BaseViewHolder {
        TextView textView;
        AiQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_ai_question);
        }
        @Override
        void bind(Object data) {
            if (data instanceof String) {
                textView.setText((String) data);
            }
        }
    }

    // 🙋 사용자 답변 말풍선
    static class UserAnswerViewHolder extends BaseViewHolder {
        TextView tv;
        UserAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_user_answer);
        }

        @Override
        void bind(Object data) {
            if (data instanceof String)
                tv.setText((String) data);

            // 클릭 → 텍스트 입력 다이얼로그 표시
            itemView.setOnClickListener(v -> {
                Context c = v.getContext();
                if (c instanceof ChatbotActivity) {
                    ChatbotActivity activity = (ChatbotActivity) c;

                    // 다이얼로그를 띄워 사용자가 입력하면
                    activity.showCustomInputDialog(
                            "답변 입력",
                            tv.getText().toString(),
                            text -> {
                                // ✅ 입력 완료 시, ChatbotActivity의 addUserAnswer(String, String) 호출
                                activity.addUserAnswer(text, "text");
                            }
                    );
                }
            });
        }
    }
}
