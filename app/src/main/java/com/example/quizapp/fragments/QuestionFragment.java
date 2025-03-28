package com.example.quizapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.example.quizapp.utils.SoundUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QuestionFragment extends Fragment {

    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private Button btnOption1;
    private Button btnOption2;
    private Button btnOption3;
    private Button btnOption4;
    private ProgressBar progressTimer;
    private TextView tvTimer;
    private ImageView ivHint;
    private TextView tvHint;
    private CardView cardHint;
    private Button btnUseHint;

    private Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalPoints = 0;
    private QuizAttempt currentAttempt;
    private CountDownTimer timer;
    private boolean questionAnswered = false;
    private long quizStartTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        // Инициализация UI элементов
        tvQuestionNumber = view.findViewById(R.id.tv_question_number);
        tvQuestion = view.findViewById(R.id.tv_question);
        btnOption1 = view.findViewById(R.id.btn_option_1);
        btnOption2 = view.findViewById(R.id.btn_option_2);
        btnOption3 = view.findViewById(R.id.btn_option_3);
        btnOption4 = view.findViewById(R.id.btn_option_4);
        progressTimer = view.findViewById(R.id.progress_timer);
        tvTimer = view.findViewById(R.id.tv_timer);
        ivHint = view.findViewById(R.id.iv_hint);
        tvHint = view.findViewById(R.id.tv_hint);
        cardHint = view.findViewById(R.id.card_hint);
        btnUseHint = view.findViewById(R.id.btn_use_hint);

        // Получаем ID викторины из аргументов
        if (getArguments() != null) {
            long quizId = getArguments().getLong("quiz_id");
            loadQuiz(quizId);
        }

        // Настройка кнопки подсказки
        btnUseHint.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            showHint();
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void loadQuiz(long quizId) {
        QuizDatabaseHelper dbHelper = QuizDatabaseHelper.getInstance(getContext());
        currentQuiz = dbHelper.getQuiz(quizId);

        if (currentQuiz != null) {
            // Получаем вопросы для викторины
            questions = dbHelper.getQuestionsForQuiz(quizId);

            // Перемешиваем вопросы для случайного порядка
            Collections.shuffle(questions);

            // Создаем новую попытку прохождения викторины
            long userId = SharedPreferencesManager.getLoggedInUserId();
            if (userId != -1) {
                currentAttempt = new QuizAttempt();
                currentAttempt.setUserId(userId);
                currentAttempt.setQuizId(quizId);
                currentAttempt.setStartTime(new Date());
                quizStartTime = System.currentTimeMillis();
            }

            // Показываем первый вопрос
            showQuestion(0);
        }
    }

    private void showQuestion(int index) {
        // Сбрасываем состояние
        questionAnswered = false;
        if (timer != null) {
            timer.cancel();
        }

        // Получаем текущий вопрос
        Question currentQuestion = questions.get(index);

        // Обновляем заголовок
        tvQuestionNumber.setText(getString(R.string.question_number, index + 1, questions.size()));

        // Устанавливаем текст вопроса
        tvQuestion.setText(currentQuestion.getText());

        // Получаем варианты ответов
        List<String> options = new ArrayList<>();
        options.add(currentQuestion.getCorrectAnswer());
        options.add(currentQuestion.getOption1());
        options.add(currentQuestion.getOption2());
        options.add(currentQuestion.getOption3());

        // Перемешиваем варианты ответов
        Collections.shuffle(options);

        // Устанавливаем варианты ответов на кнопки
        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));
        btnOption4.setText(options.get(3));

        // Сбрасываем стили кнопок
        resetButtonStyles();

        // Настраиваем обработчики нажатий
        setupButtonClickListeners(currentQuestion);

        // Скрываем подсказку
        cardHint.setVisibility(View.GONE);
        btnUseHint.setVisibility(View.VISIBLE);

        // Запускаем таймер
        startTimer(currentQuiz.getTimePerQuestionInSeconds());
    }

    private void setupButtonClickListeners(Question question) {
        View.OnClickListener optionClickListener = v -> {
            // Игнорируем нажатия, если уже ответили на вопрос
            if (questionAnswered) {
                return;
            }

            // Останавливаем таймер
            if (timer != null) {
                timer.cancel();
            }

            // Отмечаем, что ответ дан
            questionAnswered = true;

            // Получаем текст выбранного варианта
            Button clickedButton = (Button) v;
            String selectedAnswer = clickedButton.getText().toString();

            // Проверяем правильность ответа
            boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());

            if (isCorrect) {
                // Правильный ответ
                correctAnswers++;

                // Добавляем очки в зависимости от сложности
                int points = calculatePoints(question.getDifficulty());
                totalPoints += points;

                // Анимация и звук для правильного ответа
                clickedButton.setBackgroundResource(R.drawable.button_correct);
                SoundUtils.playSound(getContext(), R.raw.correct_answer);
            } else {
                // Неправильный ответ
                clickedButton.setBackgroundResource(R.drawable.button_incorrect);

                // Показываем правильный ответ
                highlightCorrectAnswer(question);

                // Вибрация и звук для неправильного ответа
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(300);
                }
                SoundUtils.playSound(getContext(), R.raw.wrong_answer);
            }

            // Задержка перед переходом к следующему вопросу
            new android.os.Handler().postDelayed(() -> {
                if (currentQuestionIndex < questions.size() - 1) {
                    // Переходим к следующему вопросу
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                } else {
                    // Завершаем викторину
                    finishQuiz();
                }
            }, 1500);
        };

        // Назначаем обработчик для всех кнопок
        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);
    }

    private void resetButtonStyles() {
        btnOption1.setBackgroundResource(R.drawable.button_option);
        btnOption2.setBackgroundResource(R.drawable.button_option);
        btnOption3.setBackgroundResource(R.drawable.button_option);
        btnOption4.setBackgroundResource(R.drawable.button_option);
    }

    private void highlightCorrectAnswer(Question question) {
        String correctAnswer = question.getCorrectAnswer();

        if (btnOption1.getText().toString().equals(correctAnswer)) {
            btnOption1.setBackgroundResource(R.drawable.button_correct);
        } else if (btnOption2.getText().toString().equals(correctAnswer)) {
            btnOption2.setBackgroundResource(R.drawable.button_correct);
        } else if (btnOption3.getText().toString().equals(correctAnswer)) {
            btnOption3.setBackgroundResource(R.drawable.button_correct);
        } else if (btnOption4.getText().toString().equals(correctAnswer)) {
            btnOption4.setBackgroundResource(R.drawable.button_correct);
        }
    }

    private void startTimer(int seconds) {
        progressTimer.setMax(seconds * 1000);
        progressTimer.setProgress(seconds * 1000);

        timer = new CountDownTimer(seconds * 1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressTimer.setProgress((int) millisUntilFinished);
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));

                // Играем звук тиканья часов на последних 5 секундах
                if (millisUntilFinished <= 5000 && millisUntilFinished > 4900) {
                    SoundUtils.playSound(getContext(), R.raw.timer_tick);
                }
            }

            @Override
            public void onFinish() {
                // Время истекло, переходим к следующему вопросу
                if (!questionAnswered) {
                    progressTimer.setProgress(0);
                    tvTimer.setText("0");

                    // Показываем правильный ответ
                    highlightCorrectAnswer(questions.get(currentQuestionIndex));

                    // Вибрация
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.vibrate(300);
                    }

                    // Задержка перед переходом к следующему вопросу
                    new android.os.Handler().postDelayed(() -> {
                        if (currentQuestionIndex < questions.size() - 1) {
                            currentQuestionIndex++;
                            showQuestion(currentQuestionIndex);
                        } else {
                            finishQuiz();
                        }
                    }, 1500);
                }
            }
        }.start();
    }

    private void showHint() {
        Question currentQuestion = questions.get(currentQuestionIndex);

        // Получаем подсказку для текущего вопроса
        String hint = QuizDatabaseHelper.getInstance(getContext())
                .getHintForQuestion(currentQuestion.getId());

        if (hint != null && !hint.isEmpty()) {
            // Показываем подсказку
            tvHint.setText(hint);
            cardHint.setVisibility(View.VISIBLE);
            btnUseHint.setVisibility(View.GONE);

            // Анимация для появления подсказки
            AnimationUtils.fadeIn(cardHint);
        }
    }

    private int calculatePoints(int difficulty) {
        // Базовые очки за правильный ответ
        int basePoints = 10;

        // Умножаем на коэффициент сложности
        return basePoints * difficulty;
    }

    private void finishQuiz() {
        // Сохраняем результаты прохождения викторины, если пользователь авторизован
        if (currentAttempt != null) {
            // Рассчитываем процент правильных ответов
            float percentage = (float) correctAnswers / questions.size() * 100;

            // Заполняем данные о попытке
            currentAttempt.setEndTime(new Date());
            currentAttempt.setCorrectAnswers(correctAnswers);
            currentAttempt.setTotalQuestions(questions.size());
            currentAttempt.setTotalPoints(totalPoints);

            // Сохраняем попытку в базу данных
            QuizDatabaseHelper.getInstance(getContext()).saveQuizAttempt(currentAttempt);

            // Обновляем общее количество очков пользователя
            QuizDatabaseHelper.getInstance(getContext())
                    .updateUserPoints(currentAttempt.getUserId(), totalPoints);
        }

        // Создаем фрагмент результатов и передаем ему данные
        ResultFragment resultFragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt("correct_answers", correctAnswers);
        args.putInt("total_questions", questions.size());
        args.putInt("total_points", totalPoints);
        args.putLong("time_spent", System.currentTimeMillis() - quizStartTime);
        resultFragment.setArguments(args);

        // Переходим к фрагменту результатов
        ((MainActivity) requireActivity()).loadFragment(resultFragment, true);
    }
}