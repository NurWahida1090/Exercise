package com.example.projekakhir.api;

import com.example.projekakhir.model.Exercise;
import com.example.projekakhir.model.ExerciseResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("v1/exercises?offset=0&limit=100")
    Call<ExerciseResponse> getAllExercises();
}

