package com.example.quizapp.models;

import java.util.Date;

public class QuizAttempt {
    private long id;
    private long userId;
    private long quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private int hintsUsed;
    private Date attemptDate;
    private int difficulty;
    private Date startTime; // Оставляем для возможного использования
    private Date endTime;   // Оставляем для возможного использования
    private long timeSpent; // Новое поле для хранения времени прохождения

    // Конструктор по умолчанию
    public QuizAttempt() {
        this.score = 0;
        this.correctAnswers = 0;
        this.hintsUsed = 0;
        this.timeSpent = 0; // Инициализируем timeSpent
    }

    // Геттеры и сеттеры
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

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
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

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

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

    // Геттер и сеттер для timeSpent
    public long getTimeSpent() {
        return timeSpent; // Теперь возвращаем сохранённое значение
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }
}