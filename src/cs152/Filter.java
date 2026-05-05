package cs152;

import java.util.ArrayList;

public class Filter {

    /**
     * Returns songs that aren't filtered out
     */
    public ArrayList<Song> filterSongs(MusicLibrary library, UserPreferences prefs) {
        ArrayList<Song> filteredSongs = new ArrayList<Song>();

        for (Song song : library.getSongs()) {
            if (!shouldExclude(song, prefs)) {
                filteredSongs.add(song);
            }
        }

        return filteredSongs;
    }

    /**
     * Determines a song's exclusion
     * - it has no genre field
     * - it is explicit and the user does not allow explicit songs
     * - its artist is blocked
     * - its genre is blocked
     */
    private boolean shouldExclude(Song song, UserPreferences prefs) {
        boolean exclude = false;

        if (song.getGenre() == null || song.getGenre().trim().isEmpty()) {
            exclude = true;
        }
        else if (!prefs.isAllowExplicit() && song.isExplicit()) {
            exclude = true;
        }
        else if (containsIgnoreCase(prefs.getExcludedArtists(), song.getArtist())) {
            exclude = true;
        }
        else if (containsGenre(song.getGenre(), prefs.getExcludedGenres())) {
            exclude = true;
        }

        return exclude;
    }

    /**
     * Checks whether the user selected at least one preferred genre or artist.
     */
    public boolean hasPreferences(UserPreferences prefs) {
        return !prefs.getPreferredGenres().isEmpty() ||
                !prefs.getPreferredArtists().isEmpty();
    }

    /**
     * checks if a list contains a specific string
     */
    public boolean containsIgnoreCase(ArrayList<String> list, String target) {
        boolean found = false;

        if (target != null) {
            String cleanedTarget = target.toLowerCase().trim();

            for (String item : list) {
                String cleanedItem = item.toLowerCase().trim();

                if (!cleanedItem.isEmpty() && cleanedItem.equals(cleanedTarget)) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Checks whether a song's genre field contains a target genre.
     */
    public boolean containsGenre(String songGenreField, ArrayList<String> targetGenres) {
        boolean found = false;

        if (songGenreField != null && !songGenreField.trim().isEmpty()) {
            String cleanedGenreField = songGenreField.toLowerCase().trim();

            for (String genre : targetGenres) {
                String cleanedTarget = genre.toLowerCase().trim();

                if (!cleanedTarget.isEmpty() && cleanedGenreField.contains(cleanedTarget)) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean containsGenre(String songGenreField, String targetGenre) {
        boolean found = false;

        if (songGenreField != null && targetGenre != null &&
                !songGenreField.trim().isEmpty()) {

            String cleanedGenreField = songGenreField.toLowerCase().trim();
            String cleanedTarget = targetGenre.toLowerCase().trim();

            if (!cleanedTarget.isEmpty() && cleanedGenreField.contains(cleanedTarget)) {
                found = true;
            }
        }

        return found;
    }
}