// app/src/main/java/com/example/quizapp/fragments/HistoryFragment.java
package com.example.quizapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quizapp.R;
import com.example.quizapp.adapters.QuizAttemptAdapter;
import com.example.quizapp.database.QuizDatabaseHelper;
import com.example.quizapp.models.QuizAttempt;
import com.example.quizapp.utils.SharedPreferencesManager;

import java.util.List;

public class HistoryFragment extends Fragment {

    private TextView tvHistoryTitle;
    private ListView lvHistory;
    private TextView tvNoHistory;
    private Button btnBackFromHistory;

    private QuizDatabaseHelper dbHelper;
    private List<QuizAttempt> quizAttempts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        tvHistoryTitle = view.findViewById(R.id.tv_history_title);
        lvHistory = view.findViewById(R.id.lv_history);
        tvNoHistory = view.findViewById(R.id.tv_no_history);
        btnBackFromHistory = view.findViewById(R.id.btn_back_from_history);

        dbHelper = new QuizDatabaseHelper(getContext());

        loadHistoryData();

        btnBackFromHistory.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadHistoryData() {
        long userId = SharedPreferencesManager.getInstance(requireContext()).getCurrentUserId();
        quizAttempts = dbHelper.getQuizAttemptsByUser(userId);

        if (quizAttempts != null && !quizAttempts.isEmpty()) {
            QuizAttemptAdapter adapter = new QuizAttemptAdapter(requireContext(), quizAttempts);
            lvHistory.setAdapter(adapter);
            tvNoHistory.setVisibility(View