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
        }
        catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }
}