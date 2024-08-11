package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment3.databinding.ActivityFavoriteMoviesBinding;

import java.util.List;

public class FavouriteMoviesActivity extends AppCompatActivity {

    private ActivityFavoriteMoviesBinding binding;
    private DatabaseHelper dbHelper;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityFavoriteMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up back button
        binding.backButton.setOnClickListener(v -> {
            // Notify changes to MainActivity and finish
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        dbHelper = new DatabaseHelper(this);

        // Set up RecyclerView with layout manager
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        loadFavorites();
    }

    private void loadFavorites() {
        List<Movie> favoriteMovies = dbHelper.getAllFavourites();
        MovieAdapter adapter = new MovieAdapter(this, favoriteMovies);
        binding.recyclerViewFavorites.setAdapter(adapter);

        // Update SharedViewModel with new favorites
        sharedViewModel.setFavoriteMovies(favoriteMovies);
    }
}
