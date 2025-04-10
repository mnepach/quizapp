package com.example.quizapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quizapp.R;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.Quiz;
import com.example.quizapp.models.QuizAttempt;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class QuizHistoryAdapter extends ArrayAdapter<QuizAttempt> {

    private final Context context;
    private final List<QuizAttempt> attempts;

    public QuizHistoryAdapter(Context context, List<QuizAttempt> attempts) {
        super(context, R.layout.item_history, attempts);
        this.context = context;
        this.attempts = attempts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        }

        QuizAttempt attempt = attempts.get(position);

        TextView tvQuizTitle = convertView.findViewById(R.id.tv_quiz_title);
        TextView tvQuizDate = convertView.findViewById(R.id.tv_quiz_date);
        TextView tvQuizScore = convertView.findViewById(R.id.tv_quiz_score);
        TextView tvQuizTime = convertView.findViewById(R.id.tv_quiz_time);

        // Получаем информацию о викторине
        Quiz quiz = QuizDatabaseHelper.getInstance(context).getQuiz(attempt.getQuizId());

        if (quiz != null) {
            tvQuizTitle.setText(quiz.getTitle());
        } else {
            tvQuizTitle.setText("Unknown Quiz");
        }

        // Форматируем дату
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        tvQuizDate.setText(dateFormat.format(attempt.getAttemptDate()));

        // Показываем результаты
        int percentage = (int) (((float) attempt.getCorrectAnswers() / attempt.getTotalQuestions()) * 100);
        String scoreText = attempt.getCorrectAnswers() + "/" + attempt.getTotalQuestions() + " (" + percentage + "%)";
        tvQuizScore.setText(scoreText);

        // Вычисляем затраченное время используя метод getTimeSpent() из QuizAttempt
        long timeSpent = attempt.getTimeSpent();
        if (timeSpent == 0 && attempt.getEndTime() != null && attempt.getStartTime() != null) {
            // Вычисляем время вручную, если метод вернул 0, но есть данные о начале и конце
            timeSpent = attempt.getEndTime().getTime() - attempt.getStartTime().getTime();
        }

        // Преобразуем миллисекунды в минуты и секунды
        long minutes = timeSpent / (60 * 1000);
        long seconds = (timeSpent % (60 * 1000)) / 1000;
        String timeText = minutes + " min " + seconds + " sec";
        tvQuizTime.setText(timeText);

        return convertView;
    }
}