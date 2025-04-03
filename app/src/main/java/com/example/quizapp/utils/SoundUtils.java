package com.example.quizapp.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.quizapp.R;

public class SoundUtils {
    private static MediaPlayer mediaPlayer;

    public static void playCorrectAnswerSound(Context context) {
        if (SharedPreferencesManager.getInstance(context).isSoundEffectsEnabled()) {
            releaseMediaPlayer();
            mediaPlayer = MediaPlayer.create(context, R.raw.correct_answer);
            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
            mediaPlayer.start();
        }
    }

    public static void playWrongAnswerSound(Context context) {
        if (SharedPreferencesManager.getInstance(context).isSoundEffectsEnabled()) {
            releaseMediaPlayer();
            mediaPlayer = MediaPlayer.create(context, R.raw.wrong_answer);
            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
            mediaPlayer.start();
        }
    }

    public static void playTimerTickSound(Context context) {
        if (SharedPreferencesManager.getInstance(context).isSoundEffectsEnabled()) {
            releaseMediaPlayer();
            mediaPlayer = MediaPlayer.create(context, R.raw.timer_tick);
            mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
            mediaPlayer.start();
        }
    }

    private static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void vibrate(Context context, long milliseconds) {
        if (SharedPreferencesManager.getInstance(context).isVibrationEnabled()) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    // Для старых API
                    vibrator.vibrate(milliseconds);
                }
            }
        }
    }
}