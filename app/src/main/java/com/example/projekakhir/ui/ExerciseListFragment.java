package com.example.projekakhir.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekakhir.R;
import com.example.projekakhir.api.ApiClient;
import com.example.projekakhir.api.ApiService;
import com.example.projekakhir.model.Exercise;
import com.example.projekakhir.model.ExerciseResponse;
import com.example.projekakhir.ui.adapter.ExerciseAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<Exercise> filteredList = new ArrayList<>();
    private String targetMuscle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        targetMuscle = getArguments() != null ? getArguments().getString("target_muscle") : "";

        fetchExercises();

        return view;
    }

    private void fetchExercises() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ExerciseResponse> call = api.getAllExercises();

        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> allExercises = response.body().getData().getExercises();

                    filteredList.clear();
                    for (Exercise e : allExercises) {
                        List<String> targets = e.getTargetMuscles();
                        if (targets != null) {
                            for (String target : targets) {
                                if (target.toLowerCase().contains(targetMuscle.toLowerCase())) {
                                    filteredList.add(e);
                                    break;
                                }
                            }
                        }
                    }

                    adapter = new ExerciseAdapter(requireContext(), filteredList, exercise -> {
                        Fragment detailFragment = new DetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("exercise_json", new Gson().toJson(exercise));
                        detailFragment.setArguments(bundle);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, detailFragment)
                                .addToBackStack(null)
                                .commit();
                    });

                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                t.printStackTrace();
                // Bisa juga tampilkan Toast / Log error
            }
        });
    }
}
