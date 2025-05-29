package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    public interface OnDocumentListener {
        void onDocumentClick(int position);
    }

    private List<DocumentItem> documentList;
    private OnDocumentListener listener;

    public DocumentAdapter(List<DocumentItem> documentList, OnDocumentListener listener) {
        this.documentList = documentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentItem doc = documentList.get(position);
        holder.textViewTitle.setText(doc.getTitle());

        String url = doc.getUrl().toLowerCase();

        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
            Glide.with(holder.imageViewThumbnail.getContext())
                    .load(doc.getUrl())
                    .placeholder(R.drawable.applicationmobile)
                    .into(holder.imageViewThumbnail);
        } else if (url.endsWith(".pdf")) {
            holder.imageViewThumbnail.setImageResource(R.drawable.pdflogo);
        } else {
            holder.imageViewThumbnail.setImageResource(R.drawable.pdflogo);
        }
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public DocumentItem getDocumentAt(int position) {
        return documentList.get(position);
    }

    public void updateList(List<DocumentItem> newList) {
        documentList.clear();
        documentList.addAll(newList);
        notifyDataSetChanged();
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewThumbnail;
        TextView textViewTitle;

        DocumentViewHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onDocumentClick(getAdapterPosition());
        }
    }
}
