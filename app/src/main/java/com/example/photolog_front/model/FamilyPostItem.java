// com/example/photolog_front/model/FamilyPostItem.java
package com.example.photolog_front.model;

public class FamilyPostItem {
    public int id;
    public String title;
    public String content;
    public String created_at;
    public int user_id;
    public String user_name;
    public PhotoItem photo;

    public static class PhotoItem {
        public int id;
        public String file_path;
        public String exif_datetime;
        public Double latitude;
        public Double longitude;
        public String created_at;
    }
}

