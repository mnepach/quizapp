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

import com.example.quizapp.R;
import com.example.quizapp.models.User;
import com.example.quizapp.util.DatabaseAsyncTask;

public class RegisterFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button registerButton;
    private TextView loginTextView;
    private MySQLHelper dbHelper = new MySQLHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        emailEditText = view.findViewById(R.id.email_edit_text);
        registerButton = view.findViewById(R.id.register_button);
        loginTextView = view.findViewById(R.id.login_text_view);

        registerButton.setOnClickListener(v -> attemptRegister());
        loginTextView.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void attemptRegister() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        new DatabaseAsyncTask<User>(
                () -> dbHelper.registerUser(username, password, email),
                this::onRegisterSuccess,
                this::onRegisterError
        ).execute();
    }

    private void onRegisterSuccess(User user) {
        if (user != null) {
            Toast.makeText(getContext(), "Регистрация успешна! Теперь вы можете войти.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void onRegisterError(Exception e) {
        Toast.makeText(getContext(), "Ошибка при регистрации: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}