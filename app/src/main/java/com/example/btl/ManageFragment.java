package com.example.btl;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ManageFragment extends Fragment implements QuestionAdapter.OnQuestionListener {
    private RecyclerView rvQuestions;
    private FloatingActionButton fabAdd;
    private DatabaseHelper dbHelper;
    private QuestionAdapter adapter;
    private List<Question> questionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        rvQuestions = view.findViewById(R.id.rvQuestions);
        fabAdd = view.findViewById(R.id.fabAdd);
        dbHelper = new DatabaseHelper(getContext());
        dbHelper.importQuestionsFromCSV();
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        return view;
    }

    private void refreshData() {
        List<Question> newData = dbHelper.getAllQuestions();

        if (questionList != null) {
            questionList.clear();
            questionList.addAll(newData);
        } else {
            questionList = newData;
        }

        if (adapter == null) {
            adapter = new QuestionAdapter(questionList, this);
            rvQuestions.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showAddEditDialog(Question existingQuestion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_question, null);
        builder.setView(view);

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etContent = view.findViewById(R.id.etContent);
        EditText etA = view.findViewById(R.id.etOptionA);
        EditText etB = view.findViewById(R.id.etOptionB);
        EditText etC = view.findViewById(R.id.etOptionC);
        EditText etD = view.findViewById(R.id.etOptionD);
        EditText etAns = view.findViewById(R.id.etAnswer);


        EditText etChapter = view.findViewById(R.id.etChapter);

        if (existingQuestion != null) {
            tvTitle.setText("Sửa câu hỏi");
            etContent.setText(existingQuestion.getContent());
            etA.setText(existingQuestion.getOptionA());
            etB.setText(existingQuestion.getOptionB());
            etC.setText(existingQuestion.getOptionC());
            etD.setText(existingQuestion.getOptionD());
            etAns.setText(existingQuestion.getAnswer());
            if (etChapter != null) {
                etChapter.setText(String.valueOf(existingQuestion.getChapter()));
            }
        }

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String content = etContent.getText().toString();
            String a = etA.getText().toString();
            String b = etB.getText().toString();
            String c = etC.getText().toString();
            String d = etD.getText().toString();
            String ans = etAns.getText().toString().toUpperCase();

            // Xử lý mặc định nếu người dùng không nhập chương
            int chapter = 1;
            if (etChapter != null && !etChapter.getText().toString().isEmpty()) {
                try {
                    chapter = Integer.parseInt(etChapter.getText().toString());
                } catch (NumberFormatException e) {
                    chapter = 1;
                }
            }

            if (content.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || ans.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingQuestion == null) {
                // Dùng Constructor có chapter
                dbHelper.addQuestion(new Question(0, content, a, b, c, d, ans, chapter));
                Toast.makeText(getContext(), "Đã thêm câu hỏi", Toast.LENGTH_SHORT).show();
            } else {
                existingQuestion.setContent(content);
                existingQuestion.setOptionA(a);
                existingQuestion.setOptionB(b);
                existingQuestion.setOptionC(c);
                existingQuestion.setOptionD(d);
                existingQuestion.setAnswer(ans);
                existingQuestion.setChapter(chapter); // Cập nhật chapter

                dbHelper.updateQuestion(existingQuestion);
                Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
            }
            refreshData();
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    @Override
    public void onEdit(Question q) {
        showAddEditDialog(q);
    }

    @Override
    public void onDelete(Question q) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa câu hỏi này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteQuestion(q.getId());
                    refreshData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}