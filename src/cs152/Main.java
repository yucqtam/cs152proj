package cs152;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        SongLoader loader = new SongLoader();

        try {
            ArrayList<Song> songs = loader.loadSongs("dataset/musicDataset.csv"); // may change when uploaded to github

            for (int i = 0; i < 10 && i < songs.size(); i++) {		// remove later. test to check if it can scan csv file
                System.out.println(songs.get(i));
            }
            System.out.println();

            MusicLibrary library = new MusicLibrary(songs);
            System.out.println("catalog size: " + library.size());
            System.out.println();

            for (int i = 0; i < 10 && i < library.getSongs().size(); i++) { //  test to compare if same array as when first reading csv file
                System.out.println(library.getSongs().get(i));
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }
}