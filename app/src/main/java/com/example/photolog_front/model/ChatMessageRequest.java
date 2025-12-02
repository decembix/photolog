package com.example.photolog_front.model;

public class ChatMessageRequest {
    public String session_id;
    public String input_type;
    public String content;

    public ChatMessageRequest(String sessionId, String inputType, String content) {
        this.session_id = sessionId;
        this.input_type = inputType;
        this.content = content;
    }
}

