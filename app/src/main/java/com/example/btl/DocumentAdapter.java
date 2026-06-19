package com.example.btl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {
    private List<String[]> documents;
    private OnDocumentListener listener; // Giao diện lắng nghe sự kiện

    public interface OnDocumentListener {
        void onEdit(String[] doc);
        void onDelete(String[] doc);
    }

    public DocumentAdapter(List<String[]> documents, OnDocumentListener listener) {
        this.documents = documents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        String[] doc = documents.get(position);
        holder.tvTitle.setText(doc[1]);
        holder.tvContent.setText(doc[2]);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(doc));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(doc));
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;
        ImageButton btnEdit, btnDelete;

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDocTitle);
            tvContent = itemView.findViewById(R.id.tvDocContent);
            btnEdit = itemView.findViewById(R.id.btnEditDoc);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoc);
        }
    }

    public void updateData(List<String[]> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }
}