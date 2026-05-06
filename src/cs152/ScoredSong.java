package cs152;

/**
 * Creates a ScoredSong object with the Song object + score calculated by recommendation engine
 * to use in playlist generation
 *
 * @author Timmy Vo
 */
public class ScoredSong {
    private Song song;
    private int genreScore;
    private int artistScore;
    private int feedbackScore;
    private int totalScore;

    /**
     * Constructs a ScoredSong object using a song and its score breakdown.
     *
     * @param song, the song being scored
     * @param genreScore, the score based on matching genres
     * @param artistScore, the score based on matching artists
     * @param feedbackScore, the score based on user like/dislike feedback
     */
    public ScoredSong(Song song, int genreScore, int artistScore, int feedbackScore) {
        this.song = song;
        this.genreScore = genreScore;
        this.artistScore = artistScore;
        this.feedbackScore = feedbackScore;
        this.totalScore = genreScore + artistScore + feedbackScore;
    }

    // getter
    public Song getSong() {
        return song;
    }

    // getter
    public int getTotalScore() {
        return totalScore;
    }
}