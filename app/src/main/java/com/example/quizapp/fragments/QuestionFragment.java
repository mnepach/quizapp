package com.example.quizapp.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.models.User;
import com.example.quizapp.util.DatabaseAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private TextView questionTextView;
    private TextView questionNumberTextView;
    private TextView timerTextView;
    private Button[] answerButtons = new Button[4];
    private ImageButton hintButton;
    private ProgressBar timerProgressBar;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int difficulty = 1;
    private long totalTimeTaken = 0;
    private CountDownTimer timer;
    private long timeLeftInMillis;
    private MySQLHelper dbHelper = new MySQLHelper();

    private MediaPlayer correctSoundPlayer;
    private MediaPlayer incorrectSoundPlayer;
    private Vibrator vibrator;
    private Animation pulseAnimation;
    private Animation shakeAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        questionTextView = view.findViewById(R.id.question_text_view);
        questionNumberTextView = view.findViewById(R.id.question_number_text_view);
        timerTextView = view.findViewById(R.id.timer_text_view);
        timerProgressBar = view.findViewById(R.id.timer_progress_bar);
        hintButton = view.findViewById(R.id.hint_button);

        answerButtons[0] = view.findViewById(R.id.answer_button_1);
        answerButtons[1] = view.findViewById(R.id.answer_button_2);
        answerButtons[2] = view.findViewById(R.id.answer_button_3);
        answerButtons[3] = view.findViewById(R.id.answer_button_4);

        for (int i = 0; i < answerButtons.length; i++) {
            final int answerIndex = i;
            answerButtons[i].setOnClickListener(v -> checkAnswer(answerIndex));
        }

        hintButton.setOnClickListener(v -> showHint());

        // Инициализация звуков, вибрации и анимаций
        correctSoundPlayer = MediaPlayer.create(getContext(), R.raw.correct_sound);
        incorrectSoundPlayer = MediaPlayer.create(getContext(), R.raw.incorrect_sound);
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        pulseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
        shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);

        // Получение уровня сложности из аргументов
        if (getArguments() != null) {
            difficulty = getArguments().getInt("difficulty", 1);
        }

        // Загрузка вопросов из базы данных
        loadQuestions();

        return view;
    }

    private void loadQuestions() {
        new DatabaseAsyncTask<List<Question>>(
                () -> dbHelper.getQuestionsByDifficulty(difficulty),
                this::onQuestionsLoaded,
                this::onError
        ).execute();
    }

    private void onQuestionsLoaded(List<Question> loadedQuestions) {
        if (loadedQuestions != null && !loadedQuestions.isEmpty()) {
            questions = loadedQuestions;
            showQuestion(currentQuestionIndex);
        } else {
            Toast.makeText(getContext(), "Не удалось загрузить вопросы", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).replaceFragment(new MenuFragment(), false);
        }
    }

    private void onError(Exception e) {
        Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        ((MainActivity) requireActivity()).replaceFragment(new MenuFragment(), false);
    }

    private void showQuestion(int index) {
        if (index < questions.size()) {
            Question question = questions.get(index);

            questionTextView.setText(question.getQuestionText());
            questionNumberTextView.setText("Вопрос " + (index + 1) + " из " + questions.size());

            for (int i = 0; i < answerButtons.length; i++) {
                answerButtons[i].setText(question.getOptions()[i]);
                answerButtons[i].setBackgroundResource(R.drawable.answer_button_normal);
                answerButtons[i].setEnabled(true);
            }

            // Сброс и запуск таймера
            if (timer != null) {
                timer.cancel();
            }

            startTimer(question.getTimeInSeconds() * 1000);
        } else {
            finishQuiz();
        }
    }

    private void startTimer(int milliseconds) {
        timeLeftInMillis = milliseconds;
        timerProgressBar.setMax(milliseconds / 1000);

        timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                timerTextView.setText(secondsRemaining + " с");
                timerProgressBar.setProgress(secondsRemaining);
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                timerTextView.setText("0 с");
                timerProgressBar.setProgress(0);

                // Если время истекло, считаем ответ неверным
                showCorrectAnswer();

                // Переход к следующему вопросу через 2 секунды
                questionTextView.postDelayed(() -> {
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                }, 2000);
            }
        }.start();
    }

    private void checkAnswer(int selectedAnswerIndex) {
        if (timer != null) {
            timer.cancel();
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        boolean isCorrect = selectedAnswerIndex == currentQuestion.getCorrectAnswerIndex();

        // Обновление общего времени
        long timeTakenForQuestion = currentQuestion.getTimeInSeconds() * 1000 - timeLeftInMillis;
        totalTimeTaken += timeTakenForQuestion / 1000;

        // Визуальное выделение правильного/неправильного ответа
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setEnabled(false);

            if (i == currentQuestion.getCorrectAnswerIndex()) {
                answerButtons[i].setBackgroundResource(R.drawable.answer_button_correct);
                if (i == selectedAnswerIndex) {
                    answerButtons[i].startAnimation(pulseAnimation);
                }
            } else if (i == selectedAnswerIndex) {
                answerButtons[i].setBackgroundResource(R.drawable.answer_button_incorrect);
                answerButtons[i].startAnimation(shakeAnimation);
            }
        }

        // Звуковое и вибро сопровождение
        if (isCorrect) {
            correctSoundPlayer.start();
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            score++;
            Toast.makeText(getContext(), "Правильно!", Toast.LENGTH_SHORT).show();
        } else {
            incorrectSoundPlayer.start();
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 100, 100, 100}, -1));
            Toast.makeText(getContext(), "Неправильно! Правильный ответ: " +
                    currentQuestion.getOptions()[currentQuestion.getCorrectAnswerIndex()], Toast.LENGTH_SHORT).show();
        }

        // Переход к следующему вопросу через 2 секунды
        questionTextView.postDelayed(() -> {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }, 2000);
    }

    private void showCorrectAnswer() {
        Question currentQuestion = questions.get(currentQuestionIndex);

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setEnabled(false);

            if (i == currentQuestion.getCorrectAnswerIndex()) {
                answerButtons[i].setBackgroundResource(R.drawable.answer_button_correct);
            }
        }

        incorrectSoundPlayer.start();
        vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 100, 100, 100}, -1));
        Toast.makeText(getContext(), "Время истекло! Правильный ответ: " +
                currentQuestion.getOptions()[currentQuestion.getCorrectAnswerIndex()], Toast.LENGTH_SHORT).show();
    }

    private void showHint() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        String hintText = currentQuestion.getHint();

        if (hintText != null && !hintText.isEmpty()) {
            Toast.makeText(getContext(), "Подсказка: " + hintText, Toast.LENGTH_LONG).show();
            hintButton.setEnabled(false);
            hintButton.setAlpha(0.5f);
        } else {
            Toast.makeText(getContext(), "Для этого вопроса нет подсказки", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishQuiz() {
        User currentUser = ((MainActivity) requireActivity()).getCurrentUser();

        // Сохранение результата
        new DatabaseAsyncTask<QuizAttempt>(
                () -> dbHelper.saveQuizAttempt(
                        currentUser.getId(),
                        score,
                        questions.size(),
                        difficulty,
                        totalTimeTaken
                ),
                this::onQuizSaved,
                this::onError
        ).execute();
    }

    private void onQuizSaved(QuizAttempt attempt) {
        // Переход к экрану результатов
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        bundle.putInt("totalQuestions", questions.size());
        bundle.putInt("difficulty", difficulty);
        bundle.putLong("timeTaken", totalTimeTaken);

        ResultFragment resultFragment = new ResultFragment();
        resultFragment.setArguments(bundle);

        ((MainActivity) requireActivity()).replaceFragment(resultFragment, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (correctSoundPlayer != null) {
            correctSoundPlayer.release();
        }
        if (incorrectSoundPlayer != null) {
            incorrectSoundPlayer.release();
        }
    }
}