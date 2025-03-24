package com.example.quizapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizapp.fragments.MenuFragment;
import com.example.quizapp.utils.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация SharedPreferencesManager
        SharedPreferencesManager.init(this);

        // Если это первый запуск активности, открываем начальный фрагмент
        if (savedInstanceState == null) {
            loadFragment(new MenuFragment(), false);
        }
    }

    /**
     * Метод для загрузки фрагментов
     * @param fragment фрагмент, который нужно загрузить
     * @param addToBackStack нужно ли добавлять в backstack
     */
    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Анимация перехода между фрагментами
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /**
     * Метод для возврата к предыдущему фрагменту (если есть)
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}