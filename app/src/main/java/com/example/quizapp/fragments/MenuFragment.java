// app/src/main/java/com/example/quizapp/fragments/MenuFragment.java
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
    private TextView tvPoints;
    private Button btnEasyQuiz;
    private Button btnMediumQuiz;
    private Button btnHardQuiz;
    private Button btnHistory;
    private Button btnSettings;
    private Button btnLogout;
    private QuizDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvPoints = view.findViewById(R.id.tv_points);
        btnEasyQuiz = view.findViewById(R.id.btn_easy_quiz);
        btnMediumQuiz = view.findViewById(R.id.btn_medium_quiz);
        btnHardQuiz = view.findViewById(R.id.btn_hard_quiz);
        btnHistory = view.findViewById(R.id.btn_history);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);
        dbHelper = new QuizDatabaseHelper(getContext());

        loadUserData();

        btnEasyQuiz.setOnClickListener(v -> startQuiz(1));
        btnMediumQuiz.setOnClickListener(v -> startQuiz(2));
        btnHardQuiz.setOnClickListener(v -> startQuiz(3));
        btnHistory.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new HistoryFragment()));
        btnSettings.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new SettingsFragment()));
        btnLogout.setOnClickListener(v -> logout());

        if (SharedPreferencesManager.getInstance(requireContext()).isAnimationEnabled()) {
            AnimationUtils.fadeIn(view, 500);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        long userId = SharedPreferencesManager.getInstance(requireContext()).getCurrentUserId();
        User user = dbHelper.getUserById(userId);

        if (user != null) {
            tvWelcome.setText("Добро пожаловать, " + user.getUsername() + "!");
            tvPoints.setText("Ваши очки: " + user.getTotalPoints());
        }
    }

    private void startQuiz(int difficulty) {
        Bundle args = new Bundle();
        args.putInt("difficulty", difficulty);

        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.setArguments(args);

        ((MainActivity) requireActivity()).loadFragment(questionFragment);
    }

    private void logout() {
        SharedPreferencesManager.getInstance(requireContext()).logout();
        ((MainActivity) requireActivity()).loadFragment(new LoginFragment(), false);
    }
}