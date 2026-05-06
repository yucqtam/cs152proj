package cs152;

import java.util.ArrayList;

/**
 * Stores user playlist preferences.
 *
 * Current supported preferences:
 * - preferred genres
 * - excluded genres
 * - preferred artists
 * - excluded artists
 * - whether explicit songs are allowed
 * - desired playlist length
 *
 * @author Yogi C.
 */
public class UserPreferences {
    private ArrayList<String> preferredGenres;
    private ArrayList<String> excludedGenres;
    private ArrayList<String> preferredArtists;
    private ArrayList<String> excludedArtists;
    private boolean allowExplicit;
    private int desiredPlaylistLength;

    
    public UserPreferences() {
        preferredGenres = new ArrayList<String>();
        excludedGenres = new ArrayList<String>();
        preferredArtists = new ArrayList<String>();
        excludedArtists = new ArrayList<String>();
        allowExplicit = true;
        desiredPlaylistLength = 10;
    }

    public ArrayList<String> getPreferredGenres() {
        return preferredGenres;
    }

    public ArrayList<String> getExcludedGenres() {
        return excludedGenres;
    }

    public ArrayList<String> getPreferredArtists() {
        return preferredArtists;
    }

    public ArrayList<String> getExcludedArtists() {
        return excludedArtists;
    }

    public boolean isAllowExplicit() {
        return allowExplicit;
    }

    public int getDesiredPlaylistLength() {
        return desiredPlaylistLength;
    }

    public void setAllowExplicit(boolean allowExplicit) {
        this.allowExplicit = allowExplicit;
    }

    public void setDesiredPlaylistLength(int desiredPlaylistLength) {
        this.desiredPlaylistLength = desiredPlaylistLength;
    }

    public void addPreferredGenre(String genre) {
        preferredGenres.add(genre.toLowerCase().trim());
    }

    public void addExcludedGenre(String genre) {
        excludedGenres.add(genre.toLowerCase().trim());
    }

    public void addPreferredArtist(String artist) {
        preferredArtists.add(artist.toLowerCase().trim());
    }

    public void addExcludedArtist(String artist) {
        excludedArtists.add(artist.toLowerCase().trim());
    }

}