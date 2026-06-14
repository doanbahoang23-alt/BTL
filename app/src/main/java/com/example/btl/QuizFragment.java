package com.example.btl;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.List;

public class QuizFragment extends Fragment {
    private TextView tvQuestionCount, tvQuestionContent, tvTimer;
    private Button btnA, btnB, btnC, btnD, btnNext, btnFinish;
    private List<Question> questionList;
    private int currentIdx = 0;
    private int score = 0;
    private DatabaseHelper dbHelper;
    private boolean answered = false;

    private CountDownTimer countDownTimer;
    private long dynamicTimeLimit;
    private int currentExamId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // ÁNH XẠ ĐẦY ĐỦ CÁC VIEW Ở ĐÂY
        tvQuestionCount = view.findViewById(R.id.tvQuestionCount);
        tvQuestionContent = view.findViewById(R.id.tvQuestionContent);
        tvTimer = view.findViewById(R.id.tvTimer); // <-- ĐÂY CHÍNH LÀ DÒNG BỊ THIẾU GÂY CRASH
        btnA = view.findViewById(R.id.btnOptionA);
        btnB = view.findViewById(R.id.btnOptionB);
        btnC = view.findViewById(R.id.btnOptionC);
        btnD = view.findViewById(R.id.btnOptionD);
        btnNext = view.findViewById(R.id.btnNext);
        btnFinish = view.findViewById(R.id.btnFinish);

        dbHelper = new DatabaseHelper(getContext());

        int numQuestions = 25;

        if (getArguments() != null) {
            currentExamId = getArguments().getInt("exam_id", -1);
            numQuestions = getArguments().getInt("num_questions", 25);
        }

        if (currentExamId != -1) {
            questionList = dbHelper.getQuestionsForExam(currentExamId);
        } else {
            questionList = dbHelper.getRandomQuestionsByChapter(numQuestions);
        }

        // CHỐT CHẶN AN TOÀN TRÁNH VĂNG APP
        if (questionList == null || questionList.isEmpty()) {
            tvQuestionContent.setText("Không có câu hỏi nào trong hệ thống. Vui lòng thêm câu hỏi hoặc tạo đề thi trước.");
            btnNext.setVisibility(View.GONE);
            return view;
        }

        dynamicTimeLimit = (long) questionList.size() * 60 * 1000L;

        displayQuestion();
        startTimer();

        View.OnClickListener optionClickListener = v -> {
            if (answered) return;
            answered = true;
            Button selectedBtn = (Button) v;
            String selectedAnswer = "";
            if (v.getId() == R.id.btnOptionA) selectedAnswer = "A";
            else if (v.getId() == R.id.btnOptionB) selectedAnswer = "B";
            else if (v.getId() == R.id.btnOptionC) selectedAnswer = "C";
            else if (v.getId() == R.id.btnOptionD) selectedAnswer = "D";

            Question currentQ = questionList.get(currentIdx);
            if (selectedAnswer.equals(currentQ.getAnswer())) {
                score++;
                selectedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                selectedBtn.setTextColor(Color.WHITE);
            } else {
                selectedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                selectedBtn.setTextColor(Color.WHITE);
                highlightCorrectAnswer(currentQ.getAnswer());
            }
            btnNext.setVisibility(View.VISIBLE);
        };

        btnA.setOnClickListener(optionClickListener);
        btnB.setOnClickListener(optionClickListener);
        btnC.setOnClickListener(optionClickListener);
        btnD.setOnClickListener(optionClickListener);

        btnNext.setOnClickListener(v -> {
            if (currentIdx < questionList.size() - 1) {
                currentIdx++;
                displayQuestion();
            } else {
                showResultDialog(null);
            }
        });

        btnFinish.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn kết thúc bài thi ngay bây giờ?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> showResultDialog(null))
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return view;
    }

    private void displayQuestion() {
        if (questionList == null || questionList.isEmpty()) return;
        answered = false;
        btnNext.setVisibility(View.GONE);
        resetButtonColors();

        Question q = questionList.get(currentIdx);
        tvQuestionCount.setText("Câu hỏi " + (currentIdx + 1) + "/" + questionList.size());
        tvQuestionContent.setText(q.getContent());
        btnA.setText("A. " + q.getOptionA());
        btnB.setText("B. " + q.getOptionB());
        btnC.setText("C. " + q.getOptionC());
        btnD.setText("D. " + q.getOptionD());
    }

    private void resetButtonColors() {
        ColorStateList defaultTint = ColorStateList.valueOf(Color.parseColor("#F5F5F5"));
        btnA.setBackgroundTintList(defaultTint);
        btnB.setBackgroundTintList(defaultTint);
        btnC.setBackgroundTintList(defaultTint);
        btnD.setBackgroundTintList(defaultTint);

        btnA.setTextColor(Color.parseColor("#333333"));
        btnB.setTextColor(Color.parseColor("#333333"));
        btnC.setTextColor(Color.parseColor("#333333"));
        btnD.setTextColor(Color.parseColor("#333333"));
    }

    private void highlightCorrectAnswer(String answer) {
        ColorStateList greenTint = ColorStateList.valueOf(Color.GREEN);
        if (answer.equals("A")) {
            btnA.setBackgroundTintList(greenTint);
            btnA.setTextColor(Color.WHITE);
        } else if (answer.equals("B")) {
            btnB.setBackgroundTintList(greenTint);
            btnB.setTextColor(Color.WHITE);
        } else if (answer.equals("C")) {
            btnC.setBackgroundTintList(greenTint);
            btnC.setTextColor(Color.WHITE);
        } else if (answer.equals("D")) {
            btnD.setBackgroundTintList(greenTint);
            btnD.setTextColor(Color.WHITE);
        }
    }

    private void showResultDialog(String messagePrefix) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        String aiComment;
        if (score >= 18) aiComment = "Chúc mừng học sinh đã hoàn thành xuất sắc bài thi.";
        else if (score >= 14) aiComment = "Khá tốt! Bạn đã nắm vững kiến thức cơ bản.";
        else if (score >= 10) aiComment = "Đạt yêu cầu. Cần ôn tập thêm.";
        else aiComment = "Bạn cần cố gắng nhiều hơn.";

        String finalMessage = (messagePrefix != null ? messagePrefix + "\n\n" : "")
                + "Số điểm của bạn: " + score + "/" + questionList.size()
                + "\n\nNhận xét: " + aiComment;

        new AlertDialog.Builder(requireContext())
                .setTitle("Kết quả thi thử")
                .setMessage(finalMessage)
                .setPositiveButton("Làm lại", (dialog, which) -> {
                    currentIdx = 0;
                    score = 0;

                    if (currentExamId != -1) {
                        java.util.Collections.shuffle(questionList);
                    } else {
                        int num = (getArguments() != null) ? getArguments().getInt("num_questions", 25) : 25;
                        questionList = dbHelper.getRandomQuestionsByChapter(num);
                    }

                    displayQuestion();
                    startTimer();
                })
                .setNegativeButton("Thoát", (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();

                    // Hiển thị lại Bottom Navigation khi thoát thi
                    if (getActivity() instanceof MainActivity) {
                        View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(dynamicTimeLimit, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                String timeFormatted = String.format("%02d:%02d", seconds / 60, seconds % 60);
                if (tvTimer != null) {
                    tvTimer.setText(timeFormatted);
                }
            }

            @Override
            public void onFinish() {
                if (tvTimer != null) tvTimer.setText("00:00");
                showResultDialog("Đã hết thời gian làm bài!");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}