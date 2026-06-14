package com.example.btl;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ExamListFragment extends Fragment {
    private RecyclerView rvExams;
    private FloatingActionButton fabCreateExam;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_list, container, false);

        rvExams = view.findViewById(R.id.rvExams);
        fabCreateExam = view.findViewById(R.id.fabCreateExam);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        dbHelper = new DatabaseHelper(getContext());

        rvExams.setLayoutManager(new LinearLayoutManager(getContext()));
        loadExams();

        fabCreateExam.setOnClickListener(v -> showCreateExamDialog());

        return view;
    }

    private void loadExams() {
        List<Exam> exams = dbHelper.getAllExams();

        // Sử dụng ExamAdapter từ file bên ngoài
        ExamAdapter adapter = new ExamAdapter(exams, exam -> {
            ExamDetailFragment detailFragment = new ExamDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("exam_id", exam.getId());
            bundle.putString("exam_title", exam.getTitle());
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvExams.setAdapter(adapter);
    }

    private void showCreateExamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tạo đề thi mới");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etTitle = new EditText(getContext());
        etTitle.setHint("Tên đề thi (VD: Đề thi số 1)");
        layout.addView(etTitle);

        final EditText etCount = new EditText(getContext());
        etCount.setHint("Số lượng câu hỏi (VD: 30)");
        etCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCount);

        builder.setView(layout);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String countStr = etCount.getText().toString().trim();

            if (title.isEmpty() || countStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = Integer.parseInt(countStr);
            dbHelper.createSavedExam(title, count);
            Toast.makeText(getContext(), "Tạo đề thi thành công!", Toast.LENGTH_SHORT).show();
            loadExams();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}