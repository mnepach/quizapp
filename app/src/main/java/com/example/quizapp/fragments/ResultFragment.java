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
import com.example.quizapp.utils.AnimationUtils;

import java.util.Locale;

public class ResultFragment extends Fragment {

    private static final long MAX_QUIZ_DURATION_MS = 24 * 60 * 60 * 1000; // 24 часа в миллисекундах
    private static final long ANIMATION_DELAY_MS = 300; // Задержка между анимациями

    private TextView tvResultTitle;
    private TextView tvCorrectAnswers;
    private TextView tvScore;
    private TextView tvTimeSpent;
    private Button btnBackToMenu;
    private Button btnViewHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        // Инициализация UI элементов
        tvResultTitle = view.findViewById(R.id.tv_result_title);
        tvCorrectAnswers = view.findViewById(R.id.tv_correct_answers);
        tvScore = view.findViewById(R.id.tv_score);
        tvTimeSpent = view.findViewById(R.id.tv_time_spent);
        btnBackToMenu = view.findViewById(R.id.btn_back_to_menu);
        btnViewHistory = view.findViewById(R.id.btn_view_history);

        // Настройка обработчиков нажатий
        btnBackToMenu.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        });

        btnViewHistory.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            ((MainActivity) requireActivity()).loadFragment(new HistoryFragment(), true);
        });

        // Отображение результатов
        if (getArguments() != null) {
            displayResults();
        }

        return view;
    }

    private void displayResults() {
        int correctAnswers = getArguments().getInt("correct_answers");
        int totalQuestions = getArguments().getInt("total_questions");
        int totalPoints = getArguments().getInt("total_points");
        long timeSpent = getArguments().getLong("time_spent");

        // Проверяем корректность времени
        if (timeSpent < 0 || timeSpent > MAX_QUIZ_DURATION_MS) {
            timeSpent = 0; // Сбрасываем на 0, если время некорректное
        }

        // Рассчитываем процент правильных ответов
        int percentage = totalQuestions > 0 ? (int) (((float) correctAnswers / totalQuestions) * 100) : 0;

        // Устанавливаем заголовок в зависимости от результата
        if (percentage >= 80) {
            tvResultTitle.setText(R.string.result_excellent);
        } else if (percentage >= 60) {
            tvResultTitle.setText(R.string.result_good);
        } else if (percentage >= 40) {
            tvResultTitle.setText(R.string.result_average);
        } else {
            tvResultTitle.setText(R.string.result_poor);
        }

        // Устанавливаем информацию о правильных ответах
        tvCorrectAnswers.setText(getString(R.string.correct_answers, correctAnswers, totalQuestions, percentage));

        // Устанавливаем информацию об очках
        tvScore.setText(getString(R.string.total_score, totalPoints));

        // Форматируем время в минуты и секунды
        long minutes = timeSpent / (60 * 1000);
        long seconds = (timeSpent % (60 * 1000)) / 1000;
        String timeText = String.format(Locale.getDefault(), "%d мин %d сек", minutes, seconds);
        tvTimeSpent.setText(timeText);

        // Анимируем появление текста
        AnimationUtils.fadeIn(tvResultTitle);
        new android.os.Handler().postDelayed(() -> AnimationUtils.fadeIn(tvCorrectAnswers), ANIMATION_DELAY_MS);
        new android.os.Handler().postDelayed(() -> AnimationUtils.fadeIn(tvScore), ANIMATION_DELAY_MS * 2);
        new android.os.Handler().postDelayed(() -> AnimationUtils.fadeIn(tvTimeSpent), ANIMATION_DELAY_MS * 3);
    }
}