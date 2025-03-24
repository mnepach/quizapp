package com.example.quizapp.models;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private long id;
    private String title;
    private int difficulty;
    private List<Question> questions;
    private int timePerQuestionInSeconds;

    public Quiz() {
        questions = new ArrayList<>();
    }

    public Quiz(String title, int difficulty, int timePerQuestionInSeconds) {
        this.title = title;
        this.difficulty = difficulty;
        this.timePerQuestionInSeconds = timePerQuestionInSeconds;
        this.questions = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public int getTimePerQuestionInSeconds() {
        return timePerQuestionInSeconds;
    }

    public void setTimePerQuestionInSeconds(int timePerQuestionInSeconds) {
        this.timePerQuestionInSeconds = timePerQuestionInSeconds;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getMaxScore() {
        int totalPoints = 0;
        for (Question question : questions) {
            totalPoints += question.getPointsValue();
        }
        return totalPoints;
    }
}