package com.example.timecapsule.Bean;

public class Diary {
    private int ID = -1;
    private String title;
    private String content;
    private String date;
    private String image;
    private String lock;


    public Diary(){
        this.lock = "";
    }

    public Diary(String title, String content, String date, String image) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.image = image;
        this.lock = "";
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }
}
