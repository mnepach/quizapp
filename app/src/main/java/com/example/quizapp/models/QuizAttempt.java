package com.example.quizapp.models;

import java.util.Date;

public class QuizAttempt {
    private long id;
    private long userId;
    private long quizId;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int hintsUsed;
    private Date attemptDate;
    private Date startTime; // Added field for tracking start time
    private Date endTime;   // Added field for tracking end time
    private String quizTitle;
    private int difficulty;

    public QuizAttempt() {
        attemptDate = new Date();
        startTime = new Date(); // Initialize start time on creation
    }

    public QuizAttempt(long userId, long quizId, String quizTitle, int difficulty) {
        this.userId = userId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.difficulty = difficulty;
        this.score = 0;
        this.correctAnswers = 0;
        this.hintsUsed = 0;
        this.attemptDate = new Date();
        this.startTime = new Date(); // Initialize start time on creation
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addToScore(int points) {
        this.score += points;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void incrementCorrectAnswers() {
        this.correctAnswers++;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    public void incrementHintsUsed() {
        this.hintsUsed++;
    }

    public Date getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(Date attemptDate) {
        this.attemptDate = attemptDate;
    }

    // Added methods for start and end time
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(attemptDate);
    }

    public String getPerformancePercentage() {
        if (totalQuestions == 0) return "0%";
        return (correctAnswers * 100 / totalQuestions) + "%";
    }
}