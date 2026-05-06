package cs152.controller;

import cs152.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * PlaylistController
 *
 * Bridges the GUI (view) with the backend logic.
 * Loads the music library once on construction, then generates
 * playlists on demand whenever the user clicks "Generate".
 */
public class PlaylistController {

    private MusicLibrary library;
    private boolean libraryLoaded = false;

    private final RecommendationEngine1 engine1 = new RecommendationEngine1();
    private final RecommendationEngine2 engine2 = new RecommendationEngine2();

    /** Path to the dataset CSV relative to the project root. */
    private static final String DATASET_PATH = "dataset/musicDataset.csv";

    public PlaylistController() {
        loadLibrary();
    }

    // ---------------------------------------------------------------
    // Library loading
    // ---------------------------------------------------------------

    private void loadLibrary() {
        SongLoader loader = new SongLoader();
        try {
            ArrayList<Song> songs = loader.loadSongs(DATASET_PATH);
            library = new MusicLibrary(songs);
            libraryLoaded = true;
        } catch (IOException e) {
            library = new MusicLibrary();
            libraryLoaded = false;
        }
    }

    public boolean isLibraryLoaded() {
        return libraryLoaded;
    }

    public int getCatalogSize() {
        return library.size();
    }

    // ---------------------------------------------------------------
    // Playlist generation
    // ---------------------------------------------------------------

    /**
     * Generates a playlist using the specified engine (1 or 2).
     *
     * @param preferredGenres   comma-separated string of genres to prefer
     * @param preferredArtists  comma-separated string of artists to prefer
     * @param excludedGenres    comma-separated string of genres to exclude
     * @param excludedArtists   comma-separated string of artists to exclude
     * @param allowExplicit     whether explicit songs are allowed
     * @param playlistLength    max number of songs in the result
     * @param engineNumber      1 = Discovery Engine, 2 = Top Picks Engine
     * @return list of ScoredSong results
     */
    public ArrayList<ScoredSong> generatePlaylist(
            String preferredGenres,
            String preferredArtists,
            String excludedGenres,
            String excludedArtists,
            boolean allowExplicit,
            int playlistLength,
            int engineNumber) {

        UserPreferences prefs = buildPreferences(
                preferredGenres, preferredArtists,
                excludedGenres, excludedArtists,
                allowExplicit, playlistLength);

        if (engineNumber == 1) {
            return engine1.generatePlaylist(library, prefs);
        } else {
            return engine2.generatePlaylist(library, prefs);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private UserPreferences buildPreferences(
            String preferredGenres,
            String preferredArtists,
            String excludedGenres,
            String excludedArtists,
            boolean allowExplicit,
            int playlistLength) {

        UserPreferences prefs = new UserPreferences();
        prefs.setAllowExplicit(allowExplicit);
        prefs.setDesiredPlaylistLength(playlistLength);

        for (String g : splitInput(preferredGenres)) {
            prefs.addPreferredGenre(g);
        }
        for (String a : splitInput(preferredArtists)) {
            prefs.addPreferredArtist(a);
        }
        for (String g : splitInput(excludedGenres)) {
            prefs.addExcludedGenre(g);
        }
        for (String a : splitInput(excludedArtists)) {
            prefs.addExcludedArtist(a);
        }

        return prefs;
    }

    /** Splits a comma-separated input string, trimming whitespace and skipping blanks. */
    private ArrayList<String> splitInput(String raw) {
        ArrayList<String> result = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) return result;
        for (String item : raw.split(",")) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    public void recordFeedback(Song song, boolean liked) {
        engine2.recordFeedback(song, liked);
    }
}
