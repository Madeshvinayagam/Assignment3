package com.example.assignment3;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponse {
    private String Title;
    private String Year;
    private String Released;
    private String Runtime;
    private String Genre;
    private String Director;
    private String Writer;
    private String Actors;
    private String Plot;
    private String Language;
    private String Country;
    private String imdbRating;
    private String imdbVotes;
    private String Poster;
    private List<Rating> Ratings;
    private List<Movie> Search;

    @Getter
    @Setter
    public static class Rating {
        private String Source;
        private String Value;
    }
}
