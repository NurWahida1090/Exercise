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
import com.example.projekakhir.model.Exercise;

import java.util.List;
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private Context context;
    private List<Exercise> exerciseList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnItemClickListener listener) {
        this.context = context;
        this.exerciseList = exercises;
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

        GlideUrl glideUrl = new GlideUrl(
                item.getGifUrl(),
                new LazyHeaders.Builder()
                        .addHeader("Referer", "https://exercisedb.vercel.app")
                        .build()
        );

        Glide.with(context)
                .asGif()
                .load(glideUrl)
                .into(holder.imgExercise);

        // Set klik listener card
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
}
