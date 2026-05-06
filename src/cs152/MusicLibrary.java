package cs152;

/**
 * Stores a collection of Song objects. To be used by Recommendation Engines and also catalog size
 *
 * @author Timmy Vo
 */
import java.util.ArrayList;

public class MusicLibrary {
    private ArrayList<Song> songs;

    /**
     * Constructs an empty MusicLibrary.
     */
    public MusicLibrary() { songs = new ArrayList<Song>(); }
    public MusicLibrary(ArrayList<Song> songs) { this.songs = songs;}

    /**
     * Returns the list of songs in the music library.
     *
     * @return an ArrayList containing the stored Song objects
     */
    public ArrayList<Song> getSongs() { return songs; }
    public int size() { return songs.size(); }

}
