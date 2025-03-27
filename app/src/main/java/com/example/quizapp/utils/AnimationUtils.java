package com.example.quizapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AnimationUtils {

    public static void buttonClick(View view) {
        if (view == null) return;

        // Создаем анимацию нажатия кнопки (легкое сжатие)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f);

        scaleX.setDuration(100);
        scaleY.setDuration(100);

        scaleX.start();
        scaleY.start();
    }

    public static void animateCorrectAnswer(View view) {
        if (view == null) return;

        // Создаем анимацию масштабирования
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);

        // Создаем анимацию прозрачности
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.7f);
        alphaAnimation.setDuration(300);

        // Объединяем анимации
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setRepeatMode(Animation.REVERSE);
        animationSet.setRepeatCount(1);

        view.startAnimation(animationSet);
    }

    public static void animateWrongAnswer(View view) {
        if (view == null) return;

        // Создаем анимацию "дрожания"
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    public static void animateTimer(ProgressBar progressBar, int durationInSeconds) {
        if (progressBar == null) return;

        progressBar.setMax(durationInSeconds * 1000);
        progressBar.setProgress(durationInSeconds * 1000);

        ValueAnimator animator = ValueAnimator.ofInt(durationInSeconds * 1000, 0);
        animator.setDuration(durationInSeconds * 1000);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            progressBar.setProgress(value);
        });
        animator.start();
    }

    public static void fadeIn(View view) {
        if (view == null) return;

        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null)
                .start();
    }

    public static void fadeOut(View view) {
        if (view == null) return;

        view.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }
}