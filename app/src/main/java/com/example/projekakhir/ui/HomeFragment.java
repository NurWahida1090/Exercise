package com.example.projekakhir.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView; // Import SearchView dari appcompat
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
    private List<Exercise> originalExerciseList; // Untuk menyimpan daftar asli

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userName.setText("HELLO THERE!");

        reloadIcon.setOnClickListener(v -> {
            loadExercises();
        });

        // --- Logika Pencarian ---
        // Ini sudah benar untuk pencarian real-time
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Biasanya, ini dipanggil saat pengguna menekan tombol "search" pada keyboard.
                // Jika Anda ingin filter langsung saat mengetik, sebagian besar logika ada di onQueryTextChange.
                filterExercises(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Ini adalah metode yang akan dipanggil setiap kali teks dalam SearchView berubah,
                // termasuk saat mengetik satu huruf atau menghapus satu huruf.
                filterExercises(newText);
                return false;
            }
        });
        // --- Akhir Logika Pencarian ---

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
                    // Simpan daftar asli
                    originalExerciseList = response.body().getData().getExercises();
                    Log.d("API", "Total exercises from API: " + originalExerciseList.size());

                    // Inisialisasi adapter dengan daftar asli.
                    // Penting: Jika ada teks pencarian di SearchView saat ini (misalnya setelah rotasi layar),
                    // kita perlu memfilter ulang saat pertama kali daftar dimuat.
                    String currentQuery = searchView.getQuery().toString();
                    List<Exercise> initialListToDisplay;
                    if (!currentQuery.isEmpty()) {
                        // Jika ada query, filter daftar awal
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
                        // Jika tidak ada query, tampilkan semua
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

    // --- Metode untuk memfilter daftar latihan ---
    private void filterExercises(String query) {
        if (adapter == null || originalExerciseList == null) {
            return;
        }

        List<Exercise> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(originalExerciseList); // Jika query kosong, tampilkan semua
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Exercise exercise : originalExerciseList) {
                // Filter berdasarkan nama, target, atau bagian tubuh
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