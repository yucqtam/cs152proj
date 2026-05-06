package cs152;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Recommendation Engine 2
 *
 * Scores songs based on preferred genres, preferred artists,
 * and user feedback from liked or disliked songs. It shuffles matching songs
 * before sorting so that songs with the same score can appear in a different
 * order. It also limits the final playlist to one song per artist.
 *
 * @author Timmy Vo
 */
public class RecommendationEngine2 {
    private final Filter filter;

    private ArrayList<String> likedArtists = new ArrayList<String>();
    private ArrayList<String> skippedArtists = new ArrayList<String>();
    private ArrayList<String> likedGenres = new ArrayList<String>();
    private ArrayList<String> skippedGenres = new ArrayList<String>();

    /**
     * Constructs a RecommendationEngine2 object with a Filter.
     */
    public RecommendationEngine2() {
        filter = new Filter();
    }

    /**
     * Records user feedback for a song. Like/Dislike.
     *
     * @param song, the song the user gave feedback on
     * @param liked, true if the song was liked, false if it was disliked
     */
    public void recordFeedback(Song song, boolean liked) {
        if (liked) {
            addIfMissing(likedArtists, song.getArtist());
            addGenres(likedGenres, song.getGenre());
        }
        else {
            addIfMissing(skippedArtists, song.getArtist());
            addGenres(skippedGenres, song.getGenre());
        }
    }

    /**
     * Adds an item to a list if it is not already in the list. Safety net
     *
     * @param list, the list to add the item to
     * @param item, the item being added
     */
    private void addIfMissing(ArrayList<String> list, String item) {
        boolean found = false;

        if (item != null) {
            String cleanedItem = item.toLowerCase().trim();

            for (String current : list) {
                if (current.equalsIgnoreCase(cleanedItem)) {
                    found = true;
                }
            }

            if (!found && !cleanedItem.isEmpty()) {
                list.add(cleanedItem);
            }
        }
    }

    /**
     * Splits a song's genre field and adds each genre to the given list.
     *
     * @param list, the list that stores liked or skipped genres
     * @param genreField, the comma-separated genre field from a song
     */
    private void addGenres(ArrayList<String> list, String genreField) {
        if (genreField != null) {
            String[] genres = genreField.split(",");

            for (String genre : genres) {
                addIfMissing(list, genre);
            }
        }
    }

    /**
     * Calculates the feedback score for a song.
     *
     * Bonus points for matching liked artists or genres.
     * Lose points for matching skipped artists or genres.
     *
     * @param song, the song being scored
     * @return the feedback score for the song
     */
    private int calculateFeedbackScore(Song song) {
        int score = 0;

        if (filter.containsIgnoreCase(likedArtists, song.getArtist())) {
            score = score + 10;
        }

        if (filter.containsIgnoreCase(skippedArtists, song.getArtist())) {
            score = score - 10;
        }

        if (filter.containsGenre(song.getGenre(), likedGenres)) {
            score = score + 5;
        }

        if (filter.containsGenre(song.getGenre(), skippedGenres)) {
            score = score - 5;
        }

        return score;
    }

    /**
     * Generates a playlist using user preferences and stored feedback.
     *
     * @param library, the music library containing available songs
     * @param prefs, the user's playlist preferences
     * @return an ArrayList of scored songs representing the generated playlist
     */
    public ArrayList<ScoredSong> generatePlaylist(MusicLibrary library, UserPreferences prefs) {
        ArrayList<Song> filteredSongs = filter.filterSongs(library, prefs);
        ArrayList<ScoredSong> playlist = new ArrayList<ScoredSong>();

        for (Song song : filteredSongs) {
            int genreScore = calculateGenreScore(song, prefs);
            int artistScore = calculateArtistScore(song, prefs);
            int feedbackScore = calculateFeedbackScore(song);
            int totalScore = genreScore + artistScore + feedbackScore;

            if (!filter.hasPreferences(prefs) || totalScore > 0) {
                if (!alreadyAdded(playlist, song)) {
                    ScoredSong scoredSong = new ScoredSong(song, genreScore, artistScore, feedbackScore);
                    playlist.add(scoredSong);
                }
            }
        }

        Collections.shuffle(playlist);
        sortRecommendations(playlist);

        return limitSongsPerArtist(playlist, prefs.getDesiredPlaylistLength());
    }

    /**
     * Calculates the genre score for a song.
     *
     * @param song, the song being scored
     * @param prefs, the user's playlist preferences
     * @return 10 if the song matches a preferred genre, otherwise 0
     */
    private int calculateGenreScore(Song song, UserPreferences prefs) {
        int score = 0;

        if (filter.containsGenre(song.getGenre(), prefs.getPreferredGenres())) {
            score = 10;
        }

        return score;
    }

    /**
     * Calculates the artist score for a song.
     *
     * @param song, the song being scored
     * @param prefs, the user's playlist preferences
     * @return 10 if the song matches a preferred artist, otherwise 0
     */
    private int calculateArtistScore(Song song, UserPreferences prefs) {
        int score = 0;

        if (filter.containsIgnoreCase(prefs.getPreferredArtists(), song.getArtist())) {
            score = 10;
        }

        return score;
    }

    /**
     * Sorts the playlist from highest total score to lowest total score.
     *
     * @param playlist, the playlist to sort
     */
    private void sortRecommendations(ArrayList<ScoredSong> playlist) {
        playlist.sort(new Comparator<ScoredSong>() {
            @Override
            public int compare(ScoredSong s1, ScoredSong s2) {
                return Integer.compare(s2.getTotalScore(), s1.getTotalScore());
            }
        });
    }

    /**
     * Limits the final playlist so each artist appears at most once.
     *
     * @param playlist, the sorted playlist before artist limiting
     * @param limit, the maximum number of songs to return
     * @return a limited playlist with no repeated artists
     */
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

    /**
     * Counts how many times an artist appears in a playlist.
     *
     * @param playlist, the playlist being checked
     * @param artist, the artist name to count
     * @return the number of songs by the given artist
     */
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

    /**
     * Checks whether a song has already been added to a playlist.
     *
     * A song is considered already added if both the song name and artist match.
     *
     * @param playlist, the playlist being checked
     * @param song, the song to search for
     * @return true if the song is already in the playlist, false otherwise
     */
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