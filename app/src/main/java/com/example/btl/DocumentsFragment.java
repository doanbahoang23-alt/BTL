package com.example.btl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DocumentsFragment extends Fragment {
    private RecyclerView rvDocuments;
    private DocumentAdapter adapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        rvDocuments = view.findViewById(R.id.rvDocuments);
        dbHelper = new DatabaseHelper(getContext());

        List<String[]> docs = dbHelper.getAllDocuments();
        adapter = new DocumentAdapter(docs);
        
        rvDocuments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDocuments.setAdapter(adapter);

        return view;
    }
}
