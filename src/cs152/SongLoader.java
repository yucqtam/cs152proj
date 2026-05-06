package cs152;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Loads songs from the musicDataset csv file with key attributes.
 *
 * @author Timmy Vo
 */
public class SongLoader {
    /**
     * Reads and stores songs from a csv file into an ArrayList.
     * 
     * @param fileName the csv file name
     * @return list of Song objects
     * @throws IOException if file cannot be read
     */
    public ArrayList<Song> loadSongs(String fileName) throws IOException {
        ArrayList<Song> songs = new ArrayList<Song>();
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String line = input.readLine(); 

        while ((line = input.readLine()) != null) {
        	String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // 

            if (parts.length > 9) {	// checks that the row has enough columns before accessing index 9.
            	String name = parts[1];	// track name
            	boolean isExplicit = Boolean.parseBoolean(parts[5]);
            	String artist = parts[6];	// artist name
            	String genre = parts[9];	// genre(s)

            	genre = genre.replace("[", "").replace("]", "").replace("'", "").trim(); // makes it look cleaner

            	Song song = new Song(name, artist, genre, isExplicit);
            	songs.add(song);
            }
        }
        input.close();
        return songs;
    }
}
