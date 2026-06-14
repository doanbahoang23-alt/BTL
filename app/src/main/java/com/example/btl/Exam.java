package com.example.btl;

public class Exam {
    private int id;
    private String title;
    private String createdAt;

    public Exam(int id, String title, String createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCreatedAt() { return createdAt; }
}