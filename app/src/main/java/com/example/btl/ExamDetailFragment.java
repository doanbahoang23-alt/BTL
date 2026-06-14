package com.example.btl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExamDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_detail, container, false);

        TextView tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        RecyclerView rvExamQuestions = view.findViewById(R.id.rvExamQuestions);

        ImageButton btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            int examId = getArguments().getInt("exam_id");
            String title = getArguments().getString("exam_title");
            tvDetailTitle.setText(title);

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            List<Question> questions = dbHelper.getQuestionsForExam(examId);

            rvExamQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

            QuestionAdapter adapter = new QuestionAdapter(questions, new QuestionAdapter.OnQuestionListener() {
                @Override public void onEdit(Question q) { }
                @Override public void onDelete(Question q) { }
            });
            rvExamQuestions.setAdapter(adapter);
        }

        return view;
    }
}