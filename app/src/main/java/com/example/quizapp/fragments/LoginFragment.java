// app/src/main/java/com/example/quizapp/fragments/LoginFragment.java
package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.User;
import com.example.quizapp.utils.SharedPreferencesManager;

public class LoginFragment extends Fragment {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private QuizDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_to_register);
        dbHelper = new QuizDatabaseHelper(getContext());

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> ((MainActivity) requireActivity()).loadFragment(new RegistrationFragment()));

        return view;
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // Login successful
            SharedPreferencesManager.getInstance(requireContext()).setCurrentUserId(user.getId());
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        } else {
            // Login failed
            Toast.makeText(getContext(), "Неверное имя пользователя или пароль", Toast.LENGTH_SHORT).show();
        }
    }
}