package cs152;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Recommendation engine that generates a genre-diverse discovery playlist.
 *
 * Filters songs based on the user's preferences, places matching
 * songs into genre buckets, sorts each bucket by score, and rotates through
 * the buckets to create a more varied playlist. It also limits songs from
 * repeated artists to 1
 *
 * @author Timmy Vo
 */
public class RecommendationEngine1 {
    private final Filter filter;

    /**
     * Constructs a RecommendationEngine1 object with a Filter.
     */
    public RecommendationEngine1() {
        filter = new Filter();
    }

    /**
     * Generates a playlist using the user's preferences.
     *
     * @param library, the music library containing available songs
     * @param prefs, the user's playlist preferences
     * @return an ArrayList of scored songs representing the generated playlist
     */
    public ArrayList<ScoredSong> generatePlaylist(MusicLibrary library, UserPreferences prefs) {
        ArrayList<Song> filteredSongs = filter.filterSongs(library, prefs);
        ArrayList<ScoredSong> finalPlaylist = new ArrayList<ScoredSong>();

        ArrayList<ArrayList<ScoredSong>> genreBuckets = new ArrayList<ArrayList<ScoredSong>>();
        ArrayList<Integer> bucketIndexes = new ArrayList<Integer>();

        for (int i = 0; i < prefs.getPreferredGenres().size(); i++) {
            genreBuckets.add(new ArrayList<ScoredSong>());
            bucketIndexes.add(0);
        }

        for (Song song : filteredSongs) {
            int matchedGenres = countMatchedPreferredGenres(song, prefs);

            if (!filter.hasPreferences(prefs) || matchedGenres > 0) {
                int genreScore = calculateDiscoveryGenreScore(song, prefs);
                int artistScore = calculateDiscoveryArtistScore(song, prefs);
                int totalScore = genreScore + artistScore;

                if (totalScore > 0) {
                    ScoredSong scoredSong = new ScoredSong(song, genreScore, artistScore, 0);

                    for (int i = 0; i < prefs.getPreferredGenres().size(); i++) {
                        String genre = prefs.getPreferredGenres().get(i);

                        if (filter.containsGenre(song.getGenre(), genre)) {
                            if (!alreadyAdded(genreBuckets.get(i), song)) {
                                genreBuckets.get(i).add(scoredSong);
                            }
                        }
                    }
                }
            }
        }

        for (ArrayList<ScoredSong> bucket : genreBuckets) {
            sortDiscoveryRecommendations(bucket);
        }

        while (finalPlaylist.size() < prefs.getDesiredPlaylistLength()
                && stillHasAvailableSongs(genreBuckets, bucketIndexes)) {

            for (int i = 0; i < genreBuckets.size(); i++) {
                ArrayList<ScoredSong> bucket = genreBuckets.get(i);
                int currentIndex = bucketIndexes.get(i);
                boolean addedSong = false;

                while (currentIndex < bucket.size() && !addedSong) {
                    ScoredSong scoredSong = bucket.get(currentIndex);
                    Song song = scoredSong.getSong();

                    if (!alreadyAdded(finalPlaylist, song)
                            && countArtist(finalPlaylist, song.getArtist()) < 2) {
                        finalPlaylist.add(scoredSong);
                        addedSong = true;
                    }

                    currentIndex++;
                }

                bucketIndexes.set(i, currentIndex);

                if (finalPlaylist.size() == prefs.getDesiredPlaylistLength()) {
                    return finalPlaylist;
                }
            }
        }

        return finalPlaylist;
    }

    /**
     * Calculates a discovery genre score for a song.
     *
     * Songs receive more points for exact genre matches, partial genre matches,
     * matching multiple preferred genres, and having multiple genres listed.
     *
     * @param song, the song being scored
     * @param prefs, the user's playlist preferences
     * @return the calculated genre score
     */
    private int calculateDiscoveryGenreScore(Song song, UserPreferences prefs) {
        int score = 0;
        int matchedGenres = 0;
        int totalPreferredGenres = countPreferredGenres(prefs);

        String songGenre = song.getGenre().toLowerCase().trim();

        for (String genre : prefs.getPreferredGenres()) {
            String preferredGenre = genre.toLowerCase().trim();

            if (preferredGenre.length() > 0) {
                if (songGenre.equals(preferredGenre)) {
                    score = score + 25;
                    matchedGenres++;
                }
                else if (songGenre.contains(preferredGenre)) {
                    score = score + 15;
                    matchedGenres++;
                }
            }
        }

        if (totalPreferredGenres > 1 && matchedGenres == totalPreferredGenres) {
            score = score + 10;
        }

        if (songGenre.contains(",")) {
            score = score + 2;
        }

        return score;
    }

    /**
     * Calculates the discovery artist score for a song.
     *
     * This engine favors discovery by slightly reducing the score for preferred
     * artists and slightly increasing the score for unfamiliar artists.
     *
     * @param song, the song being scored
     * @param prefs, the user's playlist preferences
     * @return the calculated artist score
     */
    private int calculateDiscoveryArtistScore(Song song, UserPreferences prefs) {
        int score = 0;

        if (filter.containsIgnoreCase(prefs.getPreferredArtists(), song.getArtist())) {
            score = score - 10;
        }
        else {
            score = score + 5;
        }

        return score;
    }

    /**
     * Sorts a genre bucket from highest score to lowest score.
     *
     * @param playlist, the genre bucket to sort
     */
    private void sortDiscoveryRecommendations(ArrayList<ScoredSong> playlist) {
        playlist.sort(new Comparator<ScoredSong>() {
            @Override
            public int compare(ScoredSong s1, ScoredSong s2) {
                int scoreCompare = Integer.compare(s2.getTotalScore(), s1.getTotalScore());

                if (scoreCompare != 0) {
                    return scoreCompare;
                }

                int artistCompare = s1.getSong().getArtist().compareToIgnoreCase(s2.getSong().getArtist());

                if (artistCompare != 0) {
                    return artistCompare;
                }

                return s1.getSong().getName().compareToIgnoreCase(s2.getSong().getName());
            }
        });
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
            }
        }

        return found;
    }

    /**
     * Checks whether any genre bucket still has songs available.
     *
     * @param genreBuckets, the list of genre buckets
     * @param bucketIndexes, the current index position for each bucket
     * @return true if at least one bucket still has songs left, false otherwise
     */
    private boolean stillHasAvailableSongs(ArrayList<ArrayList<ScoredSong>> genreBuckets, ArrayList<Integer> bucketIndexes) {
        boolean hasSongs = false;

        for (int i = 0; i < genreBuckets.size(); i++) {
            if (bucketIndexes.get(i) < genreBuckets.get(i).size()) {
                hasSongs = true;
            }
        }

        return hasSongs;
    }

    /**
     * Counts how many preferred genres were entered by the user.
     *
     * @param prefs, the user's playlist preferences
     * @return the number of non-blank preferred genres
     */
    private int countPreferredGenres(UserPreferences prefs) {
        int count = 0;

        for (String genre : prefs.getPreferredGenres()) {
            if (genre.trim().length() > 0) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts how many of the user's preferred genres match a song.
     *
     * @param song, the song being checked
     * @param prefs, the user's playlist preferences
     * @return the number of preferred genres that match the song
     */
    private int countMatchedPreferredGenres(Song song, UserPreferences prefs) {
        int count = 0;

        for (String genre : prefs.getPreferredGenres()) {
            if (filter.containsGenre(song.getGenre(), genre)) {
                count++;
            }
        }

        return count;
    }
}