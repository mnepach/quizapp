package com.example.quizapp.database;

import android.util.Log;

import com.example.quizapp.models.Question;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLHelper {
    private static final String TAG = "MySQLHelper";

    // Замените на ваши данные для подключения
    private static final String DB_URL = "jdbc:mysql://your_server_ip:3306/quiz_app";
    private static final String USER = "your_username";
    private static final String PASS = "your_password";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "MySQL JDBC Driver not found", e);
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // Метод для регистрации пользователя
    public User registerUser(String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // В реальном приложении нужно хешировать пароль
            pstmt.setString(3, email);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, username, password, email);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    // Метод для авторизации пользователя
    public User loginUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // В реальном приложении нужно хешировать пароль

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                } else {
                    return null; // Пользователь не найден
                }
            }
        }
    }

    // Получение вопросов по уровню сложности
    public List<Question> getQuestionsByDifficulty(int difficultyLevel) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE difficulty_level = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, difficultyLevel);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] options = new String[4];
                    options[0] = rs.getString("option1");
                    options[1] = rs.getString("option2");
                    options[2] = rs.getString("option3");
                    options[3] = rs.getString("option4");

                    Question question = new Question(
                            rs.getInt("id"),
                            rs.getString("question_text"),
                            options,
                            rs.getInt("correct_answer_index"),
                            rs.getInt("difficulty_level"),
                            rs.getString("hint"),
                            rs.getInt("time_in_seconds")
                    );

                    questions.add(question);
                }
            }
        }

        return questions;
    }

    // Получение всех вопросов
    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String[] options = new String[4];
                options[0] = rs.getString("option1");
                options[1] = rs.getString("option2");
                options[2] = rs.getString("option3");
                options[3] = rs.getString("option4");

                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        options,
                        rs.getInt("correct_answer_index"),
                        rs.getInt("difficulty_level"),
                        rs.getString("hint"),
                        rs.getInt("time_in_seconds")
                );

                questions.add(question);
            }
        }

        return questions;
    }

    // Сохранение результата прохождения викторины
    public QuizAttempt saveQuizAttempt(int userId, int score, int totalQuestions,
                                       int difficultyLevel, long timeTakenInSeconds) throws SQLException {
        String sql = "INSERT INTO quiz_attempts (user_id, score, total_questions, difficulty_level, attempt_date, time_taken_seconds) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            Date now = new Date();

            pstmt.setInt(1, userId);
            pstmt.setInt(2, score);
            pstmt.setInt(3, totalQuestions);
            pstmt.setInt(4, difficultyLevel);
            pstmt.setTimestamp(5, new Timestamp(now.getTime()));
            pstmt.setLong(6, timeTakenInSeconds);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving quiz attempt failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new QuizAttempt(id, userId, score, totalQuestions, difficultyLevel, now, timeTakenInSeconds);
                } else {
                    throw new SQLException("Saving quiz attempt failed, no ID obtained.");
                }
            }
        }
    }

    // Получение истории прохождений для пользователя
    public List<QuizAttempt> getUserAttempts(int userId) throws SQLException {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? ORDER BY attempt_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    QuizAttempt attempt = new QuizAttempt(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("score"),
                            rs.getInt("total_questions"),
                            rs.getInt("difficulty_level"),
                            new Date(rs.getTimestamp("attempt_date").getTime()),
                            rs.getLong("time_taken_seconds")
                    );

                    attempts.add(attempt);
                }
            }
        }

        return attempts;
    }
}