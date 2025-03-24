package com.example.quizapp.database;

import android.provider.BaseColumns;

public final class QuizContract {

    private QuizContract() {} // Приватный конструктор для предотвращения создания экземпляров

    // Таблица пользователей
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_TOTAL_POINTS = "total_points";
    }

    // Таблица вопросов
    public static class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COLUMN_TEXT = "question_text";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_OPTION4 = "option4";
        public static final String COLUMN_CORRECT_OPTION = "correct_option";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_HINT = "hint";
        public static final String COLUMN_POINTS_VALUE = "points_value";
        public static final String COLUMN_CATEGORY = "category";
    }

    // Таблица викторин
    public static class QuizEntry implements BaseColumns {
        public static final String TABLE_NAME = "quizzes";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_TIME_PER_QUESTION = "time_per_question";
    }

    // Таблица связи викторин и вопросов
    public static class QuizQuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUIZ_ID = "quiz_id";
        public static final String COLUMN_QUESTION_ID = "question_id";
        public static final String COLUMN_QUESTION_ORDER = "question_order";
    }

    // Таблица истории прохождения викторин
    public static class QuizAttemptEntry implements BaseColumns {
        public static final String TABLE_NAME = "quiz_attempts";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_QUIZ_ID = "quiz_id";
        public static final String COLUMN_QUIZ_TITLE = "quiz_title";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_TOTAL_QUESTIONS = "total_questions";
        public static final String COLUMN_CORRECT_ANSWERS = "correct_answers";
        public static final String COLUMN_HINTS_USED = "hints_used";
        public static final String COLUMN_ATTEMPT_DATE = "attempt_date";
        public static final String COLUMN_DIFFICULTY = "difficulty";
    }
}