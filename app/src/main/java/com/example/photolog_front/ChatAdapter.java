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
            // 클릭 리스너는 ViewHolder의 bind 메소드에서 직접 처리하므로 여기서는 제거
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 1. 모든 ViewHolder의 부모가 될 BaseViewHolder 생성
    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        // bind 메소드의 파라미터 타입을 Object로 통일
        abstract void bind(Object data);
    }

    // ImageViewHolder가 BaseViewHolder를 상속
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

    // AiQuestionViewHolder가 BaseViewHolder를 상속
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

    // UserAnswerViewHolder가 BaseViewHolder를 상속 (수정된 부분)
    static class UserAnswerViewHolder extends BaseViewHolder {
        TextView tv;
        UserAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_user_answer);
        }

        @Override
        void bind(Object data) {
            if (data instanceof String) tv.setText((String) data);

            // ViewHolder 자체에서 클릭 리스너를 설정
            itemView.setOnClickListener(v -> {
                Context c = v.getContext();
                if (c instanceof ChatbotActivity) {
                    // ChatbotActivity의 showTextInputDialog를 호출
                    ((ChatbotActivity) c).showTextInputDialog(tv.getText().toString());
                }
            });
        }
    }
}