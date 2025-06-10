package com.example.projekakhir.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.projekakhir.R;
import com.example.projekakhir.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private Context context;
    private List<Exercise> exerciseList; // Daftar yang sedang ditampilkan
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnItemClickListener listener) {
        this.context = context;
        // Menggunakan ArrayList untuk memastikan daftar bisa dimodifikasi
        this.exerciseList = new ArrayList<>(exercises); // PENTING: Salin daftar asli
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise item = exerciseList.get(position);
        holder.txtName.setText(item.getName());

        String target = (item.getTargetMuscles() != null && !item.getTargetMuscles().isEmpty()) ? item.getTargetMuscles().get(0) : "-";
        holder.txtTarget.setText("Target: " + target);

        String bodyPart = (item.getBodyParts() != null && !item.getBodyParts().isEmpty()) ? item.getBodyParts().get(0) : "-";
        holder.txtBodyPart.setText("Body Part: " + bodyPart);

        // Pastikan URL GIF dari API selalu di-refresh atau digunakan sesuai kebijakan API
        if (item.getGifUrl() != null && !item.getGifUrl().isEmpty()) {
            GlideUrl glideUrl = new GlideUrl(
                    item.getGifUrl(),
                    new LazyHeaders.Builder()
                            .addHeader("Referer", "https://exercisedb.vercel.app")
                            .build()
            );

            Glide.with(context)
                    .asGif()
                    .load(glideUrl)
                    .error(R.drawable.image_placeholder_bg) // Tambahkan ini
                    .placeholder(R.drawable.image_placeholder_bg) // Tambahkan ini
                    .into(holder.imgExercise);
        } else {
            holder.imgExercise.setImageResource(R.drawable.image_placeholder_bg); // Tampilkan placeholder jika URL kosong
            Log.e("EXERCISE_ADAPTER", "GIF URL is null or empty for exercise: " + item.getName());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgExercise;
        TextView txtName, txtTarget, txtBodyPart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgExercise = itemView.findViewById(R.id.ivGif);
            txtName = itemView.findViewById(R.id.tvName);
            txtTarget = itemView.findViewById(R.id.tvTarget);
            txtBodyPart = itemView.findViewById(R.id.tvBodyPart);
        }
    }

    /**
     * Memperbarui daftar latihan yang ditampilkan di RecyclerView.
     * @param newExerciseList Daftar latihan yang sudah difilter.
     */
    public void updateList(List<Exercise> newExerciseList) {
        this.exerciseList.clear(); // Hapus item yang ada
        this.exerciseList.addAll(newExerciseList); // Tambahkan item baru
        notifyDataSetChanged(); // Beri tahu RecyclerView untuk memperbarui tampilan
    }
}