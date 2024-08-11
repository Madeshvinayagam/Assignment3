package com.example.assignment3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment3.databinding.ItemMovieBinding;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private final DatabaseHelper dbHelper;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.movieList = movieList != null ? movieList : new ArrayList<>();
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMovieBinding binding = ItemMovieBinding.inflate(layoutInflater, parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.bind(movie);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MovieDetailsActivity.class);
            intent.putExtra("MOVIE_TITLE", movie.getTitle());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        movieList = newMovies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;
        private final SharedViewModel sharedViewModel;

        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.sharedViewModel = new SharedViewModel();
        }

        public void bind(Movie movie) {
            binding.titleTextView.setText(movie.getTitle());
            binding.yearTextView.setText(movie.getYear());
            Glide.with(binding.posterImageView.getContext()).load(movie.getPoster()).into(binding.posterImageView);

            updateFavoriteButton(movie);

            binding.favouriteButton.setOnClickListener(v -> {
                if (isFavorite(movie)) {
                    dbHelper.removeFavourite(movie.getTitle());
                    sharedViewModel.removeFavorite(movie); // Update SharedViewModel
                } else {
                    dbHelper.addFavorite(movie);
                    sharedViewModel.addFavorite(movie); // Update SharedViewModel
                }
                // Only update the button text to reflect the change
                updateFavoriteButton(movie);
            });

        }

        private void updateFavoriteButton(Movie movie) {
            if (isFavorite(movie)) {
                binding.favouriteButton.setText(R.string.remove_from_favourites);
            } else {
                binding.favouriteButton.setText(R.string.add_to_favourites);
            }
        }

        private boolean isFavorite(Movie movie) {
            List<Movie> favorites = dbHelper.getAllFavourites();
            for (Movie favorite : favorites) {
                if (favorite.getTitle().equals(movie.getTitle())) {
                    return true;
                }
            }
            return false;
        }
    }
}
