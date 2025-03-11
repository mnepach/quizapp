package com.example.quizapp.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.quizapp.R;
import com.example.quizapp.models.Question;

public class QuestionFragment extends Fragment {
    private static final String ARG_QUESTION = "question";
    private static final String ARG_QUESTION_NUMBER = "question_number";
    private static final String ARG_TOTAL_QUESTIONS = "total_questions";

    private Question question;
    private int questionNumber;
    private int totalQuestions;
    private CountDownTimer timer;
    private int timeRemaining;
    private boolean isAnswered = false;
    private OnQuestionInteractionListener listener;

    private TextView questionNumberText;
    private TextView timerText;
    private ProgressBar timerProgress;
    private TextView questionText;
    private RadioGroup answerGroup;
    private RadioButton[] optionButtons = new RadioButton[4];
    private TextView feedbackText;
    private Button hintButton;
    private Button submitButton;

    public interface OnQuestionInteractionListener {
        void onQuestionAnswered(boolean isCorrect, int timeRemaining);
    }

    public static QuestionFragment newInstance(Question question, int questionNumber, int totalQuestions) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_QUESTION_NUMBER, questionNumber);
        args.putInt(ARG_TOTAL_QUESTIONS, totalQuestions);
        fragment.setArguments(args);
        return fragment;
    }