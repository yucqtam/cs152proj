package cs152;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Generates recommended songs from a MusicLibrary
 * based on UserPreferences.
 *
 * current arbitrary scoring rules (likely to change later)
 * - matching the preferred genre : +10
 * - matching the preferred artist : +15
 */
public class RecommendationEngine {

    public ArrayList<ScoredSong> generateRecommendations(MusicLibrary library, UserPreferences prefs) {
        ArrayList<ScoredSong> recommendations = new ArrayList<ScoredSong>();

        for (Song song : library.getSongs()) {
            if (shouldExclude(song, prefs)) {
                continue;
            }

            int genreScore = calculateGenreScore(song, prefs);
            int artistScore = calculateArtistScore(song, prefs);

            ScoredSong scoredSong = new ScoredSong(song, genreScore, artistScore);
            recommendations.add(scoredSong);
        }

        sortRecommendations(recommendations);

        int limit = prefs.getDesiredPlaylistLength();
        if (limit < recommendations.size()) {
            return new ArrayList<ScoredSong>(recommendations.subList(0, limit));
        }

        return recommendations;
    }

    /**
     * Determines whether a song should be excluded.
     */
    private boolean shouldExclude(Song song, UserPreferences prefs) {
        String artist = song.getArtist().toLowerCase().trim();
        String genre = song.getGenre().toLowerCase().trim();

        if (!prefs.isAllowExplicit() && song.isExplicit()) {
            return true;
        }

        if (containsIgnoreCase(prefs.getExcludedArtists(), artist)) {
            return true;
        }

        if (containsGenre(song.getGenre(), prefs.getExcludedGenres())) {
            return true;
        }

        return false;
    }

    /**
     * Calculates score contribution from genre preferences.
     */
    private int calculateGenreScore(Song song, UserPreferences prefs) {
        if (containsGenre(song.getGenre(), prefs.getPreferredGenres())) {
            return 10;
        }
        return 0;
    }

    /**
     * Calculates score contribution from artist preferences.
     */
    private int calculateArtistScore(Song song, UserPreferences prefs) {
        String artist = song.getArtist().toLowerCase().trim();

        if (containsIgnoreCase(prefs.getPreferredArtists(), artist)) {
            return 15;
        }
        return 0;
    }

    /**
     * Sorts scored songs from highest score to lowest score.
     */
    private void sortRecommendations(ArrayList<ScoredSong> recommendations) {
        Collections.sort(recommendations, new Comparator<ScoredSong>() {
            @Override
            public int compare(ScoredSong s1, ScoredSong s2) {
                return Integer.compare(s2.getTotalScore(), s1.getTotalScore());
            }
        });
    }

    /**
     * Checks if a list contains a string ignoring case.
     */
    private boolean containsIgnoreCase(ArrayList<String> list, String target) {
        for (String item : list) {
            if (item.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a song's genre field contains any preferred/excluded genre.
     */
    private boolean containsGenre(String songGenreField, ArrayList<String> targetGenres) {
        String cleanedGenreField = songGenreField.toLowerCase();

        for (String genre : targetGenres) {
            String cleanedTarget = genre.toLowerCase().trim();
            if (cleanedGenreField.contains(cleanedTarget)) {
                return true;
            }
        }

        return false;
    }
}
