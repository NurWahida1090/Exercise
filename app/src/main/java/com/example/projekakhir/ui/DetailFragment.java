package com.example.projekakhir.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.projekakhir.R;
import com.example.projekakhir.model.Exercise;
import com.example.projekakhir.util.FavoriteManager;
import com.google.gson.Gson;

import java.util.List;

public class DetailFragment extends Fragment {

    private static final String ARG_EXERCISE_JSON = "exercise_json";
    private Exercise exercise;

    public DetailFragment() {
        // Konstruktor kosong wajib ada
    }

    public static DetailFragment newInstance(String exerciseJson) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXERCISE_JSON, exerciseJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString(ARG_EXERCISE_JSON);
            if (json != null) {
                exercise = new Gson().fromJson(json, Exercise.class);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        try {
            TextView textName = view.findViewById(R.id.tv_exercise_name);
            TextView textDescription = view.findViewById(R.id.tv_instructions);
            ImageView imageExercise = view.findViewById(R.id.iv_exercise_gif);
            TextView textTarget = view.findViewById(R.id.tv_target);
            TextView textBodyPart = view.findViewById(R.id.tv_body_part);
            TextView textSecondaryMuscles = view.findViewById(R.id.tv_secondary_muscles);
            TextView textEquipments = view.findViewById(R.id.tv_equipments);

            if (exercise != null) {
                textName.setText(safe(exercise.getName()));

                // Konversi list instructions
                List<String> instructions = exercise.getInstructions();
                StringBuilder builder = new StringBuilder();
                if (instructions != null && !instructions.isEmpty()) {
                    for (int i = 0; i < instructions.size(); i++) {
                        builder.append("Step ").append(i + 1).append(": ")
                                .append(instructions.get(i)).append("\n\n");
                    }
                } else {
                    builder.append("-");
                }
                textDescription.setText(builder.toString());

                // Konversi list ke string dengan helper
                textTarget.setText("Target: " + listToString(exercise.getTargetMuscles()));
                textBodyPart.setText("Body Part: " + listToString(exercise.getBodyParts()));
                textSecondaryMuscles.setText("Secondary Muscles: " + listToString(exercise.getSecondaryMuscles()));
                textEquipments.setText("Equipments: " + listToString(exercise.getEquipments()));

                // --- BAGIAN PERBAIKAN UNTUK LOAD GIF ---
                if (exercise.getGifUrl() != null && !exercise.getGifUrl().isEmpty()) {
                    GlideUrl glideUrl = new GlideUrl(
                            exercise.getGifUrl(),
                            new LazyHeaders.Builder()
                                    // Pastikan Referer ini sesuai dengan yang diharapkan server
                                    // Seringkali, ini adalah domain tempat API utama Anda di-host
                                    .addHeader("Referer", "https://exercisedb.vercel.app")
                                    .build()
                    );

                    Glide.with(requireContext())
                            .asGif() // Penting untuk memastikan Glide mencoba memuatnya sebagai GIF
                            .load(glideUrl)
                            .error(R.drawable.image_placeholder_bg) // Gambar placeholder jika gagal
                            .placeholder(R.drawable.image_placeholder_bg) // Gambar placeholder saat loading
                            .into(imageExercise);
                } else {
                    // Handle jika URL GIF kosong atau null
                    imageExercise.setImageResource(R.drawable.image_placeholder_bg);
                    Log.e("DETAIL_FRAGMENT", "GIF URL is null or empty for exercise: " + exercise.getName());
                }
            } else {
                Log.e("DETAIL_FRAGMENT", "Exercise object is NULL");
            }

        } catch (Exception e) {
            Log.e("DETAIL_FRAGMENT", "Error in onCreateView", e);
        }

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        ImageView btnLove = view.findViewById(R.id.btn_love);

        // Set icon sesuai status favorit
        boolean isFavorite = FavoriteManager.isFavorite(requireContext(), exercise);
        btnLove.setImageResource(isFavorite ? R.drawable.ic_favfilled : R.drawable.ic_favborder);

        btnLove.setOnClickListener(v -> {
            boolean currentlyFavorite = FavoriteManager.isFavorite(requireContext(), exercise);
            if (currentlyFavorite) {
                FavoriteManager.removeFavorite(requireContext(), exercise);
                btnLove.setImageResource(R.drawable.ic_favborder);
            } else {
                FavoriteManager.addFavorite(requireContext(), exercise);
                btnLove.setImageResource(R.drawable.ic_favfilled);
            }
        });

        return view;
    }

    // Helper aman untuk String
    private String safe(String input) {
        return input != null ? input : "-";
    }

    //Helper untuk mengubah List<String> ke String
    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
