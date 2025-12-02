package com.example.photolog_front.model;

import java.util.List;

public class ChatMessageResponse {

    public int session_id;
    public boolean completed;

    public List<String> missing_slots;

    public String next_question;
    public List<String> next_questions;

    public Diary diary;

    // Diary 내부 객체 정의
    public static class Diary {
        public int id;
        public String title;
        public String content;
        public String date;
        public String place;
        public String people;
        public String emotion;
        public String created_at;
    }
}
