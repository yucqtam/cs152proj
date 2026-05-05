package cs152;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        SongLoader loader = new SongLoader();

        try {
            ArrayList<Song> songs = loader.loadSongs("dataset/musicDataset.csv");

            MusicLibrary library = new MusicLibrary(songs);
            UserPreferences prefs = new UserPreferences();

            // preference sample
            prefs.addPreferredGenre("pop");
            prefs.addPreferredGenre("christmas");

            prefs.addPreferredArtist("Taylor Swift");
            prefs.addPreferredArtist("The Weeknd");
            prefs.addPreferredArtist("Rihanna");

            // exclude sample
            prefs.addExcludedArtist("Central Cee");
            prefs.addExcludedArtist("Selena Gomez");
            prefs.addExcludedGenre("stutter house");

            // explicity and length preference
            prefs.setAllowExplicit(false);
            prefs.setDesiredPlaylistLength(10);

            RecommendationEngine1 engine1 = new RecommendationEngine1();
            RecommendationEngine2 engine2 = new RecommendationEngine2();

            ArrayList<ScoredSong> playlist1 = engine1.generatePlaylist(library, prefs);
            ArrayList<ScoredSong> playlist2 = engine2.generatePlaylist(library, prefs);

            System.out.println("Catalog size: " + library.size());
            System.out.println();

            System.out.println("Generated Playlist 1");
            System.out.println("------------------------------------------");

            for (ScoredSong scoredSong : playlist1) {
                System.out.println(scoredSong);
            }

            System.out.println();

            System.out.println("Generated Playlist 2");
            System.out.println("-------------------------------------------");

            for (ScoredSong scoredSong : playlist2) {
                System.out.println(scoredSong);
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }
}