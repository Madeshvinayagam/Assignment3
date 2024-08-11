package com.example.assignment3;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Movie>> favoriteMovies = new MutableLiveData<>();

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(List<Movie> movies) {
        favoriteMovies.setValue(movies);
    }

    public void addFavorite(Movie movie) {
        List<Movie> currentFavorites = favoriteMovies.getValue();
        if (currentFavorites != null) {
            currentFavorites.add(movie);
            setFavoriteMovies(currentFavorites);
            Log.d("SharedViewModel", "Favorite added: " + movie.getTitle());
        }
    }

    public void removeFavorite(Movie movie) {
        List<Movie> currentFavorites = favoriteMovies.getValue();
        if (currentFavorites != null) {
            currentFavorites.remove(movie);
            setFavoriteMovies(currentFavorites);
            Log.d("SharedViewModel", "Favorite removed: " + movie.getTitle());
        }
    }
}
