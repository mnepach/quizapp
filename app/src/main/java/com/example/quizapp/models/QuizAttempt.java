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
    private String quizTitle;
    private int difficulty;

    public QuizAttempt() {
        attemptDate = new Date();
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