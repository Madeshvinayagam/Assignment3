package com.example.assignment3;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Movie {
    private final String Title;
    private final String Year;
    private final String Poster;

    public Movie(String title, String year, String poster) {
        this.Title = title;
        this.Year = year;
        this.Poster = poster;
    }
}
