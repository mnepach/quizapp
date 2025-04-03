package com.example.quizapp.fragments;

import android.os.Bundle;
import android.util.Log;
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
    private QuizDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Инициализация UI элементов
        lvHistory = view.findViewById(R.id.lv_history);
        tvNoHistory = view.findViewById(R.id.tv_no_history);
        btnBackToMenu = view.findViewById(R.id.btn_back_to_menu);

        // Инициализация базы данных
        dbHelper = QuizDatabaseHelper.getInstance(getContext());

        // Настройка кнопки возврата в меню
        btnBackToMenu.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        });

        // Загрузка истории при создании
        loadHistory();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем историю при возвращении к фрагменту
        loadHistory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем базу данных при уничтожении фрагмента
        dbHelper.closeDatabase();
    }

    private void loadHistory() {
        long userId = SharedPreferencesManager.getInstance(getContext()).getCurrentUserId();

        if (userId != -1) {
            // Получаем историю прохождений из базы данных
            List<QuizAttempt> attempts = dbHelper.getQuizAttemptsForUser(userId);

            // Логируем количество попыток для диагностики
            Log.d("HistoryFragment", "Loaded " + attempts.size() + " attempts for userId: " + userId);

            if (!attempts.isEmpty()) {
                // Показываем историю
                QuizHistoryAdapter adapter = new QuizHistoryAdapter(getContext(), attempts);
                lvHistory.setAdapter(adapter);
                tvNoHistory.setVisibility(View.GONE);
                lvHistory.setVisibility(View.VISIBLE);
            } else {
                // Нет истории
                tvNoHistory.setText(R.string.no_history_available);
                tvNoHistory.setVisibility(View.VISIBLE);
                lvHistory.setVisibility(View.GONE);
            }
        } else {
            // Пользователь не авторизован
            tvNoHistory.setText(R.string.login_to_view_history);
            tvNoHistory.setVisibility(View.VISIBLE);
            lvHistory.setVisibility(View.GONE);
            Log.w("HistoryFragment", "User not logged in, userId: " + userId);
        }
    }
}