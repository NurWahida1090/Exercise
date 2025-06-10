package com.example.projekakhir.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projekakhir.R;
import com.example.projekakhir.api.ApiService;
import com.example.projekakhir.model.Exercise;
import com.example.projekakhir.model.ExerciseResponse;
import com.example.projekakhir.ui.adapter.TargetMuscleAdapter;
import com.example.projekakhir.api.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TargetMuscleListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TargetMuscleAdapter adapter;
    private List<String> targetMusclesList = new ArrayList<>();
    private List<Exercise> allExercises = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_muscle_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_target);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TargetMuscleAdapter(targetMusclesList, target -> {
            Fragment fragment = new ExerciseListFragment(); // tampilkan exercise berdasarkan target
            Bundle bundle = new Bundle();
            bundle.putString("target_muscle", target);
            fragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        fetchAllExercises();
        return view;
    }

    private void fetchAllExercises() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllExercises().enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExerciseResponse> call, @NonNull Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allExercises = response.body().getData().getExercises();

                    Set<String> uniqueTargets = new HashSet<>();
                    for (Exercise ex : allExercises) {
                        if (ex.getTargetMuscles() != null) {
                            Log.d("TARGET_MUSCLES", "Exercise: " + ex.getName() + ", Target: " + ex.getTargetMuscles());
                            uniqueTargets.addAll(ex.getTargetMuscles());
                        }
                    }

                    targetMusclesList.clear();
                    targetMusclesList.addAll(uniqueTargets);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Response failed or empty body");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExerciseResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Request failed", t);
            }
        });
    }
}
