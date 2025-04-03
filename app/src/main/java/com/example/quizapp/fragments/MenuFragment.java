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
import com.example.quizapp.models.User;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SharedPreferencesManager;

public class MenuFragment extends Fragment {

    private TextView tvWelcome;
    private Button btnStartQuiz;
    private Button btnViewHistory;
    private Button btnSettings;
    private Button btnLogin;
    private TextView tvPoints;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Надуваем layout фрагмента
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Инициализация элементов UI
        tvWelcome = view.findViewById(R.id.tv_welcome);
        btnStartQuiz = view.findViewById(R.id.btn_start_quiz);
        btnViewHistory = view.findViewById(R.id.btn_view_history);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogin = view.findViewById(R.id.btn_login);
        tvPoints = view.findViewById(R.id.tv_points);

        // Проверяем, авторизован ли пользователь
        updateUI();

        // Настраиваем обработчики нажатий
        setupClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем UI при возвращении на фрагмент
        updateUI();
    }

    private void updateUI() {
        // Проверяем, авторизован ли пользователь
        SharedPreferencesManager prefsManager = SharedPreferencesManager.getInstance(getContext());
        long userId = prefsManager.getCurrentUserId();
        if (userId != -1) {
            // Пользователь авторизован
            User user = QuizDatabaseHelper.getInstance(getContext()).getUser(userId);
            if (user != null) {
                tvWelcome.setText(getString(R.string.welcome_user, user.getUsername()));
                tvPoints.setText(getString(R.string.total_points, user.getTotalPoints()));
                tvPoints.setVisibility(View.VISIBLE);
                btnLogin.setText(R.string.logout);
                btnViewHistory.setEnabled(true);
            }
        } else {
            // Пользователь не авторизован
            tvWelcome.setText(R.string.welcome_guest);
            tvPoints.setVisibility(View.GONE);
            btnLogin.setText(R.string.login_register);
            btnViewHistory.setEnabled(false);
        }
    }

    private void setupClickListeners() {
        // Кнопка "Начать викторину"
        btnStartQuiz.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v); // Анимация нажатия
            ((MainActivity) requireActivity()).loadFragment(new QuizSelectionFragment(), true);
        });

        // Кнопка "История"
        btnViewHistory.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            ((MainActivity) requireActivity()).loadFragment(new HistoryFragment(), true);
        });

        // Кнопка "Настройки"
        btnSettings.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            ((MainActivity) requireActivity()).loadFragment(new SettingsFragment(), true);
        });

        // Кнопка "Войти/Выйти"
        btnLogin.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            SharedPreferencesManager prefsManager = SharedPreferencesManager.getInstance(getContext());
            if (prefsManager.getCurrentUserId() != -1) {
                // Пользователь авторизован - выполняем выход
                prefsManager.logout();
                updateUI();
            } else {
                // Пользователь не авторизован - переходим на экран входа
                ((MainActivity) requireActivity()).loadFragment(new RegistrationFragment(), true);
            }
        });
    }
}