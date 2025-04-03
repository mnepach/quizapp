package com.example.quizapp.models;

public class User {
    private long id;
    private String username;
    private String password; // В реальном приложении пароли следует хешировать
    private int totalPoints;

    public User() {
    }

    public User(long id, String username, String password, int totalPoints) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.totalPoints = totalPoints;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }
}