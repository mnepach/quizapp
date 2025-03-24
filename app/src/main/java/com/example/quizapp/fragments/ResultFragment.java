// app/src/main/java/com/example/quizapp/fragments/ResultFragment.java
package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.models.User;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SharedPreferencesManager;

public class ResultFragment extends Fragment {

    private TextView tvQuizTitle;
    private TextView tvScore;
    private TextView tvCorrectAnswers;
    private TextView tvHintsUsed;
    private TextView tvPerformance;
    private TextView tvTotalPoints;
    private Button btnBackToMenu;
    private Button btnRetryQuiz;

    private QuizDatabaseHelper dbHelper;
    private QuizAttempt quizAttempt;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        tvQuizTitle = view.findViewById(R.id.tv_result_quiz_title);
        tvScore = view.findViewById(R.id.tv_result_score);
        tvCorrectAnswers = view.findViewById(R.id.tv_result_correct_answers);
        tvHintsUsed = view.findViewById(R.id.tv_result_hints_used);
        tvPerformance = view.findViewById(R.id.tv_result_performance);
        tvTotalPoints = view.findViewById(R.id.tv_result_total_points);
        btnBackToMenu = view.findViewById(R.id.btn_back_to_menu);
        btnRetryQuiz = view.findViewById(R.id.btn_retry_quiz);

        dbHelper = new QuizDatabaseHelper(getContext());

        if (getArguments() != null) {
            long attemptId = getArguments().getLong("attempt_id", -1);
            if (attemptId != -1) {
                quizAttempt = dbHelper.getQuizAttemptById(attemptId);
                loadResultData();
            }
        }

        btnBackToMenu.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false));
        btnRetryQuiz.setOnClickListener(v -> retryQuiz());

        if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
            AnimationUtils.fadeIn(view, 500);
        }

        return view;
    }

    private void loadResultData() {
        if (quizAttempt != null) {
            tvQuizTitle.setText(quizAttempt.getQuizTitle());
            tvScore.setText("Очки: " + quizAttempt.getScore());
            tvCorrectAnswers.setText("Правильных ответов: " + quizAttempt.getCorrectAnswers() +
                    " из " + quizAttempt.getTotalQuestions());
            tvHintsUsed.setText("Использовано подсказок: " + quizAttempt.getHintsUsed());
            tvPerformance.setText("Производительность: " + quizAttempt.getPerformancePercentage());

            // Update user's total points
            long userId = SharedPreferencesManager.getInstance(requireContext()).getCurrentUserId();
            currentUser = dbHelper.getUserById(userId);

            if (currentUser != null) {
                // Add quiz score to user's total points
                currentUser.addPoints(quizAttempt.getScore());
                dbHelper.updateUser(currentUser);

                tvTotalPoints.setText("Всего очков: " + currentUser.getTotalPoints());
            }
        }
    }

    private void retryQuiz() {
        Bundle args = new Bundle();
        args.putInt("difficulty", quizAttempt.getDifficulty());

        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.setArguments(args);

        ((MainActivity) requireActivity()).loadFragment(questionFragment);
    }
}