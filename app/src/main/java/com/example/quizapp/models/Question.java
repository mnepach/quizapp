package com.example.quizapp.models;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private long id;
    private String questionText;
    private String[] options;
    private int correctOptionIndex;
    private int difficulty; // 1 - легкий, 2 - средний, 3 - сложный
    private String hint;
    private int pointsValue;
    private int category;

    public Question() {
        options = new String[4];
    }

    public Question(String questionText, String[] options, int correctOptionIndex, int difficulty, String hint, int pointsValue, int category) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.difficulty = difficulty;
        this.hint = hint;
        this.pointsValue = pointsValue;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(int pointsValue) {
        this.pointsValue = pointsValue;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isCorrectAnswer(int selectedOptionIndex) {
        return selectedOptionIndex == correctOptionIndex;
    }
}