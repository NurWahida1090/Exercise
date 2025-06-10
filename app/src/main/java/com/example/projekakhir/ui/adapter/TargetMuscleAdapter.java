package com.example.projekakhir.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar target otot.
 * Menggunakan layout bawaan Android (simple_list_item_1).
 */
public class TargetMuscleAdapter extends RecyclerView.Adapter<TargetMuscleAdapter.ViewHolder> {

    private List<String> targetList;
    private OnItemClickListener listener;

    /**
     * Interface untuk menangani klik pada item.
     */
    public interface OnItemClickListener {
        void onClick(String targetMuscle);
    }

    /**
     * Konstruktor adapter.
     */
    public TargetMuscleAdapter(List<String> targetList, OnItemClickListener listener) {
        this.targetList = targetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout bawaan Android untuk item list
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String targetMuscle = targetList.get(position);
        holder.textView.setText(targetMuscle);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(targetMuscle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return targetList != null ? targetList.size() : 0;
    }

    /**
     * ViewHolder untuk adapter ini.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ambil TextView dari layout bawaan Android
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
