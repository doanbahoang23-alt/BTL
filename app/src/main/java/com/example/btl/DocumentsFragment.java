package com.example.btl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DocumentsFragment extends Fragment {
    private RecyclerView rvDocuments;
    private DocumentAdapter adapter;
    private DatabaseHelper dbHelper;
    private EditText edtSearchDoc;
    private FloatingActionButton fabAddDoc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        rvDocuments = view.findViewById(R.id.rvDocuments);
        edtSearchDoc = view.findViewById(R.id.edtSearchDoc);
        fabAddDoc = view.findViewById(R.id.fabAddDoc);
        dbHelper = new DatabaseHelper(getContext());
        dbHelper.importDocumentsFromCSV();
        loadDocuments();

        // Sự kiện tìm kiếm tài liệu
        edtSearchDoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    adapter.updateData(dbHelper.getAllDocuments());
                } else {
                    adapter.updateData(dbHelper.searchDocuments(keyword));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDocumentDialog();
            }
        });

        return view;
    }

    private void loadDocuments() {
        List<String[]> docs = dbHelper.getAllDocuments();
        if (adapter == null) {
            adapter = new DocumentAdapter(docs, new DocumentAdapter.OnDocumentListener() {
                @Override
                public void onEdit(String[] doc) {
                    showEditDocumentDialog(doc);
                }

                @Override
                public void onDelete(String[] doc) {
                    confirmDeleteDocument(doc);
                }
            });
            rvDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
            rvDocuments.setAdapter(adapter);
        } else {
            adapter.updateData(docs);
        }
    }

    private void showAddDocumentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm tài liệu mới");

        // Set up the input
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText inputTitle = new EditText(getContext());
        inputTitle.setHint("Nhập tiêu đề...");
        layout.addView(inputTitle);

        final EditText inputContent = new EditText(getContext());
        inputContent.setHint("Nhập nội dung...");
        inputContent.setMinLines(3);
        layout.addView(inputContent);

        builder.setView(layout);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = inputTitle.getText().toString().trim();
                String content = inputContent.getText().toString().trim();

                if (!title.isEmpty() && !content.isEmpty()) {
                    dbHelper.addDocument(title, content);
                    Toast.makeText(getContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    loadDocuments(); // Tải lại danh sách
                    edtSearchDoc.setText(""); // Xóa trắng ô tìm kiếm
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showEditDocumentDialog(String[] doc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa tài liệu");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText inputTitle = new EditText(getContext());
        inputTitle.setText(doc[1]); // Hiển thị sẵn title cũ
        layout.addView(inputTitle);

        final EditText inputContent = new EditText(getContext());
        inputContent.setText(doc[2]); // Hiển thị sẵn nội dung cũ
        inputContent.setMinLines(3);
        layout.addView(inputContent);

        builder.setView(layout);
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String title = inputTitle.getText().toString().trim();
            String content = inputContent.getText().toString().trim();

            if (!title.isEmpty() && !content.isEmpty()) {
                int docId = Integer.parseInt(doc[0]); // Trích xuất ID
                dbHelper.updateDocument(docId, title, content);
                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                loadDocuments();
                edtSearchDoc.setText("");
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void confirmDeleteDocument(String[] doc) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài liệu này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int docId = Integer.parseInt(doc[0]);
                    dbHelper.deleteDocument(docId);
                    Toast.makeText(getContext(), "Đã xóa tài liệu", Toast.LENGTH_SHORT).show();
                    loadDocuments();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}