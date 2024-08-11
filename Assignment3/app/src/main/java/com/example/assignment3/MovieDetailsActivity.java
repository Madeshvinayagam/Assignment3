package com.example.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.assignment3.databinding.ActivityMovieDetailsBinding;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MovieDetailsActivity extends AppCompatActivity {

    private ActivityMovieDetailsBinding binding;
    private OkHttpClient client;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        String title = intent.getStringExtra("MOVIE_TITLE");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        gson = new Gson();
        assert title != null;
        getMovieDetails(title);
    }

    private void getMovieDetails(String title) {
        String query = title.trim();
        if (!query.isEmpty()) {
            String url = "https://www.omdbapi.com/?apikey=868aefa7&t=" + query + "&type=movie";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        MovieResponse movieResponse = gson.fromJson(jsonResponse, MovieResponse.class);

                        runOnUiThread(() -> {
                            if (movieResponse != null) {
                                setMovieDetails(movieResponse);
                            } else {
                                Toast.makeText(MovieDetailsActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    private void setMovieDetails(MovieResponse movieResponse) {
        binding.titleTextView.setText(movieResponse.getTitle());
        binding.yearTextView.setText(String.format("%s%s", getString(R.string.year), movieResponse.getYear()));
        binding.releasedTextView.setText(String.format("%s%s", getString(R.string.released), movieResponse.getReleased()));
        binding.runtimeTextView.setText(String.format("%s%s", getString(R.string.runtime), movieResponse.getRuntime()));
        binding.genreTextView.setText(String.format("%s%s", getString(R.string.genre), movieResponse.getGenre()));
        binding.directorTextView.setText(String.format("%s%s", getString(R.string.director), movieResponse.getDirector()));
        binding.writerTextView.setText(String.format("%s%s", getString(R.string.writer), movieResponse.getWriter()));
        binding.actorsTextView.setText(String.format("%s%s", getString(R.string.actors), movieResponse.getActors()));
        binding.plotTextView.setText(String.format("%s%s", getString(R.string.plot), movieResponse.getPlot()));
        binding.languageTextView.setText(String.format("%s%s", getString(R.string.language), movieResponse.getLanguage()));
        binding.countryTextView.setText(String.format("%s%s", getString(R.string.country), movieResponse.getCountry()));
        binding.ratingsTextView.setText(String.format("%s%s%s%s", getString(R.string.imdb_rating), movieResponse.getImdbRating(), getString(R.string.nrotten_tomatoes), movieResponse.getRatings().get(1).getValue()));
        binding.imdbVotesTextView.setText(String.format("%s%s", getString(R.string.imdb_votes), movieResponse.getImdbVotes()));


        Glide.with(this).load(movieResponse.getPoster()).into(binding.posterImageView);
    }
}
