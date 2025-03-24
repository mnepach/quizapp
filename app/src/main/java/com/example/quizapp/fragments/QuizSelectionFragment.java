package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.utils.AnimationUtils;

import java.util.List;

public class QuizSelectionFragment extends Fragment {

    private RadioGroup rgQuizzes;
    private Button btnStartSelected;
    private TextView tvDifficulty;
    private TextView tvTime;
    private TextView tvQuestions;

    private List<Quiz> quizzes;
    private long selectedQuizId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_selection, container, false);

        // Инициализация UI элементов
        rgQuizzes = view.findViewById(R.id.rg_quizzes);
        btnStartSelected = view.findViewById(R.id.btn_start_selected);
        tvDifficulty = view.findViewById(R.id.tv_difficulty);
        tvTime = view.findViewById(R.id.tv_time);
        tvQuestions = view.findViewById(R.id.tv_questions);

        // Загружаем список викторин
        loadQuizzes();

        // Обработчик выбора викторины
        rgQuizzes.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                // Получаем ID выбранной викторины из тега радиокнопки
                RadioButton selectedButton = view.findViewById(checkedId);
                selectedQuizId = (long) selectedButton.getTag();

                // Отображаем информацию о выбранной викторине
                showQuizDetails(selectedQuizId);
            }
        });

        // Обработчик нажатия кнопки "Начать"
        btnStartSelected.setOnClickListener(v -> {
            if (selectedQuizId != -1) {
                AnimationUtils.buttonClick(v);

                // Создаем фрагмент вопроса и передаем ему ID выбранной викторины
                QuestionFragment questionFragment = new QuestionFragment();
                Bundle args = new Bundle();
                args.putLong("quiz_id", selectedQuizId);
                questionFragment.setArguments(args);

                // Переходим к фрагменту вопроса
                ((MainActivity) requireActivity()).loadFragment(questionFragment, true);
            } else {
                Toast.makeText(getContext(), R.string.select_quiz_first, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadQuizzes() {
        // Получаем список викторин из базы данных
        quizzes = QuizDatabaseHelper.getInstance(getContext()).getAllQuizzes();

        // Добавляем радиокнопки для каждой викторины
        for (Quiz quiz : quizzes) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(quiz.getTitle());
            rb.setTag(quiz.getId()); // Сохраняем ID викторины в теге
            rgQuizzes.addView(rb);
        }
    }

    private void showQuizDetails(long quizId) {
        // Находим выбранную викторину
        Quiz selectedQuiz = null;
        for (Quiz quiz : quizzes) {
            if (quiz.getId() == quizId) {
                selectedQuiz = quiz;
                break;
            }
        }

        if (selectedQuiz != null) {
            // Отображаем информацию о сложности
            String difficulty;
            switch (selectedQuiz.getDifficulty()) {
                case 1:
                    difficulty = getString(R.string.difficulty_easy);
                    break;
                case 2:
                    difficulty = getString(R.string.difficulty_medium);
                    break;
                case 3:
                    difficulty = getString(R.string.difficulty_hard);
                    break;
                default:
                    difficulty = getString(R.string.difficulty_unknown);
            }
            tvDifficulty.setText(getString(R.string.difficulty_label, difficulty));

            // Отображаем информацию о времени на вопрос
            tvTime.setText(getString(R.string.time_per_question, selectedQuiz.getTimePerQuestionInSeconds()));

            // Отображаем количество вопросов
            int questionCount = selectedQuiz.getQuestions().size();
            tvQuestions.setText(getString(R.string.question_count, questionCount));

            // Показываем детали викторины
            tvDifficulty.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            tvQuestions.setVisibility(View.VISIBLE);

            // Активируем кнопку "Начать"
            btnStartSelected.setEnabled(true);
        }
    }
}