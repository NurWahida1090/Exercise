package com.example.projekakhir.model;

import java.util.List;

public class ExerciseResponse {
    private boolean success;
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String previousPage;
        private String nextPage;
        private int totalPages;
        private int totalExercises;
        private int currentPage;
        private List<Exercise> exercises;

        public String getPreviousPage() {
            return previousPage;
        }

        public String getNextPage() {
            return nextPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalExercises() {
            return totalExercises;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public List<Exercise> getExercises() {
            return exercises;
        }
    }
}
