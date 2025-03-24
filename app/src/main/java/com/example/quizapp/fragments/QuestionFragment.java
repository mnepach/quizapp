// app/src/main/java/com/example/quizapp/fragments/QuestionFragment.java
package com.example.quizapp.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.Hint;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.example.quizapp.utils.SoundUtils;

import java.util.List;

public class QuestionFragment extends Fragment {

    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private Button btnOption1;
    private Button btnOption2;
    private Button btnOption3;
    private Button btnOption4;
    private Button btnHint;
    private TextView tvHint;
    private ProgressBar progressTimer;
    private TextView tvPointsInfo;

    private QuizDatabaseHelper dbHelper;
    private Quiz currentQuiz;
    private int currentQuestionIndex = 0;
    private Question currentQuestion;
    private QuizAttempt quizAttempt;
    private CountDownTimer timer;
    private boolean answered = false;
    private boolean hintUsed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        tvQuestionNumber = view.findViewById(R.id.tv_question_number);
        tvQuestion = view.findViewById(R.id.tv_question);
        btnOption1 = view.findViewById(R.id.btn_option_1);
        btnOption2 = view.findViewById(R.id.btn_option_2);
        btnOption3 = view.findViewById(R.id.btn_option_3);
        btnOption4 = view.findViewById(R.id.btn_option_4);
        btnHint = view.findViewById(R.id.btn_hint);
        tvHint = view.findViewById(R.id.tv_hint);
        progressTimer = view.findViewById(R.id.progress_timer);
        tvPointsInfo = view.findViewById(R.id.tv_points_info);

        dbHelper = new QuizDatabaseHelper(getContext());

        int difficulty = 1; // Default to easy
        if (getArguments() != null) {
            difficulty = getArguments().getInt("difficulty", 1);
        }

        // Load quiz questions for the selected difficulty
        currentQuiz = dbHelper.getQuizByDifficulty(difficulty);

        // Initialize quiz attempt
        long userId = SharedPreferencesManager.getInstance(requireContext()).getCurrentUserId();
        quizAttempt = new QuizAttempt(userId, currentQuiz.getId(), currentQuiz.getTitle(), currentQuiz.getDifficulty());
        quizAttempt.setTotalQuestions(currentQuiz.getTotalQuestions());

        // Set button click listeners
        btnOption1.setOnClickListener(v -> checkAnswer(0));
        btnOption2.setOnClickListener(v -> checkAnswer(1));
        btnOption3.setOnClickListener(v -> checkAnswer(2));
        btnOption4.setOnClickListener(v -> checkAnswer(3));
        btnHint.setOnClickListener(v -> showHint());

        // Load first question
        loadQuestion();

