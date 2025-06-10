// FavoriteManager.java
package com.example.projekakhir.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.projekakhir.model.Exercise;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static final String PREF_NAME = "favorites_pref";
    private static final String KEY_FAVORITES = "favorites_list";

    public static void addFavorite(Context context, Exercise exercise) {
        List<Exercise> favorites = getFavorites(context);
        if (!favorites.contains(exercise)) {
            favorites.add(exercise);
            saveFavorites(context, favorites);
        }
    }

    public static void removeFavorite(Context context, Exercise exercise) {
        List<Exercise> favorites = getFavorites(context);
        favorites.remove(exercise);
        saveFavorites(context, favorites);
    }

    public static boolean isFavorite(Context context, Exercise exercise) {
        List<Exercise> favorites = getFavorites(context);
        for (Exercise fav : favorites) {
            if (fav.getId().equals(exercise.getId())) {
                return true;
            }
        }
        return false;
    }

    public static List<Exercise> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAVORITES, null);
        Type type = new TypeToken<ArrayList<Exercise>>() {}.getType();
        return json != null ? new Gson().fromJson(json, type) : new ArrayList<>();
    }

    private static void saveFavorites(Context context, List<Exercise> favorites) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FAVORITES, new Gson().toJson(favorites));
        editor.apply();
    }
}
