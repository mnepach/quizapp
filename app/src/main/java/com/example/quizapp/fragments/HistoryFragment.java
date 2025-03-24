package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.adapters.QuizHistoryAdapter;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.utils.SharedPreferencesManager;

import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView lvHistory;
    private TextView tvNoHistory;
    private Button btnBackToMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Инициализация UI элементов
        lvHistory = view.findViewById(R.id.lv_history);
        tvNoHistory = view.findViewById(R.id.tv_no_history);
        btnBackToMenu = view.findViewById(R.id.btn_back_to_menu);

        // Настройка кнопки возврата в меню
        btnBackToMenu.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        });

        // Загрузка истории прохождений
        loadHistory();

        return view;
    }

    private void loadHistory() {
        // Получаем ID текущего пользователя
        long userId = SharedPreferencesManager.getLoggedInUserId();

        if (userId != -1) {
            // Получаем историю прохождений из базы данных
            List<QuizAttempt> attempts = QuizDatabaseHelper.getInstance(getContext())
                    .getQuizAttemptsForUser(userId);

            if (attempts != null && !attempts.isEmpty()) {
                // Показываем историю
                QuizHistoryAdapter adapter = new QuizHistoryAdapter(getContext(), attempts);
                lvHistory.setAdapter(adapter);
                tvNoHistory.setVisibility(View.GONE);
                lvHistory.setVisibility(View.VISIBLE);
            } else {
                // Нет истории
                tvNoHistory.setVisibility(View.VISIBLE);
                lvHistory.setVisibility(View.GONE);
            }
        } else {
            // Пользователь не авторизован
            tvNoHistory.setText(R.string.login_to_view_history);
            tvNoHistory.setVisibility(View.VISIBLE);
            lvHistory.setVisibility(View.GONE);
        }
    }
}