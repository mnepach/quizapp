package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SharedPreferencesManager;

public class SettingsFragment extends Fragment {

    private SharedPreferencesManager preferencesManager;
    private SeekBar seekBarVolume;
    private TextView tvVolumeValue;
    private CheckBox cbVibration;
    private CheckBox cbSoundEffects;
    private CheckBox cbAnimations;
    private Button btnApply;
    private Button btnReset;
    private Button btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Инициализация менеджера SharedPreferences
        preferencesManager = SharedPreferencesManager.getInstance(requireContext());

        // Инициализация UI элементов
        seekBarVolume = view.findViewById(R.id.seek_bar_volume);
        tvVolumeValue = view.findViewById(R.id.tv_volume_value);
        cbVibration = view.findViewById(R.id.cb_vibration);
        cbSoundEffects = view.findViewById(R.id.cb_sound_effects);
        cbAnimations = view.findViewById(R.id.cb_animations);
        btnApply = view.findViewById(R.id.btn_apply);
        btnReset = view.findViewById(R.id.btn_reset);
        btnBack = view.findViewById(R.id.btn_back);

        // Загружаем текущие настройки
        loadSettings();

        // Настраиваем обработчики для UI элементов
        setupListeners();

        return view;
    }

    private void loadSettings() {
        // Загружаем настройки из SharedPreferences
        int volume = preferencesManager.getVolume();
        boolean vibration = preferencesManager.isVibrationEnabled();
        boolean soundEffects = preferencesManager.isSoundEffectsEnabled();
        boolean animations = preferencesManager.isAnimationsEnabled();

        // Устанавливаем значения в UI
        seekBarVolume.setProgress(volume);
        tvVolumeValue.setText(String.valueOf(volume));
        cbVibration.setChecked(vibration);
        cbSoundEffects.setChecked(soundEffects);
        cbAnimations.setChecked(animations);
    }

    private void setupListeners() {
        // Обработчик изменения громкости
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVolumeValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Обработчик нажатия на кнопку "Применить"
        btnApply.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            saveSettings();
        });

        // Обработчик нажатия на кнопку "Сбросить"
        btnReset.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            resetSettings();
        });

        // Обработчик нажатия на кнопку "Назад"
        btnBack.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        });
    }

    private void saveSettings() {
        // Сохраняем настройки в SharedPreferences
        int volume = seekBarVolume.getProgress();
        boolean vibration = cbVibration.isChecked();
        boolean soundEffects = cbSoundEffects.isChecked();
        boolean animations = cbAnimations.isChecked();

        preferencesManager.setVolume(volume);
        preferencesManager.setVibrationEnabled(vibration);
        preferencesManager.setSoundEffectsEnabled(soundEffects);
        preferencesManager.setAnimationsEnabled(animations);

        // Показываем сообщение об успешном сохранении
        Toast.makeText(requireContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    private void resetSettings() {
        // Сбрасываем настройки на значения по умолчанию
        seekBarVolume.setProgress(100);
        tvVolumeValue.setText("100");
        cbVibration.setChecked(true);
        cbSoundEffects.setChecked(true);
        cbAnimations.setChecked(true);
    }
}