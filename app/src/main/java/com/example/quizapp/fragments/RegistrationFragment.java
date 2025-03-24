package com.example.quizapp.fragments;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.User;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RegistrationFragment extends Fragment {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSubmit;
    private TextView tvToggleMode;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private boolean isLoginMode = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Инициализация UI элементов
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnSubmit = view.findViewById(R.id.btn_submit);
        tvToggleMode = view.findViewById(R.id.tv_toggle_mode);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Настройка TabLayout и ViewPager
        setupViewPager();

        // Настройка обработчиков нажатий
        btnSubmit.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            if (validateInput()) {
                if (isLoginMode) {
                    login();
                } else {
                    register();
                }
            }
        });

        // Переключение между режимами входа и регистрации
        tvToggleMode.setOnClickListener(v -> {
            AnimationUtils.fadeIn(v);
            toggleMode();
        });

        return view;
    }

    private void setupViewPager() {
        // Создаем адаптер для ViewPager
        AuthAdapter adapter = new AuthAdapter(this);
        viewPager.setAdapter(adapter);

        // Связываем ViewPager с TabLayout
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.login);
            } else {
                tab.setText(R.string.register);
            }
        });
        mediator.attach();

        // Обработка переключения вкладок
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                isLoginMode = position == 0;
                updateUI();
            }
        });
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        viewPager.setCurrentItem(isLoginMode ? 0 : 1);
        updateUI();
    }

    private void updateUI() {
        if (isLoginMode) {
            btnSubmit.setText(R.string.login);
            tvToggleMode.setText(R.string.no_account);
        } else {
            btnSubmit.setText(R.string.register);
            tvToggleMode.setText(R.string.already_have_account);
        }
    }

    private boolean validateInput() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_empty_username));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_empty_password));
            return false;
        }

        if (!isLoginMode && password.length() < 6) {
            etPassword.setError(getString(R.string.error_short_password));
            return false;
        }

        return true;
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверяем учетные данные
        User user = QuizDatabaseHelper.getInstance(getContext()).getUserByCredentials(username, password);

        if (user != null) {
            // Авторизация успешна
            SharedPreferencesManager.setLoggedInUserId(user.getId());
            Toast.makeText(getContext(), getString(R.string.login_success, username), Toast.LENGTH_SHORT).show();

            // Возвращаемся на главный экран
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        } else {
            // Ошибка авторизации
            Toast.makeText(getContext(), R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
        }
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Проверяем, существует ли пользователь с таким именем
        if (QuizDatabaseHelper.getInstance(getContext()).usernameExists(username)) {
            etUsername.setError(getString(R.string.error_username_exists));
            return;
        }

        // Создаем нового пользователя
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setTotalPoints(0);

        // Сохраняем пользователя в базу данных
        long userId = QuizDatabaseHelper.getInstance(getContext()).saveUser(newUser);

        if (userId != -1) {
            // Регистрация успешна
            SharedPreferencesManager.setLoggedInUserId(userId);
            Toast.makeText(getContext(), R.string.register_success, Toast.LENGTH_SHORT).show();

            // Возвращаемся на главный экран
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        } else {
            // Ошибка регистрации
            Toast.makeText(getContext(), R.string.error_registration, Toast.LENGTH_SHORT).show();
        }
    }

    // Адаптер для ViewPager с вкладками входа и регистрации
    private static class AuthAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        public AuthAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Создаем фрагмент для вкладки
            if (position == 0) {
                return new LoginTabFragment();
            } else {
                return new RegisterTabFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Две вкладки: вход и регистрация
        }
    }

    // Фрагмент для вкладки входа
    public static class LoginTabFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_login, container, false);
        }
    }

    // Фрагмент для вкладки регистрации
    public static class RegisterTabFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_register, container, false);
        }
    }
}