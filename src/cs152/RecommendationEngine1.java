package cs152;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Recommendation Engine 1
 *
 * filters songs
 * creates a bucket/list for each preferred genre.
 * score matching song and put it in correct genre bucket
 * sort each bucket by score
 * pulls 1 song from each bucket in a rotation
 * tries to limit repeat artists
 */
public class RecommendationEngine1 {
    private final Filter filter;

    public RecommendationEngine1() {
        filter = new Filter();
    }

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
                    ScoredSong scoredSong = new ScoredSong(song, genreScore, artistScore);

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
            }
        }

        return found;
    }

    private boolean stillHasAvailableSongs(ArrayList<ArrayList<ScoredSong>> genreBuckets, ArrayList<Integer> bucketIndexes) {
        boolean hasSongs = false;

        for (int i = 0; i < genreBuckets.size(); i++) {
            if (bucketIndexes.get(i) < genreBuckets.get(i).size()) {
                hasSongs = true;
            }
        }

        return hasSongs;
    }

    private int countPreferredGenres(UserPreferences prefs) {
        int count = 0;

        for (String genre : prefs.getPreferredGenres()) {
            if (genre.trim().length() > 0) {
                count++;
            }
        }

        return count;
    }

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