package com.example.projekakhir.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView; // Pastikan ini diimport
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ImageView reloadIcon;
    private TextView userName;
    private ProgressBar loadingProgressBar;
    private SearchView searchView;
    private TextView tvTargetMusclesLink; // Deklarasikan TextView baru
    private List<Exercise> originalExerciseList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_exercise);
        reloadIcon = view.findViewById(R.id.logo_reload);
        userName = view.findViewById(R.id.user_name);
        loadingProgressBar = view.findViewById(R.id.loading_progress);
        searchView = view.findViewById(R.id.search_view);
        tvTargetMusclesLink = view.findViewById(R.id.tv_target_muscles_link); // Inisialisasi TextView baru

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userName.setText("HELLO THERE!");

        reloadIcon.setOnClickListener(v -> {
            loadExercises();
        });

        // --- Logika Pencarian ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterExercises(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterExercises(newText);
                return false;
            }
        });
        // --- Akhir Logika Pencarian ---

        // --- Listener untuk "Target Muscles" ---
        tvTargetMusclesLink.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.fragment_container, new CategoryFragment());
            transaction.addToBackStack(null); // pengguna kembali
            transaction.commit();
        });
        // --- Akhir Listener "Target Muscles" ---

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadExercises();
    }

    private void loadExercises() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        Call<ExerciseResponse> call = api.getAllExercises();

        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                loadingProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    originalExerciseList = response.body().getData().getExercises();
                    Log.d("API", "Total exercises from API: " + originalExerciseList.size());

                    String currentQuery = searchView.getQuery().toString();
                    List<Exercise> initialListToDisplay;
                    if (!currentQuery.isEmpty()) {
                        initialListToDisplay = new ArrayList<>();
                        String lowerCaseQuery = currentQuery.toLowerCase();
                        for (Exercise exercise : originalExerciseList) {
                            if (exercise.getName() != null && exercise.getName().toLowerCase().contains(lowerCaseQuery) ||
                                    exercise.getTargetMuscles() != null && exercise.getTargetMuscles().toString().toLowerCase().contains(lowerCaseQuery) ||
                                    exercise.getBodyParts() != null && exercise.getBodyParts().toString().toLowerCase().contains(lowerCaseQuery)) {
                                initialListToDisplay.add(exercise);
                            }
                        }
                    } else {
                        initialListToDisplay = originalExerciseList;
                    }

                    adapter = new ExerciseAdapter(getContext(), initialListToDisplay, exercise -> {
                        Gson gson = new Gson();
                        String exerciseJson = gson.toJson(exercise);

                        DetailFragment detailFragment = DetailFragment.newInstance(exerciseJson);

                        FragmentTransaction transaction = requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();

                        transaction.replace(R.id.fragment_container, detailFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(getContext(), "Response not successful", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Response error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Failed to load exercises: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_FAILURE", t.toString());
            }
        });
    }

    private void filterExercises(String query) {
        if (adapter == null || originalExerciseList == null) {
            return;
        }

        List<Exercise> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(originalExerciseList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Exercise exercise : originalExerciseList) {
                if (exercise.getName() != null && exercise.getName().toLowerCase().contains(lowerCaseQuery) ||
                        (exercise.getTargetMuscles() != null && exercise.getTargetMuscles().toString().toLowerCase().contains(lowerCaseQuery)) ||
                        (exercise.getBodyParts() != null && exercise.getBodyParts().toString().toLowerCase().contains(lowerCaseQuery))) {
                    filteredList.add(exercise);
                }
            }
        }
        adapter.updateList(filteredList);
    }
}