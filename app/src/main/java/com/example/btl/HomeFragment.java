package com.example.btl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.List;

public class HomeFragment extends Fragment {
    private TextView tvWelcome, tvProgress;
    private ProgressBar progressBar;
    private ImageButton btnLogout;
    private CardView cardStartQuiz, cardAskAi, cardDocuments, cardCreateQuiz;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvProgress = view.findViewById(R.id.tvProgress);
        progressBar = view.findViewById(R.id.progressBar);
        btnLogout = view.findViewById(R.id.btnLogout);
        cardStartQuiz = view.findViewById(R.id.cardStartQuiz);
        cardAskAi = view.findViewById(R.id.cardAskAi);
        cardDocuments = view.findViewById(R.id.cardDocuments);
        cardCreateQuiz = view.findViewById(R.id.cardCreateQuiz);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "Người dùng");
        tvWelcome.setText("Xin chào, " + username + "!");

        // Update real progress
        int progress = dbHelper.getUserProgress(username);
        progressBar.setProgress(progress);
        tvProgress.setText("Tiến độ: " + progress + "%");

        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).logout();
            }
        });


        cardStartQuiz.setOnClickListener(v -> {
            String[] options = {"Thi ngẫu nhiên (25 câu - 25 phút)", "Chọn đề thi đã lưu"};
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Tùy chọn Thi thử")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            startQuizMode(-1, 25);
                        } else {
                            // Mở danh sách chọn đề thi đã lưu
                            showSavedExamsForQuiz();
                        }
                    })
                    .show();
        });


        cardAskAi.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).bottomNav.setSelectedItemId(R.id.nav_ai);
            }
        });


        if (cardDocuments != null) {
            cardDocuments.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).bottomNav.setSelectedItemId(R.id.nav_doc);
                }
            });
        }

        cardCreateQuiz.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ExamListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void showSavedExamsForQuiz() {
        List<Exam> exams = dbHelper.getAllExams();
        if (exams.isEmpty()) {
            android.widget.Toast.makeText(getContext(), "Chưa có đề thi nào! Hãy vào phần Tạo đề thi trước.", android.widget.Toast.LENGTH_LONG).show();
            return;
        }

        String[] examTitles = new String[exams.size()];
        for (int i = 0; i < exams.size(); i++) {
            examTitles[i] = exams.get(i).getTitle();
        }

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Chọn đề thi")
                .setItems(examTitles, (dialog, which) -> {
                    int selectedExamId = exams.get(which).getId();
                    startQuizMode(selectedExamId, 0);
                })
                .show();
    }

    private void startQuizMode(int examId, int numQuestions) {
        QuizFragment quizFragment = new QuizFragment();
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putInt("exam_id", examId);
        bundle.putInt("num_questions", numQuestions);
        quizFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, quizFragment)
                .addToBackStack(null)
                .commit();

        if (getActivity() instanceof MainActivity) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation); // Sửa đúng ID của bạn nếu cần
            if (bottomNav != null) bottomNav.setVisibility(View.GONE);
        }
    }


}