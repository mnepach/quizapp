package com.example.quizapp.models;

public class Hint {
    private long id;
    private long questionId;
    private int pointsCost;

    public Hint() {
    }

    public Hint(long questionId, int pointsCost) {
        this.questionId = questionId;
        this.pointsCost = pointsCost;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }
    public int getPointsCost() { return pointsCost; }
    public void setPointsCost(int pointsCost) { this.pointsCost = pointsCost; }
}