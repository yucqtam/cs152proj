package cs152;

import java.util.ArrayList;

/**
 * Filters songs based on the user's playlist preferences.
 *
 * The Filter class removes songs that should not appear in the generated
 * playlist based on user-preferences and exclusions
 *
 * @author Timmy Vo
 */
public class Filter {

    /**
     * Returns a list of songs that are not excluded by the user's preferences.
     *
     * @param library, the music library containing all available songs
     * @param prefs, the user's playlist preferences
     * @return an ArrayList of songs that pass the filter
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
     * Determines whether a song should be excluded from the playlist.
     *
     * @param song, the song being checked
     * @param prefs, the user's playlist preferences
     * @return true if the song should be excluded, false otherwise
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
     * Checks whether the user entered at least one preferred genre or artist.
     *
     * @param prefs, the user's playlist preferences
     * @return true if the user has at least one preferred genre or artist, false otherwise
     */
    public boolean hasPreferences(UserPreferences prefs) {
        return !prefs.getPreferredGenres().isEmpty() ||
                !prefs.getPreferredArtists().isEmpty();
    }

    /**
     * Checks whether a list contains a target string, ignoring case.
     *
     * @param list, the list of strings to search
     * @param target, the target string to find
     * @return true if the list contains the target string, false otherwise
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
     * Checks whether a song's genre field contains any genre from a target list.
     *
     * @param songGenreField, the genre field from a song
     * @param targetGenres, the list of genres to search for
     * @return true if the song genre field contains a target genre, false otherwise
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

    /**
     * Checks whether a song's genre field contains one target genre.
     *
     * @param songGenreField, the genre field from a song
     * @param targetGenre, the genre to search for
     * @return true if the song genre field contains the target genre, false otherwise
     */
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