package com.example.quizapp.models;

public class Question {
    private int id;
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    private int difficultyLevel; // 1 - легкий, 2 - средний, 3 - сложный
    private String hint;
    private int timeInSeconds; // время на ответ

    public Question(int id, String questionText, String[] options, int correctAnswerIndex,
                    int difficultyLevel, String hint, int timeInSeconds) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficultyLevel = difficultyLevel;
        this.hint = hint;
        this.timeInSeconds = timeInSeconds;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }

    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public void setCorrectAnswerIndex(int correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getHint() { return hint; }
    public void setHint(String hint) { this.hint = hint; }

    public int getTimeInSeconds() { return timeInSeconds; }
    public void setTimeInSeconds(int timeInSeconds) { this.timeInSeconds = timeInSeconds; }
}