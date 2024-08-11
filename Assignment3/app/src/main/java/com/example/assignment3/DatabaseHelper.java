package com.example.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITES = "favorites";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_POSTER = "poster";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating table: " + TABLE_FAVORITES);
        String createTable = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_YEAR + " TEXT, " +
                COLUMN_POSTER + " TEXT)";
        try {
            db.execSQL(createTable);
            Log.d(TAG, "Table created successfully");
        } catch (SQLException e) {
            Log.e(TAG, "Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
            onCreate(db);
            Log.d(TAG, "Database upgraded successfully");
        } catch (SQLException e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        }
    }

    public void addFavorite(Movie movie) {
        Log.d(TAG, "Adding movie to favorites: " + movie.getTitle());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_YEAR, movie.getYear());
        values.put(COLUMN_POSTER, movie.getPoster());

        try {
            long result = db.insert(TABLE_FAVORITES, null, values);
            if (result == -1) {
                Log.e(TAG, "Failed to add movie to favorites");
            } else {
                Log.d(TAG, "Movie added to favorites successfully with ID: " + result);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding movie to favorites: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    public List<Movie> getAllFavourites() {
        Log.d(TAG, "Fetching all favorite movies");
        List<Movie> favorites = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(TABLE_FAVORITES, null, null, null, null, null, null)) {

            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int yearIndex = cursor.getColumnIndex(COLUMN_YEAR);
            int posterIndex = cursor.getColumnIndex(COLUMN_POSTER);

            // Log the indices to ensure they are valid
            Log.d(TAG, "titleIndex: " + titleIndex + ", yearIndex: " + yearIndex + ", posterIndex: " + posterIndex);

            if (titleIndex == -1 || yearIndex == -1 || posterIndex == -1) {
                Log.e(TAG, "Error: One or more columns not found in the cursor");
                return favorites; // Return an empty list
            }

            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(
                            cursor.getString(titleIndex),
                            cursor.getString(yearIndex),
                            cursor.getString(posterIndex)
                    );
                    favorites.add(movie);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Number of favorite movies fetched: " + favorites.size());
        } catch (SQLException e) {
            Log.e(TAG, "Error fetching favorite movies: " + e.getMessage());
        }
        return favorites;
    }


    public void removeFavourite(String title) {
        Log.d(TAG, "Removing movie from favorites: " + title);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            int rowsAffected = db.delete(TABLE_FAVORITES, COLUMN_TITLE + "=?", new String[]{title});
            if (rowsAffected == 0) {
                Log.e(TAG, "Failed to remove movie from favorites");
            } else {
                Log.d(TAG, "Movie removed from favorites successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error removing movie from favorites: " + e.getMessage());
        }
    }
}
