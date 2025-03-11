package com.example.quizapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.R;
import com.example.quizapp.models.User;

public class MenuFragment extends Fragment {
    private static final String ARG_USER = "user";

    private User currentUser;
    private int selectedDifficulty = 1; // По умолчанию легкий уровень

    private OnMenuInteractionListener listener;

    public interface OnMenuInteractionListener {
        void onStartQuizClicked(int difficultyLevel);
        void onLoginClicked();
        void onRegisterClicked();
        void onHistoryClicked();
        void onExitClicked();
    }

    public static MenuFragment newInstance(User user) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        TextView usernameText = view.findViewById(R.id.username_text);
        if (currentUser != null) {
            usernameText.setText("Привет, " + currentUser.getUsername() + "!");
            usernameText.setVisibility(View.VISIBLE);
        }

        Button startQuizButton = view.findViewById(R.id.btn_start_quiz);
        Button loginButton = view.findViewById(R.id.btn_login);
        Button registerButton = view.findViewById(R.id.btn_register);
        Button historyButton = view.findViewById(R.id.btn_history);
        Button exitButton = view.findViewById(R.id.btn_exit);

        RadioGroup difficultyGroup = view.findViewById(R.id.difficulty_group);
        RadioButton easyRadio = view.findViewById(R.id.rb_easy);
        RadioButton mediumRadio = view.findViewById(R.id.rb_medium);
        RadioButton hardRadio = view.findViewById(R.id.rb_hard);

        // Настройка видимости кнопок в зависимости от статуса пользователя
        if (currentUser != null) {
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            historyButton.setVisibility(View.VISIBLE);
        } else {
            historyButton.setVisibility(View.GONE);
        }

        difficultyGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_easy) {
                selectedDifficulty = 1;
            } else if (checkedId == R.id.rb_medium) {
                selectedDifficulty = 2;
            } else if (checkedId == R.id.rb_hard) {
                selectedDifficulty = 3;
            }
        });

        startQuizButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStartQuizClicked(selectedDifficulty);
            }
        });

        loginButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLoginClicked();
            }
        });

        registerButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRegisterClicked();
            }
        });

        historyButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryClicked();
            }
        });

        exitButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExitClicked();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMenuInteractionListener) {
            listener = (OnMenuInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnMenuInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}