package com.example.btl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {
    private List<String[]> documents;

    public DocumentAdapter(List<String[]> documents) {
        this.documents = documents;
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
        holder.tvTitle.setText(doc[0]);
        holder.tvContent.setText(doc[1]);
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDocTitle);
            tvContent = itemView.findViewById(R.id.tvDocContent);
        }
    }

    public void updateData(List<String[]> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }
}
