package com.example.btl;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiFragment extends Fragment {
    private RecyclerView rvChat;
    private EditText etAiInput;
    private ImageButton btnAiSend;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private Map<String, String> knowledgeBase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        rvChat = view.findViewById(R.id.rvChat);
        etAiInput = view.findViewById(R.id.etAiInput);
        btnAiSend = view.findViewById(R.id.btnAiSend);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        initKnowledgeBase();

        // Welcome message
        addMessage("Chào bạn! Tôi là trợ lý học tập HaUI. Bạn cần tôi giải đáp thắc mắc gì về môn Công nghệ phần mềm không?", false);

        btnAiSend.setOnClickListener(v -> {
            String query = etAiInput.getText().toString().trim();
            if (query.isEmpty()) return;

            addMessage(query, true);
            etAiInput.setText("");

            // Simulate AI thinking
            new Handler().postDelayed(() -> {
                String response = getAiResponse(query.toLowerCase());
                addMessage(response, false);
            }, 500);
        });

        return view;
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new ChatMessage(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    private void initKnowledgeBase() {
        knowledgeBase = new HashMap<>();
        knowledgeBase.put("vòng đời", "SDLC (Software Development Life Cycle) gồm: Khảo sát, Phân tích, Thiết kế, Viết mã, Kiểm thử, Triển khai và Bảo trì.");
        knowledgeBase.put("agile", "Agile là phương pháp phát triển linh hoạt, tập trung vào con người và sự tương tác. Các framework phổ biến: Scrum, XP, Kanban.");
        knowledgeBase.put("scrum", "Scrum là framework Agile phổ biến nhất. Gồm 3 vai trò: Product Owner, Scrum Master, Team. Làm việc theo các Sprints (2-4 tuần).");
        knowledgeBase.put("kiểm thử", "Kiểm thử (Testing) có các mức: Unit Test (đơn vị), Integration Test (tích hợp), System Test (hệ thống) và Acceptance Test (chấp nhận).");
        knowledgeBase.put("uml", "UML (Unified Modeling Language) dùng để thiết kế hệ thống. Có 14 loại biểu đồ, quan trọng nhất là: Use Case, Class, Sequence, State.");
        knowledgeBase.put("waterfall", "Mô hình Thác nước (Waterfall) là mô hình cổ điển, thực hiện tuần tự. Ưu điểm: dễ quản lý. Nhược điểm: khó thay đổi yêu cầu.");
        knowledgeBase.put("nguyên lý", "Các nguyên lý thiết kế quan trọng: SOLID, DRY (Don't Repeat Yourself), KISS (Keep It Simple, Stupid).");
        knowledgeBase.put("haui", "Trường Đại học Công nghiệp Hà Nội là ngôi trường đào tạo kỹ thuật hàng đầu. Chúc bạn học tốt môn CNPM tại đây!");
    }

    private String getAiResponse(String query) {
        for (String key : knowledgeBase.keySet()) {
            if (query.contains(key)) return knowledgeBase.get(key);
        }
        
        if (query.contains("chào") || query.contains("hi") || query.contains("hello")) {
            return "Xin chào! Tôi có thể giúp gì cho bạn trong việc ôn tập môn Công nghệ phần mềm?";
        }
        
        return "Câu hỏi của bạn rất thú vị! Theo kiến thức CNPM, vấn đề này liên quan đến quy trình phát triển. Bạn có thể tìm hiểu thêm ở Chương " + (int)(Math.random() * 5 + 1) + " trong tài liệu của trường nhé.";
    }
}
