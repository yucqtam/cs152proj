package cs152;
/**
 * Song objects with track name, artist, genre(s), and whether it's explicit or not
 * 
 * @author Timmy Vo
 */
public class Song {
    private String name;
    private String artist;
    private String genre;
    private boolean explicit;

    /**
     * Contructs a Song object with the given track information.
     *
     * @param name, the name of the track
     * @param artist, the artist who performed the track
     * @param genre, the genre or genres associated with the track
     * @param explicit, true if the track contains explicit content, false otherwise
     */
    public Song(String name, String artist, String genre, boolean explicit) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.explicit = explicit;
    }

    // Getters
    public String getName() { return name; }
    public String getArtist() {return artist; }
    public String getGenre() { return genre; }
    public boolean isExplicit() { return explicit; }

}