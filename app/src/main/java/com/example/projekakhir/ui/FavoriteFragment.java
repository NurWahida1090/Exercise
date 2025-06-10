package com.example.projekakhir.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekakhir.R;
import com.example.projekakhir.model.Exercise;
import com.example.projekakhir.ui.adapter.ExerciseAdapter;
import com.example.projekakhir.util.FavoriteManager;
import com.google.gson.Gson;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ExerciseAdapter adapter;
    private List<Exercise> favoriteList;

    public FavoriteFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recycler_favorite);
        emptyTextView = view.findViewById(R.id.tv_empty_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteList = FavoriteManager.getFavorites(requireContext());

        adapter = new ExerciseAdapter(getContext(), favoriteList, exercise -> {
            String exerciseJson = new Gson().toJson(exercise);
            DetailFragment detailFragment = DetailFragment.newInstance(exerciseJson);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        toggleEmptyState();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteList.clear();
        favoriteList.addAll(FavoriteManager.getFavorites(requireContext()));
        adapter.notifyDataSetChanged();
        toggleEmptyState();
    }

    private void toggleEmptyState() {
        if (favoriteList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }
}
