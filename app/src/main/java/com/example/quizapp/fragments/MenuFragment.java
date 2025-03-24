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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.adapters.DifficultyAdapter;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.models.User;
import com.example.quizapp.util.DatabaseAsyncTask;

import java.util.List;

public class MenuFragment extends Fragment {

    private Button startQuizButton;
    private Button historyButton;
    private Button logoutButton;
    private TextView welcomeTextView;
    private RecyclerView difficultyRecyclerView;
    private DifficultyAdapter difficultyAdapter;
    private MySQLHelper dbHelper = new MySQLHelper();
    private int selectedDifficulty = 1; // По умолчанию легкий уровень

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        startQuizButton = view.findViewById(R.id.start_quiz_button);
        historyButton = view.findViewById(R.id.history_button);
        logoutButton = view.findViewById(R.id.logout_button);
        welcomeTextView = view.findViewById(R.id.welcome_text_view);
        difficultyRecyclerView = view.findViewById(R.id.difficulty_recycler_view);

        User currentUser = ((MainActivity) requireActivity()).getCurrentUser();
        if (currentUser != null) {
            welcomeTextView.setText("Привет, " + currentUser.getUsername() + "!");
        }

        // Настройка RecyclerView для выбора сложности
        difficultyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        difficultyAdapter = new DifficultyAdapter();
        difficultyAdapter.setOnDifficultySelectedListener(difficulty -> {
            selectedDifficulty = difficulty;
        });
        difficultyRecyclerView.setAdapter(difficultyAdapter);

        startQuizButton.setOnClickListener(v -> startQuiz());
        historyButton.setOnClickListener(v -> showHistory());
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void startQuiz() {
        Bundle bundle = new Bundle();
        bundle.putInt("difficulty", selectedDifficulty);

        QuizFragment quizFragment = new QuizFragment();
        quizFragment.setArguments(bundle);

        ((MainActivity) requireActivity()).replaceFragment(quizFragment, true);
    }

    private void showHistory() {
        User currentUser = ((MainActivity) requireActivity()).getCurrentUser();
        if (currentUser != null) {
            new DatabaseAsyncTask<List<QuizAttempt>>(
                    () -> dbHelper.getUserAttempts(currentUser.getId()),
                    this::onHistoryLoaded,
                    e -> { /* Обработка ошибки */ }
            ).execute();
        }
    }

    private void onHistoryLoaded(List<QuizAttempt> attempts) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", ((MainActivity) requireActivity()).getCurrentUser().getId());

        HistoryFragment historyFragment = new HistoryFragment();
        historyFragment.setArguments(bundle);

        ((MainActivity) requireActivity()).replaceFragment(historyFragment, true);
    }

    private void logout() {
        ((MainActivity) requireActivity()).setCurrentUser(null);
        ((MainActivity) requireActivity()).replaceFragment(new LoginFragment(), false);
    }
}