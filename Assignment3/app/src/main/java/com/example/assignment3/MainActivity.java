package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assignment3.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MovieAdapter movieAdapter;
    private OkHttpClient client;
    private Gson gson;
    private SharedViewModel sharedViewModel;
    private ActivityResultLauncher<Intent> favouritesActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this, new ArrayList<>());
        binding.recyclerView.setAdapter(movieAdapter);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        gson = new Gson();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        sharedViewModel.getFavoriteMovies().observe(this, favoriteMovies -> {
            if (movieAdapter != null) {
                movieAdapter.updateMovies(favoriteMovies);
            }
        });

        favouritesActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                refreshMovieFavorites();
            }
        });

        binding.favoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavouriteMoviesActivity.class);
            favouritesActivityLauncher.launch(intent);
        });

        binding.searchButton.setOnClickListener(v -> searchMovies());
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchMovies();
    }

    private void searchMovies() {
        String query = binding.searchField.getText().toString().trim();
        if (!query.isEmpty()) {
            String url = "https://www.omdbapi.com/?apikey=868aefa7&s=" + query + "&type=movie";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        MovieResponse movieResponse = gson.fromJson(jsonResponse, MovieResponse.class);

                        runOnUiThread(() -> {
                            if (movieResponse != null && movieResponse.getSearch() != null) {
                                movieAdapter.updateMovies(movieResponse.getSearch());
                                refreshMovieFavorites(); // Refresh favorites
                            } else {
                                Toast.makeText(MainActivity.this, "Movie not found!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    private void refreshMovieFavorites() {
        List<Movie> favoriteMovies = sharedViewModel.getFavoriteMovies().getValue();
        if (favoriteMovies != null) {
            movieAdapter.updateMovies(favoriteMovies);
        }
    }
}
