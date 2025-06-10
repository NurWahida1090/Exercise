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
        // Pastikan ID ini cocok dengan yang di XML
        loadingProgressBar = view.findViewById(R.id.loading_progress);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userName.setText("HELLO THERE!");

        reloadIcon.setOnClickListener(v -> {
            loadExercises();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Memuat data secara otomatis saat fragment dibuat dan ditampilkan
        loadExercises();
    }

    private void loadExercises() {
        // Tampilkan loading saat memulai request API
        loadingProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE); // Sembunyikan RecyclerView saat loading

        ApiService api = ApiClient.getClient().create(ApiService.class);

        Call<ExerciseResponse> call = api.getAllExercises();

        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                // Sembunyikan loading setelah response diterima
                loadingProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE); // Tampilkan RecyclerView lagi

                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> exerciseList = response.body().getData().getExercises();
                    Log.d("API", "Total exercises from API: " + exerciseList.size());

                    adapter = new ExerciseAdapter(getContext(), exerciseList, exercise -> {
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
                // Sembunyikan loading jika request gagal
                loadingProgressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE); // Tampilkan RecyclerView lagi

                Toast.makeText(getContext(), "Failed to load exercises: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_FAILURE", t.toString());
            }
        });
    }
}