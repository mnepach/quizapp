package com.example.quizapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREFS_NAME = "QuizAppPrefs";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_VIBRATION_ENABLED = "vibration_enabled";
    private static final String KEY_ANIMATION_ENABLED = "animation_enabled";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";

    private SharedPreferences sharedPreferences;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    // Методы для работы с volume
    public int getVolume() {
        return sharedPreferences.getInt(KEY_VOLUME, 100);
    }

    public void setVolume(int volume) {
        sharedPreferences.edit().putInt(KEY_VOLUME, volume).apply();
    }

    // Методы для вибрации
    public boolean isVibrationEnabled() {
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true);
    }

    public void setVibrationEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply();
    }

    // Методы для звуковых эффектов
    public boolean isSoundEffectsEnabled() {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEffectsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    // Методы для анимаций
    public boolean isAnimationsEnabled() {
        return sharedPreferences.getBoolean(KEY_ANIMATION_ENABLED, true);
    }

    public void setAnimationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_ANIMATION_ENABLED, enabled).apply();
    }

    // Методы управления пользователем
    public void setCurrentUserId(long userId) {
        sharedPreferences.edit().putLong(KEY_CURRENT_USER_ID, userId).apply();
    }

    public long getCurrentUserId() {
        return sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1);
    }

    public boolean isUserLoggedIn() {
        return getCurrentUserId() != -1;
    }

    public void logout() {
        setCurrentUserId(-1);
    }
}