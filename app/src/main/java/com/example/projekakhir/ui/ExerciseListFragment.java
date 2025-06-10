package com.example.projekakhir.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView; // Import SearchView

import android.util.Log; // Tambahkan Log untuk debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Tambahkan Toast untuk pesan pengguna

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
    private List<Exercise> allExercisesFilteredByTarget = new ArrayList<>(); // Daftar asli setelah filter target muscle
    private SearchView searchView; // Deklarasikan SearchView
    private String targetMuscle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_exercise_list); // Ganti ID ini
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.search_view_exercise_list); // Inisialisasi SearchView

        targetMuscle = getArguments() != null ? getArguments().getString("target_muscle") : "";

        // --- Logika Pencarian Real-Time ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tidak perlu banyak dilakukan di sini karena onQueryTextChange sudah menangani real-time
                filterExercises(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Ini yang akan memicu pencarian real-time setiap kali teks berubah
                filterExercises(newText);
                return false;
            }
        });
        // --- Akhir Logika Pencarian Real-Time ---

        fetchExercises();

        return view;
    }

    private void fetchExercises() {
        // Asumsi API call ini hanya dilakukan sekali untuk mendapatkan semua data
        // yang akan difilter berdasarkan target muscle, lalu difilter lagi oleh search.
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ExerciseResponse> call = api.getAllExercises();

        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> allExercisesFromApi = response.body().getData().getExercises();

                    // Filter awal berdasarkan targetMuscle yang diterima dari HomeFragment
                    allExercisesFilteredByTarget.clear();
                    for (Exercise e : allExercisesFromApi) {
                        List<String> targets = e.getTargetMuscles();
                        if (targets != null) {
                            for (String target : targets) {
                                if (target.toLowerCase().contains(targetMuscle.toLowerCase())) {
                                    allExercisesFilteredByTarget.add(e);
                                    break;
                                }
                            }
                        }
                    }

                    // Inisialisasi adapter dengan daftar yang sudah difilter berdasarkan target muscle.
                    // Kemudian, terapkan filter dari searchView jika sudah ada teks saat ini.
                    // Ini penting jika fragment di-recreate dan ada teks di searchView.
                    String currentSearchQuery = searchView.getQuery().toString();
                    List<Exercise> initialDisplayList = new ArrayList<>();

                    if (currentSearchQuery.isEmpty()) {
                        initialDisplayList.addAll(allExercisesFilteredByTarget);
                    } else {
                        // Jika ada query saat initial load, filter lagi dari allExercisesFilteredByTarget
                        String lowerCaseQuery = currentSearchQuery.toLowerCase();
                        for (Exercise exercise : allExercisesFilteredByTarget) {
                            if (exercise.getName() != null && exercise.getName().toLowerCase().contains(lowerCaseQuery) ||
                                    (exercise.getEquipments() != null && exercise.getEquipments().toString().toLowerCase().contains(lowerCaseQuery))) {
                                initialDisplayList.add(exercise);
                            }
                        }
                    }


                    adapter = new ExerciseAdapter(requireContext(), initialDisplayList, exercise -> {
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

                    if (initialDisplayList.isEmpty() && !targetMuscle.isEmpty()) {
                        Toast.makeText(getContext(), "No exercises found for " + targetMuscle, Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load exercises. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("ExerciseListFragment", "Response not successful: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ExerciseListFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    /**
     * Memfilter daftar latihan berdasarkan query pencarian yang diberikan.
     * Filter dilakukan pada daftar yang sudah difilter oleh target muscle.
     * Pencarian berdasarkan nama latihan dan equipment.
     * @param query Teks pencarian dari SearchView.
     */
    private void filterExercises(String query) {
        if (adapter == null || allExercisesFilteredByTarget == null) {
            return; // Pastikan adapter dan daftar asli sudah dimuat
        }

        List<Exercise> currentFilteredSearchList = new ArrayList<>();
        if (query.isEmpty()) {
            // Jika query kosong, tampilkan semua latihan yang sudah difilter oleh target muscle
            currentFilteredSearchList.addAll(allExercisesFilteredByTarget);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Exercise exercise : allExercisesFilteredByTarget) {
                // Filter berdasarkan nama ATAU equipment
                boolean nameMatches = exercise.getName() != null && exercise.getName().toLowerCase().contains(lowerCaseQuery);
                boolean equipmentMatches = false;
                if (exercise.getEquipments() != null) {
                    for (String equipment : exercise.getEquipments()) {
                        if (equipment.toLowerCase().contains(lowerCaseQuery)) {
                            equipmentMatches = true;
                            break;
                        }
                    }
                }
                if (nameMatches || equipmentMatches) {
                    currentFilteredSearchList.add(exercise);
                }
            }
        }
        adapter.updateList(currentFilteredSearchList); // Panggil metode di adapter untuk memperbarui daftar
    }
}