        return view;
    }

    private void loadQuestion() {
        if (currentQuestionIndex < currentQuiz.getQuestions().size()) {
            currentQuestion = currentQuiz.getQuestions().get(currentQuestionIndex);

            // Reset state for new question
            answered = false;
            hintUsed = false;
            resetButtonsAppearance();
            tvHint.setVisibility(View.GONE);

            // Update UI
            tvQuestionNumber.setText("Вопрос " + (currentQuestionIndex + 1) + " из " + currentQuiz.getTotalQuestions());
            tvQuestion.setText(currentQuestion.getQuestionText());

            String[] options = currentQuestion.getOptions();
            btnOption1.setText(options[0]);
            btnOption2.setText(options[1]);
            btnOption3.setText(options[2]);
            btnOption4.setText(options[3]);

            tvPointsInfo.setText("Очки за вопрос: " + currentQuestion.getPointsValue());

            // Start timer
            startTimer();

            // Animate question appearance if enabled
            if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
                AnimationUtils.fadeIn(tvQuestion, 300);
            }
        } else {
            // End of quiz, save attempt and show results
            long attemptId = dbHelper.insertQuizAttempt(quizAttempt);

            Bundle args = new Bundle();
            args.putLong("attempt_id", attemptId);

            ResultFragment resultFragment = new ResultFragment();
            resultFragment.setArguments(args);

            ((MainActivity) requireActivity()).loadFragment(resultFragment);
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        int timePerQuestion = currentQuiz.getTimePerQuestionInSeconds() * 1000; // Convert to milliseconds

        // Update progress bar
        if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
            AnimationUtils.animateTimer(progressTimer, currentQuiz.getTimePerQuestionInSeconds());
        } else {
            progressTimer.setMax(timePerQuestion);
            progressTimer.setProgress(timePerQuestion);
        }

        timer = new CountDownTimer(timePerQuestion, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished <= 5000 && SharedPreferencesManager.getInstance(requireContext()).isSoundEnabled()) {
                    SoundUtils.playTimerTickSound(requireContext());
                }

                if (!SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
                    progressTimer.setProgress((int) millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                if (!answered) {
                    // Time's up, move to next question
                    SoundUtils.vibrate(requireContext(), 500);
                    Toast.makeText(getContext(), "Время истекло!", Toast.LENGTH_SHORT).show();
                    nextQuestion();
                }
            }
        }.start();
    }

    private void checkAnswer(int selectedOptionIndex) {
        if (answered) return;

        answered = true;
        if (timer != null) {
            timer.cancel();
        }

        Button selectedButton = null;
        switch (selectedOptionIndex) {
            case 0: selectedButton = btnOption1; break;
            case 1: selectedButton = btnOption2; break;
            case 2: selectedButton = btnOption3; break;
            case 3: selectedButton = btnOption4; break;
        }

        Button correctButton = null;
        switch (currentQuestion.getCorrectOptionIndex()) {
            case 0: correctButton = btnOption1; break;
            case 1: correctButton = btnOption2; break;
            case 2: correctButton = btnOption3; break;
            case 3: correctButton = btnOption4; break;
        }

        boolean isCorrect = currentQuestion.isCorrectAnswer(selectedOptionIndex);

        if (isCorrect) {
            // Correct answer
            if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
                AnimationUtils.animateCorrectAnswer(selectedButton);
            }
            if (SharedPreferencesManager.getInstance(requireContext()).isSoundEnabled()) {
                SoundUtils.playCorrectAnswerSound(requireContext());
            }

            selectedButton.setBackgroundResource(R.drawable.button_correct);

            // Calculate points (if hint was used, reduce points)
            int pointsEarned = hintUsed ? (currentQuestion.getPointsValue() / 2) : currentQuestion.getPointsValue();
            quizAttempt.addToScore(pointsEarned);
            quizAttempt.incrementCorrectAnswers();

            Toast.makeText(getContext(), "Правильно! +" + pointsEarned + " очков", Toast.LENGTH_SHORT).show();
        } else {
            // Wrong answer
            if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
                AnimationUtils.animateWrongAnswer(selectedButton);
            }
            if (SharedPreferencesManager.getInstance(requireContext()).isSoundEnabled()) {
                SoundUtils.playWrongAnswerSound(requireContext());
            }
            if (SharedPreferencesManager.getInstance(requireContext()).isVibrationEnabled()) {
                SoundUtils.vibrate(requireContext(), 300);
            }

            selectedButton.setBackgroundResource(R.drawable.button_wrong);
            correctButton.setBackgroundResource(R.drawable.button_correct);

            Toast.makeText(getContext(), "Неправильно! Правильный ответ: " +
                    currentQuestion.getOptions()[currentQuestion.getCorrectOptionIndex()], Toast.LENGTH_SHORT).show();
        }

        // Wait a moment before moving to next question
        btnOption1.postDelayed(this::nextQuestion, 1500);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }

    private void showHint() {
        if (hintUsed) return;

        Hint hint = dbHelper.getHintForQuestion(currentQuestion.getId());

        if (hint != null) {
            hintUsed = true;
            quizAttempt.incrementHintsUsed();

            tvHint.setText(hint.getHintText());
            tvHint.setVisibility(View.VISIBLE);

            if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
                AnimationUtils.fadeIn(tvHint, 300);
            }

            SoundUtils.vibrate(requireContext(), 100);

            Toast.makeText(getContext(), "Подсказка использована! -" +
                    (currentQuestion.getPointsValue() / 2) + " очков за этот вопрос", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Для этого вопроса нет подсказки", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetButtonsAppearance() {
        btnOption1.setBackgroundResource(R.drawable.button_normal);
        btnOption2.setBackgroundResource(R.drawable.button_normal);
        btnOption3.setBackgroundResource(R.drawable.button_normal);
        btnOption4.setBackgroundResource(R.drawable.button_normal);
    }

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroyView();
    }
}