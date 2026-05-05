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

    // Constructor to initialize a Song object.
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

    // NAME - ARTIST - GENRE(S) - EXPLICIT/CLEAN format
    @Override
    public String toString() {
        return name + " - " + artist + " (" + genre + ") " +
               (explicit ? "[Explicit]" : "[Clean]");
    }
}