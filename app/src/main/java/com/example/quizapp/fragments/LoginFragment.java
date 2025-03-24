package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.models.User;
import com.example.quizapp.util.DatabaseAsyncTask;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private MySQLHelper dbHelper = new MySQLHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        loginButton = view.findViewById(R.id.login_button);
        registerTextView = view.findViewById(R.id.register_text_view);

        loginButton.setOnClickListener(v -> attemptLogin());
        registerTextView.setOnClickListener(v -> goToRegister());

        return view;
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        new DatabaseAsyncTask<User>(
                () -> dbHelper.loginUser(username, password),
                this::onLoginSuccess,
                this::onLoginError
        ).execute();
    }

    private void onLoginSuccess(User user) {
        if (user != null) {
            ((MainActivity) requireActivity()).setCurrentUser(user);
            ((MainActivity) requireActivity()).replaceFragment(new MenuFragment(), false);
            Toast.makeText(getContext(), "Добро пожаловать, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Неверное имя пользователя или пароль", Toast.LENGTH_SHORT).show();
        }
    }

    private void onLoginError(Exception e) {
        Toast.makeText(getContext(), "Ошибка при авторизации: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void goToRegister() {
        ((MainActivity) requireActivity()).replaceFragment(new RegisterFragment(), true);
    }
}