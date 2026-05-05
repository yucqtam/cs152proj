package cs152;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Recommendation Engine 2
 *
 * filters
 * scores songs but genre and artist weight are the same
 * adds matching songs to a temp playlist.
 * shuffles the temp playlist
 * sorts by score, but limits 1 song per artist so no repeats
 */
public class RecommendationEngine2 {
    private final Filter filter;

    public RecommendationEngine2() {
        filter = new Filter();
    }

    public ArrayList<ScoredSong> generatePlaylist(MusicLibrary library, UserPreferences prefs) {
        ArrayList<Song> filteredSongs = filter.filterSongs(library, prefs);
        ArrayList<ScoredSong> playlist = new ArrayList<ScoredSong>();

        for (Song song : filteredSongs) {
            int genreScore = calculateGenreScore(song, prefs);
            int artistScore = calculateArtistScore(song, prefs);
            int totalScore = genreScore + artistScore;

            if (!filter.hasPreferences(prefs) || totalScore > 0) {
                if (!alreadyAdded(playlist, song)) {
                    ScoredSong scoredSong = new ScoredSong(song, genreScore, artistScore);
                    playlist.add(scoredSong);
                }
            }
        }

        Collections.shuffle(playlist);
        sortRecommendations(playlist);

        return limitSongsPerArtist(playlist, prefs.getDesiredPlaylistLength());
    }

    private int calculateGenreScore(Song song, UserPreferences prefs) {
        int score = 0;

        if (filter.containsGenre(song.getGenre(), prefs.getPreferredGenres())) {
            score = 10;
        }

        return score;
    }

    private int calculateArtistScore(Song song, UserPreferences prefs) {
        int score = 0;

        if (filter.containsIgnoreCase(prefs.getPreferredArtists(), song.getArtist())) {
            score = 10;
        }

        return score;
    }

    private void sortRecommendations(ArrayList<ScoredSong> playlist) {
        playlist.sort(new Comparator<ScoredSong>() {
            @Override
            public int compare(ScoredSong s1, ScoredSong s2) {
                return Integer.compare(s2.getTotalScore(), s1.getTotalScore());
            }
        });
    }

    private ArrayList<ScoredSong> limitSongsPerArtist(ArrayList<ScoredSong> playlist, int limit) {
        ArrayList<ScoredSong> limitedPlaylist = new ArrayList<ScoredSong>();

        for (ScoredSong scoredSong : playlist) {
            String artist = scoredSong.getSong().getArtist();

            if (countArtist(limitedPlaylist, artist) < 1) {
                limitedPlaylist.add(scoredSong);
            }

            if (limitedPlaylist.size() == limit) {
                return limitedPlaylist;
            }
        }

        return limitedPlaylist;
    }

    private int countArtist(ArrayList<ScoredSong> playlist, String artist) {
        int count = 0;

        for (ScoredSong scoredSong : playlist) {
            String currentArtist = scoredSong.getSong().getArtist();

            if (currentArtist.equalsIgnoreCase(artist)) {
                count++;
            }
        }

        return count;
    }

    private boolean alreadyAdded(ArrayList<ScoredSong> playlist, Song song) {
        boolean found = false;

        for (ScoredSong scoredSong : playlist) {
            Song currentSong = scoredSong.getSong();

            boolean sameName = currentSong.getName().equalsIgnoreCase(song.getName());
            boolean sameArtist = currentSong.getArtist().equalsIgnoreCase(song.getArtist());

            if (sameName && sameArtist) {
                found = true;
                break;
            }
        }

        return found;
    }
}