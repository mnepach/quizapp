package com.example.quizapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quizapp.models.Question;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizApp.db";
    private static final int DATABASE_VERSION = 1;

    private static QuizDatabaseHelper instance;

    // Синглтон
    public static synchronized QuizDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы пользователей
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " +
                QuizContract.UserEntry.TABLE_NAME + " (" +
                QuizContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.UserEntry.COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                QuizContract.UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                QuizContract.UserEntry.COLUMN_TOTAL_POINTS + " INTEGER DEFAULT 0" +
                ");";

        // Создание таблицы вопросов
        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizContract.QuestionEntry.TABLE_NAME + " (" +
                QuizContract.QuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuestionEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_OPTION1 + " TEXT NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_OPTION2 + " TEXT NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_OPTION3 + " TEXT NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_OPTION4 + " TEXT NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_CORRECT_OPTION + " INTEGER NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_DIFFICULTY + " INTEGER NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_HINT + " TEXT, " +
                QuizContract.QuestionEntry.COLUMN_POINTS_VALUE + " INTEGER NOT NULL, " +
                QuizContract.QuestionEntry.COLUMN_CATEGORY + " INTEGER NOT NULL" +
                ");";

        // Создание таблицы викторин
        final String SQL_CREATE_QUIZZES_TABLE = "CREATE TABLE " +
                QuizContract.QuizEntry.TABLE_NAME + " (" +
                QuizContract.QuizEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuizEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                QuizContract.QuizEntry.COLUMN_DIFFICULTY + " INTEGER NOT NULL, " +
                QuizContract.QuizEntry.COLUMN_TIME_PER_QUESTION + " INTEGER NOT NULL" +
                ");";

        // Создание таблицы связи викторин и вопросов
        final String SQL_CREATE_QUIZ_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizContract.QuizQuestionEntry.TABLE_NAME + " (" +
                QuizContract.QuizQuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuizQuestionEntry.COLUMN_QUIZ_ID + " INTEGER NOT NULL, " +
                QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ID + " INTEGER NOT NULL, " +
                QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ORDER + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + QuizContract.QuizQuestionEntry.COLUMN_QUIZ_ID + ") REFERENCES " +
                QuizContract.QuizEntry.TABLE_NAME + "(" + QuizContract.QuizEntry._ID + "), " +
                "FOREIGN KEY(" + QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ID + ") REFERENCES " +
                QuizContract.QuestionEntry.TABLE_NAME + "(" + QuizContract.QuestionEntry._ID + ")" +
                ");";

        // Создание таблицы истории прохождения викторин
        final String SQL_CREATE_QUIZ_ATTEMPTS_TABLE = "CREATE TABLE " +
                QuizContract.QuizAttemptEntry.TABLE_NAME + " (" +
                QuizContract.QuizAttemptEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuizAttemptEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_QUIZ_ID + " INTEGER NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_QUIZ_TITLE + " TEXT NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_SCORE + " INTEGER NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_TOTAL_QUESTIONS + " INTEGER NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_CORRECT_ANSWERS + " INTEGER NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_HINTS_USED + " INTEGER NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_ATTEMPT_DATE + " TEXT NOT NULL, " +
                QuizContract.QuizAttemptEntry.COLUMN_DIFFICULTY + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + QuizContract.QuizAttemptEntry.COLUMN_USER_ID + ") REFERENCES " +
                QuizContract.UserEntry.TABLE_NAME + "(" + QuizContract.UserEntry._ID + "), " +
                "FOREIGN KEY(" + QuizContract.QuizAttemptEntry.COLUMN_QUIZ_ID + ") REFERENCES " +
                QuizContract.QuizEntry.TABLE_NAME + "(" + QuizContract.QuizEntry._ID + ")" +
                ");";

        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_QUIZZES_TABLE);
        db.execSQL(SQL_CREATE_QUIZ_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_QUIZ_ATTEMPTS_TABLE);

        // Наполнение базы данных начальными данными
        fillQuestionsTable(db);
        createDefaultQuizzes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuizAttemptEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuizQuestionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuizEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuestionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.UserEntry.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuestionsTable(SQLiteDatabase db) {
        // Категория 1 - Общие знания
        Question q1 = new Question("Какая планета находится ближе всего к Солнцу?",
                new String[]{"Венера", "Земля", "Меркурий", "Марс"}, 2, 1,
                "Это самая маленькая планета в Солнечной системе", 5, 1);
        addQuestion(db, q1);

        Question q2 = new Question("Кто написал роман 'Война и мир'?",
                new String[]{"Федор Достоевский", "Лев Толстой", "Иван Тургенев", "Александр Пушкин"}, 1, 1,
                "Этот роман описывает события, связанные с наполеоновскими войнами", 5, 1);
        addQuestion(db, q2);

        Question q3 = new Question("Какая столица Франции?",
                new String[]{"Лондон", "Берлин", "Мадрид", "Париж"}, 3, 1,
                "Через этот город протекает река Сена", 5, 1);
        addQuestion(db, q3);

        // Категория 2 - История
        Question q4 = new Question("В каком году началась Первая мировая война?",
                new String[]{"1914", "1918", "1939", "1945"}, 0, 2,
                "Это было в начале 20-го века", 10, 2);
        addQuestion(db, q4);

        Question q5 = new Question("Кто был первым президентом США?",
                new String[]{"Томас Джефферсон", "Джордж Вашингтон", "Авраам Линкольн", "Бенджамин Франклин"}, 1, 2,
                "Его портрет изображен на однодолларовой купюре", 10, 2);
        addQuestion(db, q5);

        // Категория 3 - Наука
        Question q6 = new Question("Что измеряется в Ньютонах?",
                new String[]{"Скорость", "Время", "Сила", "Масса"}, 2, 2,
                "Это векторная физическая величина", 10, 3);
        addQuestion(db, q6);

        Question q7 = new Question("Какой элемент имеет химический символ 'O'?",
                new String[]{"Озон", "Золото", "Кислород", "Осмий"}, 2, 1,
                "Это один из самых распространенных элементов на Земле", 5, 3);
        addQuestion(db, q7);

        // Категория 4 - Технологии
        Question q8 = new Question("Кто основал компанию Microsoft?",
                new String[]{"Стив Джобс", "Билл Гейтс", "Марк Цукерберг", "Илон Маск"}, 1, 1,
                "Этот человек долгое время был самым богатым человеком в мире", 5, 4);
        addQuestion(db, q8);

        Question q9 = new Question("Какой язык программирования используется для создания Android-приложений?",
                new String[]{"Swift", "Objective-C", "Java/Kotlin", "C#"}, 2, 2,
                "Этот язык был разработан компанией Sun Microsystems", 10, 4);
        addQuestion(db, q9);

        // Категория 5 - Спорт
        Question q10 = new Question("Сколько игроков в футбольной команде на поле?",
                new String[]{"9", "10", "11", "12"}, 2, 1,
                "Включая вратаря", 5, 5);
        addQuestion(db, q10);

        Question q11 = new Question("Какой спорт ассоциируется с Уимблдоном?",
                new String[]{"Гольф", "Футбол", "Теннис", "Крикет"}, 2, 1,
                "Этот турнир проводится в Англии с 1877 года", 5, 5);
        addQuestion(db, q11);

        // Сложные вопросы
        Question q12 = new Question("Какая формула описывает специальную теорию относительности Эйнштейна?",
                new String[]{"E = mc²", "F = ma", "PV = nRT", "E = hf"}, 0, 3,
                "Эта формула показывает эквивалентность массы и энергии", 15, 3);
        addQuestion(db, q12);

        Question q13 = new Question("Какая страна НЕ является членом Совета Безопасности ООН?",
                new String[]{"США", "Россия", "Китай", "Германия"}, 3, 3,
                "Постоянных членов Совбеза ООН всего пять", 15, 2);
        addQuestion(db, q13);

        Question q14 = new Question("Какое самое глубокое озеро в мире?",
                new String[]{"Каспийское море", "Озеро Виктория", "Озеро Байкал", "Великие Озера"}, 2, 2,
                "Это озеро находится в Сибири", 10, 1);
        addQuestion(db, q14);

        Question q15 = new Question("Какой язык программирования был создан первым?",
                new String[]{"Фортран", "Кобол", "Бейсик", "Паскаль"}, 0, 3,
                "Он был разработан в 1950-х годах", 15, 4);
        addQuestion(db, q15);
    }

    private void addQuestion(SQLiteDatabase db, Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuizContract.QuestionEntry.COLUMN_TEXT, question.getQuestionText());
        cv.put(QuizContract.QuestionEntry.COLUMN_OPTION1, question.getOptions()[0]);
        cv.put(QuizContract.QuestionEntry.COLUMN_OPTION2, question.getOptions()[1]);
        cv.put(QuizContract.QuestionEntry.COLUMN_OPTION3, question.getOptions()[2]);
        cv.put(QuizContract.QuestionEntry.COLUMN_OPTION4, question.getOptions()[3]);
        cv.put(QuizContract.QuestionEntry.COLUMN_CORRECT_OPTION, question.getCorrectOptionIndex());
        cv.put(QuizContract.QuestionEntry.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuizContract.QuestionEntry.COLUMN_HINT, question.getHint());
        cv.put(QuizContract.QuestionEntry.COLUMN_POINTS_VALUE, question.getPointsValue());
        cv.put(QuizContract.QuestionEntry.COLUMN_CATEGORY, question.getCategory());
        db.insert(QuizContract.QuestionEntry.TABLE_NAME, null, cv);
    }

    private void createDefaultQuizzes(SQLiteDatabase db) {
        // Создаем викторину "Легкая викторина"
        ContentValues quizCV = new ContentValues();
        quizCV.put(QuizContract.QuizEntry.COLUMN_TITLE, "Легкая викторина");
        quizCV.put(QuizContract.QuizEntry.COLUMN_DIFFICULTY, 1);
        quizCV.put(QuizContract.QuizEntry.COLUMN_TIME_PER_QUESTION, 30);
        long easyQuizId = db.insert(QuizContract.QuizEntry.TABLE_NAME, null, quizCV);

        // Добавляем вопросы к легкой викторине
        addQuestionToQuiz(db, easyQuizId, 1, 0);
        addQuestionToQuiz(db, easyQuizId, 2, 1);
        addQuestionToQuiz(db, easyQuizId, 3, 2);
        addQuestionToQuiz(db, easyQuizId, 7, 3);
        addQuestionToQuiz(db, easyQuizId, 8, 4);
        addQuestionToQuiz(db, easyQuizId, 10, 5);
        addQuestionToQuiz(db, easyQuizId, 11, 6);

        // Создаем викторину "Средняя викторина"
        quizCV.clear();
        quizCV.put(QuizContract.QuizEntry.COLUMN_TITLE, "Средняя викторина");
        quizCV.put(QuizContract.QuizEntry.COLUMN_DIFFICULTY, 2);
        quizCV.put(QuizContract.QuizEntry.COLUMN_TIME_PER_QUESTION, 25);
        long mediumQuizId = db.insert(QuizContract.QuizEntry.TABLE_NAME, null, quizCV);

        // Добавляем вопросы к средней викторине
        addQuestionToQuiz(db, mediumQuizId, 4, 0);
        addQuestionToQuiz(db, mediumQuizId, 5, 1);
        addQuestionToQuiz(db, mediumQuizId, 6, 2);
        addQuestionToQuiz(db, mediumQuizId, 9, 3);
        addQuestionToQuiz(db, mediumQuizId, 14, 4);

        // Создаем викторину "Сложная викторина"
        quizCV.clear();
        quizCV.put(QuizContract.QuizEntry.COLUMN_TITLE, "Сложная викторина");
        quizCV.put(QuizContract.QuizEntry.COLUMN_DIFFICULTY, 3);
        quizCV.put(QuizContract.QuizEntry.COLUMN_TIME_PER_QUESTION, 20);
        long hardQuizId = db.insert(QuizContract.QuizEntry.TABLE_NAME, null, quizCV);

        // Добавляем вопросы к сложной викторине
        addQuestionToQuiz(db, hardQuizId, 12, 0);
        addQuestionToQuiz(db, hardQuizId, 13, 1);
        addQuestionToQuiz(db, hardQuizId, 15, 2);
    }

    private void addQuestionToQuiz(SQLiteDatabase db, long quizId, long questionId, int order) {
        ContentValues cv = new ContentValues();
        cv.put(QuizContract.QuizQuestionEntry.COLUMN_QUIZ_ID, quizId);
        cv.put(QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ID, questionId);
        cv.put(QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ORDER, order);
        db.insert(QuizContract.QuizQuestionEntry.TABLE_NAME, null, cv);
    }

    // Методы для работы с пользователями
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(QuizContract.UserEntry.COLUMN_USERNAME, user.getUsername());
        cv.put(QuizContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
        cv.put(QuizContract.UserEntry.COLUMN_TOTAL_POINTS, user.getTotalPoints());
        long id = db.insert(QuizContract.UserEntry.TABLE_NAME, null, cv);
        db.close();
        return id;
    }

    public User getUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.UserEntry.TABLE_NAME,
                new String[]{
                        QuizContract.UserEntry._ID,
                        QuizContract.UserEntry.COLUMN_USERNAME,
                        QuizContract.UserEntry.COLUMN_PASSWORD,
                        QuizContract.UserEntry.COLUMN_TOTAL_POINTS
                },
                QuizContract.UserEntry._ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.UserEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_TOTAL_POINTS))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.UserEntry.TABLE_NAME,
                new String[]{
                        QuizContract.UserEntry._ID,
                        QuizContract.UserEntry.COLUMN_USERNAME,
                        QuizContract.UserEntry.COLUMN_PASSWORD,
                        QuizContract.UserEntry.COLUMN_TOTAL_POINTS
                },
                QuizContract.UserEntry.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.UserEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_TOTAL_POINTS))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    // Add to QuizDatabaseHelper.java
    public User getUserByCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.UserEntry.TABLE_NAME,
                new String[]{
                        QuizContract.UserEntry._ID,
                        QuizContract.UserEntry.COLUMN_USERNAME,
                        QuizContract.UserEntry.COLUMN_PASSWORD,
                        QuizContract.UserEntry.COLUMN_TOTAL_POINTS
                },
                QuizContract.UserEntry.COLUMN_USERNAME + " = ? AND " +
                        QuizContract.UserEntry.COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.UserEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.UserEntry.COLUMN_TOTAL_POINTS))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.UserEntry.TABLE_NAME,
                new String[]{QuizContract.UserEntry._ID},
                QuizContract.UserEntry.COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null
        );

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    public long saveUser(User user) {
        return addUser(user);
    }

    public void updateUserPoints(long userId, int points) {
        SQLiteDatabase db = this.getWritableDatabase();
        User user = getUser(userId);
        if (user != null) {
            ContentValues cv = new ContentValues();
            cv.put(QuizContract.UserEntry.COLUMN_TOTAL_POINTS, user.getTotalPoints() + points);
            db.update(
                    QuizContract.UserEntry.TABLE_NAME,
                    cv,
                    QuizContract.UserEntry._ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        }
        db.close();
    }

    // Методы для работы с викторинами
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.QuizEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                QuizContract.QuizEntry.COLUMN_DIFFICULTY + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Quiz quiz = new Quiz();
                quiz.setId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry._ID)));
                quiz.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry.COLUMN_TITLE)));
                quiz.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry.COLUMN_DIFFICULTY)));
                quiz.setTimePerQuestionInSeconds(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry.COLUMN_TIME_PER_QUESTION)));

                // Получаем вопросы для этой викторины
                quiz.setQuestions(getQuestionsForQuiz(quiz.getId()));

                quizList.add(quiz);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return quizList;
    }

    public Quiz getQuiz(long quizId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.QuizEntry.TABLE_NAME,
                null,
                QuizContract.QuizEntry._ID + " = ?",
                new String[]{String.valueOf(quizId)},
                null, null, null
        );

        Quiz quiz = null;
        if (cursor != null && cursor.moveToFirst()) {
            quiz = new Quiz();
            quiz.setId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry._ID)));
            quiz.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry.COLUMN_TITLE)));
            quiz.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry.COLUMN_DIFFICULTY)));
            quiz.setTimePerQuestionInSeconds(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizEntry.COLUMN_TIME_PER_QUESTION)));

            // Получаем вопросы для этой викторины
            quiz.setQuestions(getQuestionsForQuiz(quiz.getId()));

            cursor.close();
        }
        db.close();
        return quiz;
    }

    // Изменение метода с private на public для доступа из QuestionFragment
    public List<Question> getQuestionsForQuiz(long quizId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Question> questionList = new ArrayList<>();

        // SQL запрос для соединения таблиц и получения вопросов в нужном порядке
        String query = "SELECT q.* FROM " + QuizContract.QuestionEntry.TABLE_NAME + " q " +
                "JOIN " + QuizContract.QuizQuestionEntry.TABLE_NAME + " qq " +
                "ON q." + QuizContract.QuestionEntry._ID + " = qq." + QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ID + " " +
                "WHERE qq." + QuizContract.QuizQuestionEntry.COLUMN_QUIZ_ID + " = ? " +
                "ORDER BY qq." + QuizContract.QuizQuestionEntry.COLUMN_QUESTION_ORDER + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(quizId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry._ID)));
                question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_TEXT)));

                String[] options = new String[4];
                options[0] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION1));
                options[1] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION2));
                options[2] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION3));
                options[3] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION4));
                question.setOptions(options);

                question.setCorrectOptionIndex(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_CORRECT_OPTION)));
                question.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_DIFFICULTY)));
                question.setHint(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_HINT)));
                question.setPointsValue(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_POINTS_VALUE)));
                question.setCategory(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_CATEGORY)));

                questionList.add(question);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return questionList;
    }

    // Методы для работы с историей прохождения викторин
    public long addQuizAttempt(QuizAttempt attempt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_USER_ID, attempt.getUserId());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_QUIZ_ID, attempt.getQuizId());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_QUIZ_TITLE, attempt.getQuizTitle());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_SCORE, attempt.getScore());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_TOTAL_QUESTIONS, attempt.getTotalQuestions());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_CORRECT_ANSWERS, attempt.getCorrectAnswers());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_HINTS_USED, attempt.getHintsUsed());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_ATTEMPT_DATE, sdf.format(attempt.getAttemptDate()));
        cv.put(QuizContract.QuizAttemptEntry.COLUMN_DIFFICULTY, attempt.getDifficulty());

        long id = db.insert(QuizContract.QuizAttemptEntry.TABLE_NAME, null, cv);
        db.close();

        // Обновляем общие очки пользователя
        updateUserPoints(attempt.getUserId(), attempt.getScore());

        return id;
    }

    public List<QuizAttempt> getQuizAttemptsForUser(long userId) {
        List<QuizAttempt> attemptList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.QuizAttemptEntry.TABLE_NAME,
                null,
                QuizContract.QuizAttemptEntry.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                QuizContract.QuizAttemptEntry.COLUMN_ATTEMPT_DATE + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            do {
                QuizAttempt attempt = new QuizAttempt();
                attempt.setId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry._ID)));
                attempt.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_USER_ID)));
                attempt.setQuizId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_QUIZ_ID)));
                attempt.setQuizTitle(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_QUIZ_TITLE)));
                attempt.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_SCORE)));
                attempt.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_TOTAL_QUESTIONS)));
                attempt.setCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_CORRECT_ANSWERS)));
                attempt.setHintsUsed(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_HINTS_USED)));
                attempt.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_DIFFICULTY)));

                try {
                    String dateString = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuizAttemptEntry.COLUMN_ATTEMPT_DATE));
                    attempt.setAttemptDate(sdf.parse(dateString));
                } catch (Exception e) {
                    attempt.setAttemptDate(new Date());
                }

                attemptList.add(attempt);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return attemptList;
    }

    public List<Question> getQuestionsByDifficulty(int difficulty) {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                QuizContract.QuestionEntry.TABLE_NAME,
                null,
                QuizContract.QuestionEntry.COLUMN_DIFFICULTY + " = ?",
                new String[]{String.valueOf(difficulty)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getLong(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry._ID)));
                question.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_TEXT)));

                String[] options = new String[4];
                options[0] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION1));
                options[1] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION2));
                options[2] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION3));
                options[3] = cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_OPTION4));
                question.setOptions(options);

                question.setCorrectOptionIndex(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_CORRECT_OPTION)));
                question.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_DIFFICULTY)));
                question.setHint(cursor.getString(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_HINT)));
                question.setPointsValue(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_POINTS_VALUE)));
                question.setCategory(cursor.getInt(cursor.getColumnIndexOrThrow(QuizContract.QuestionEntry.COLUMN_CATEGORY)));

                questionList.add(question);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return questionList;
    }
}


