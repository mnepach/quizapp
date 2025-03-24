package com.example.quizapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREFS_NAME = "QuizAppPrefs";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_VIBRATION_ENABLED = "vibration_enabled";
    private static final String KEY_ANIMATION_ENABLED = "animation_enabled";

    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setCurrentUserId(long userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_CURRENT_USER_ID, userId);
        editor.apply();
    }

    public long getCurrentUserId() {
        return sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1);
    }

    public boolean isUserLoggedIn() {
        return getCurrentUserId() != -1;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_CURRENT_USER_ID, -1);
        editor.apply();
    }

    public void setSoundEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SOUND_ENABLED, enabled);
        editor.apply();
    }

    public boolean isSoundEnabled() {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setVibrationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_VIBRATION_ENABLED, enabled);
        editor.apply();
    }

    public boolean isVibrationEnabled() {
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true);
    }

    public void setAnimationEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ANIMATION_ENABLED, enabled);
        editor.apply();
    }

    public boolean isAnimationEnabled() {
        return sharedPreferences.getBoolean(KEY_ANIMATION_ENABLED, true);
    }
}