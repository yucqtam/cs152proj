package cs152;

/*
    wraps a score around a score object
*/
public class ScoredSong {
    private Song song;
    private int genreScore;
    private int artistScore;
    private int totalScore;

    
      //Constructs a scored song with score breakdown.
     
    public ScoredSong(Song song, int genreScore, int artistScore) {
        this.song = song;
        this.genreScore = genreScore;
        this.artistScore = artistScore;
        this.totalScore = genreScore + artistScore;
    }

    public Song getSong() {
        return song;
    }

    public int getGenreScore() {
        return genreScore;
    }

    public int getArtistScore() {
        return artistScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setGenreScore(int genreScore) {
        this.genreScore = genreScore;
        this.totalScore = this.genreScore + this.artistScore;
    }

    public void setArtistScore(int artistScore) {
        this.artistScore = artistScore;
        this.totalScore = this.genreScore + this.artistScore;
    }

    @Override
    public String toString() {
        return song.toString() +
                " | genreScore=" + genreScore +
                ", artistScore=" + artistScore +
                ", totalScore=" + totalScore;
    }
}