package com.example.quizapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.Question;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.utils.AnimationUtils;
import com.example.quizapp.utils.SoundUtils;
import com.example.quizapp.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QuestionFragment extends Fragment {

    private static final long MAX_QUIZ_DURATION_MS = 24 * 60 * 60 * 1000;
    private static final long DELAY_BEFORE_NEXT_QUESTION_MS = 1500;

    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private Button btnOption1;
    private Button btnOption2;
    private Button btnOption3;
    private Button btnOption4;
    private ProgressBar progressTimer;
    private TextView tvTimer;
    private ImageView ivHint;
    private TextView tvHint;
    private CardView cardHint;
    private Button btnUseHint; // 50/50
    private Button btnShareFriend; // Отправить другу

    private Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalPoints = 0;
    private QuizAttempt currentAttempt;
    private CountDownTimer timer;
    private boolean questionAnswered = false;
    private long quizStartTime;
    private boolean hint50Used = false;
    private boolean shareFriendUsed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizStartTime = System.currentTimeMillis();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        tvQuestionNumber = view.findViewById(R.id.tv_question_number);
        tvQuestion = view.findViewById(R.id.tv_question);
        btnOption1 = view.findViewById(R.id.btn_option_1);
        btnOption2 = view.findViewById(R.id.btn_option_2);
        btnOption3 = view.findViewById(R.id.btn_option_3);
        btnOption4 = view.findViewById(R.id.btn_option_4);
        progressTimer = view.findViewById(R.id.progress_timer);
        tvTimer = view.findViewById(R.id.tv_timer);
        ivHint = view.findViewById(R.id.iv_hint);
        tvHint = view.findViewById(R.id.tv_hint);
        cardHint = view.findViewById(R.id.card_hint);
        btnUseHint = view.findViewById(R.id.btn_use_hint);
        btnShareFriend = view.findViewById(R.id.btn_audience_help);

        if (getArguments() != null) {
            long quizId = getArguments().getLong("quiz_id");
            loadQuiz(quizId);
        }

        btnUseHint.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            showHintConfirmationDialog("50/50");
        });

        btnShareFriend.setOnClickListener(v -> {
            AnimationUtils.buttonClick(v);
            showHintConfirmationDialog("share");
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void showHintConfirmationDialog(String hintType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Подтверждение");
        builder.setMessage("Вы точно хотите использовать подсказку?");

        builder.setPositiveButton("Да", (dialog, which) -> {
            if ("50/50".equals(hintType)) {
                useFiftyFifty();
            } else if ("share".equals(hintType)) {
                shareWithFriend();
            }
        });

        builder.setNegativeButton("Нет", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadQuiz(long quizId) {
        QuizDatabaseHelper dbHelper = QuizDatabaseHelper.getInstance(getContext());
        currentQuiz = dbHelper.getQuiz(quizId);

        if (currentQuiz != null) {
            questions = dbHelper.getQuestionsForQuiz(quizId);
            Collections.shuffle(questions);

            long userId = SharedPreferencesManager.getInstance(getContext()).getCurrentUserId();
            if (userId != -1) {
                currentAttempt = new QuizAttempt();
                currentAttempt.setUserId(userId);
                currentAttempt.setQuizId(quizId);
                currentAttempt.setQuizTitle(currentQuiz.getTitle());
                currentAttempt.setDifficulty(currentQuiz.getDifficulty());
                currentAttempt.setAttemptDate(new Date());
                currentAttempt.setStartTime(new Date(quizStartTime));
            }

            hint50Used = false;
            shareFriendUsed = false;

            showQuestion(0);
        } else {
            Toast.makeText(getContext(), "Не удалось загрузить викторину", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).loadFragment(new MenuFragment(), false);
        }
    }

    private void showQuestion(int index) {
        questionAnswered = false;
        if (timer != null) {
            timer.cancel();
        }

        Question currentQuestion = questions.get(index);
        String questionNumberText = "Question " + (index + 1) + " of " + questions.size();
        tvQuestionNumber.setText(questionNumberText);
        tvQuestion.setText(currentQuestion.getQuestionText());

        String[] options = currentQuestion.getOptions();
        btnOption1.setText(options[0]);
        btnOption2.setText(options[1]);
        btnOption3.setText(options[2]);
        btnOption4.setText(options[3]);

        resetButtonStyles();
        setupButtonClickListeners(currentQuestion);
        cardHint.setVisibility(View.GONE);

        btnUseHint.setVisibility(hint50Used ? View.GONE : View.VISIBLE);
        btnShareFriend.setVisibility(shareFriendUsed ? View.GONE : View.VISIBLE);

        startTimer(currentQuiz.getTimePerQuestionInSeconds());
    }

    private void setupButtonClickListeners(Question question) {
        View.OnClickListener optionClickListener = v -> {
            if (questionAnswered) {
                return;
            }

            if (timer != null) {
                timer.cancel();
            }

            questionAnswered = true;
            Button clickedButton = (Button) v;
            int selectedOptionIndex = -1;

            if (clickedButton == btnOption1) selectedOptionIndex = 0;
            else if (clickedButton == btnOption2) selectedOptionIndex = 1;
            else if (clickedButton == btnOption3) selectedOptionIndex = 2;
            else if (clickedButton == btnOption4) selectedOptionIndex = 3;

            boolean isCorrect = question.isCorrectAnswer(selectedOptionIndex);

            if (isCorrect) {
                correctAnswers++;
                int points = calculatePoints(question.getDifficulty());
                totalPoints += points;

                if (currentAttempt != null) {
                    currentAttempt.incrementCorrectAnswers();
                    currentAttempt.addToScore(points);
                }

                clickedButton.setBackgroundResource(R.drawable.button_correct);
                SoundUtils.playCorrectAnswerSound(getContext());
            } else {
                clickedButton.setBackgroundResource(R.drawable.button_incorrect);
                highlightCorrectAnswer(question);
                SoundUtils.vibrate(getContext(), 300);
                SoundUtils.playWrongAnswerSound(getContext());
            }

            new android.os.Handler().postDelayed(() -> {
                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                } else {
                    finishQuiz();
                }
            }, DELAY_BEFORE_NEXT_QUESTION_MS);
        };

        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);
    }

    private void startTimer(int seconds) {
        final int totalMillis = seconds * 1000;
        progressTimer.setMax(totalMillis);
        progressTimer.setProgress(totalMillis);

        timer = new CountDownTimer(totalMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressTimer.setProgress((int) millisUntilFinished);
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
                if (millisUntilFinished <= 5000 && millisUntilFinished > 4900) {
                    SoundUtils.playTimerTickSound(getContext());
                }
            }

            @Override
            public void onFinish() {
                if (!questionAnswered) {
                    questionAnswered = true;
                    progressTimer.setProgress(0);
                    tvTimer.setText("0");

                    Toast.makeText(getContext(), "Время истекло! Выбран неправильный ответ.", Toast.LENGTH_SHORT).show();
                    Question currentQuestion = questions.get(currentQuestionIndex);
                    highlightCorrectAnswer(currentQuestion);
                    SoundUtils.vibrate(getContext(), 300);
                    SoundUtils.playWrongAnswerSound(getContext());

                    new android.os.Handler().postDelayed(() -> {
                        if (currentQuestionIndex < questions.size() - 1) {
                            currentQuestionIndex++;
                            showQuestion(currentQuestionIndex);
                        } else {
                            finishQuiz();
                        }
                    }, DELAY_BEFORE_NEXT_QUESTION_MS);
                }
            }
        }.start();
    }

    private void useFiftyFifty() {
        if (hint50Used) {
            return;
        }

        hint50Used = true;
        btnUseHint.setVisibility(View.GONE);
        if (currentAttempt != null) {
            currentAttempt.incrementHintsUsed();
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        int correctIndex = currentQuestion.getCorrectOptionIndex();
        List<Integer> incorrectIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correctIndex) {
                incorrectIndices.add(i);
            }
        }

        Collections.shuffle(incorrectIndices);
        for (int i = 0; i < 2; i++) {
            Button button = getButtonByIndex(incorrectIndices.get(i));
            button.setEnabled(false);
            button.setAlpha(0.3f);
        }
    }

    private void shareWithFriend() {
        if (shareFriendUsed) {
            return;
        }

        shareFriendUsed = true;
        btnShareFriend.setVisibility(View.GONE);
        if (currentAttempt != null) {
            currentAttempt.incrementHintsUsed();
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        String questionText = currentQuestion.getQuestionText();
        String[] options = currentQuestion.getOptions();
        String shareText = "Помоги мне с вопросом из викторины!\n\n" +
                "Вопрос: " + questionText + "\n" +
                "Варианты:\n" +
                "1. " + options[0] + "\n" +
                "2. " + options[1] + "\n" +
                "3. " + options[2] + "\n" +
                "4. " + options[3];

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Вопрос из викторины");
        startActivity(Intent.createChooser(shareIntent, "Отправить другу через..."));
    }

    private void resetButtonStyles() {
        btnOption1.setBackgroundResource(R.drawable.button_option);
        btnOption2.setBackgroundResource(R.drawable.button_option);
        btnOption3.setBackgroundResource(R.drawable.button_option);
        btnOption4.setBackgroundResource(R.drawable.button_option);
        btnOption1.setEnabled(true);
        btnOption2.setEnabled(true);
        btnOption3.setEnabled(true);
        btnOption4.setEnabled(true);
        btnOption1.setAlpha(1.0f);
        btnOption2.setAlpha(1.0f);
        btnOption3.setAlpha(1.0f);
        btnOption4.setAlpha(1.0f);
    }

    private void highlightCorrectAnswer(Question question) {
        int correctIndex = question.getCorrectOptionIndex();
        getButtonByIndex(correctIndex).setBackgroundResource(R.drawable.button_correct);
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return btnOption1;
            case 1: return btnOption2;
            case 2: return btnOption3;
            case 3: return btnOption4;
            default: throw new IllegalArgumentException("Invalid option index: " + index);
        }
    }

    private int calculatePoints(int difficulty) {
        int basePoints = 10;
        return basePoints * difficulty;
    }

    private void finishQuiz() {
        long timeSpent;
        if (quizStartTime <= 0 || (System.currentTimeMillis() - quizStartTime) > MAX_QUIZ_DURATION_MS) {
            timeSpent = 0;
        } else {
            timeSpent = System.currentTimeMillis() - quizStartTime;
        }

        if (currentAttempt != null) {
            currentAttempt.setEndTime(new Date());
            currentAttempt.setTotalQuestions(questions.size());
            currentAttempt.setTimeSpent(timeSpent);

            QuizDatabaseHelper.getInstance(getContext()).addQuizAttempt(currentAttempt);
            QuizDatabaseHelper.getInstance(getContext())
                    .updateUserPoints(currentAttempt.getUserId(), currentAttempt.getScore());
        }

        ResultFragment resultFragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putInt("correct_answers", correctAnswers);
        args.putInt("total_questions", questions.size());
        args.putInt("total_points", totalPoints);
        args.putLong("time_spent", timeSpent);
        resultFragment.setArguments(args);

        ((MainActivity) requireActivity()).loadFragment(resultFragment, true);
    }
}