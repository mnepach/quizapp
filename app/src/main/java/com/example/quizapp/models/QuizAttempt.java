package com.example.quizapp.models;

import java.util.Date;

public class QuizAttempt {
    private int id;
    private int userId;
    private int score;
    private int totalQuestions;
    private int difficultyLevel;
    private Date attemptDate;
    private long timeTakenInSeconds;

    public QuizAttempt(int id, int userId, int score, int totalQuestions,
                       int difficultyLevel, Date attemptDate, long timeTakenInSeconds) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.difficultyLevel = difficultyLevel;
        this.attemptDate = attemptDate;
        this.timeTakenInSeconds = timeTakenInSeconds;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Date getAttemptDate() { return attemptDate; }
    public void setAttemptDate(Date attemptDate) { this.attemptDate = attemptDate; }

    public long getTimeTakenInSeconds() { return timeTakenInSeconds; }
    public void setTimeTakenInSeconds(long timeTakenInSeconds) { this.timeTakenInSeconds = timeTakenInSeconds; }
}