// app/src/main/java/com/example/quizapp/fragments/RegistrationFragment.java
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

public class RegistrationFragment extends Fragment {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private Button btnToLogin;
    private QuizDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        etUsername = view.findViewById(R.id.et_reg_username);
        etPassword = view.findViewById(R.id.et_reg_password);
        etConfirmPassword = view.findViewById(R.id.et_reg_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        btnToLogin = view.findViewById(R.id.btn_to_login);
        dbHelper = new QuizDatabaseHelper(getContext());

        btnRegister.setOnClickListener(v -> attemptRegistration());
        btnToLogin.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void attemptRegistration() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.getUserByUsername(username) != null) {
            Toast.makeText(getContext(), "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(username, password);
        long userId = dbHelper.insertUser(newUser);

        if (userId > 0) {
            SharedPreferencesManager.getInstance(requireContext()).setCurrentUserId(userId);
            Toast.makeText(getContext(), "Регистрация успешна", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        } else {
            Toast.makeText(getContext(), "Ошибка при регистрации", Toast.LENGTH_SHORT).show();
        }
    }
}