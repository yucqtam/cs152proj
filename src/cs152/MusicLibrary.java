package cs152;

/**
 * Stores Song objects into a collection
 *
 * @author Timmy Vo
 */
import java.util.ArrayList;

public class MusicLibrary {
    private ArrayList<Song> songs;

    public MusicLibrary() { // Empty music library
        songs = new ArrayList<Song>();
    }

    public MusicLibrary(ArrayList<Song> songs) {    // creates a music library with list of songs
        this.songs = songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public int size() {
        return songs.size();
    }

    public void printSongs() {
        for (Song song : songs) {
            System.out.println(song);
        }
    }
}
