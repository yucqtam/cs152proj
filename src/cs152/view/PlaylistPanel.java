package cs152.view;

import cs152.ScoredSong;
import cs152.Song;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import static cs152.view.PreferencesPanel.*;

/**
 * PlaylistPanel
 *
 * The right-hand panel that displays the generated playlist.
 * Shows an empty / loading state, then renders song cards when results arrive.
 */
public class PlaylistPanel extends JPanel {

    private final JLabel    titleLabel    = new JLabel("Your Playlist");
    private final JLabel    subtitleLabel = new JLabel("Set your preferences and hit Generate");
    private final JLabel    countLabel    = new JLabel("");
    private final JPanel    songList      = new JPanel();
    private final JScrollPane scrollPane;
    private BiConsumer<ScoredSong, Boolean> onFeedback;

    // ─────────────────────────────────────────────────────────────────

    public PlaylistPanel() {
        setBackground(BG);
        setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(28, 32, 16, 32));

        titleLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        countLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        countLabel.setForeground(ACCENT);
        countLabel.setAlignmentX(LEFT_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(countLabel);

        add(header, BorderLayout.NORTH);

        // ── Song list ─────────────────────────────────────────────
        songList.setBackground(BG);
        songList.setLayout(new BoxLayout(songList, BoxLayout.Y_AXIS));
        songList.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));

        scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        showEmptyState();
    }

    // ── Public API ────────────────────────────────────────────────────

    public void setOnFeedback(BiConsumer<ScoredSong, Boolean> onFeedback) {
        this.onFeedback = onFeedback;
    }

    public void showFeedbackMessage(String message) {
        subtitleLabel.setText("Feedback saved — " + message);
    }

    /** Shows a spinner while generation is in progress. */
    public void showLoading() {
        subtitleLabel.setText("Generating…");
        countLabel.setText("");
        songList.removeAll();

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT));
        center.setBackground(BG);
        center.setAlignmentX(LEFT_ALIGNMENT);

        JLabel loadingLabel = new JLabel("⏳  Finding your tracks…");
        loadingLabel.setForeground(MUTED);
        loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        center.add(loadingLabel);

        songList.add(center);
        songList.revalidate();
        songList.repaint();
    }

    /** Populates the list with the given scored songs. */
    public void showPlaylist(ArrayList<ScoredSong> songs, String engineName) {
        songList.removeAll();

        if (songs == null || songs.isEmpty()) {
            subtitleLabel.setText("No songs matched your preferences — try broadening your criteria.");
            countLabel.setText("");
            showEmptyState();
            return;
        }

        subtitleLabel.setText("Generated with " + engineName);
        countLabel.setText(songs.size() + " tracks");

        for (int i = 0; i < songs.size(); i++) {
            ScoredSong ss = songs.get(i);
            songList.add(buildSongCard(i + 1, ss));
            songList.add(Box.createVerticalStrut(8));
        }

        songList.revalidate();
        songList.repaint();
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    /** Shows the catalogue size in the header. */
    public void setCatalogInfo(int size) {
        countLabel.setText("Catalog: " + size + " songs");
    }

    /** Shows a dataset error. */
    public void showError(String message) {
        subtitleLabel.setText("⚠  " + message);
        countLabel.setText("");
        songList.removeAll();
        songList.revalidate();
        songList.repaint();
    }

    // ── Card builder ──────────────────────────────────────────────────

    private JPanel buildSongCard(int index, ScoredSong ss) {
        Song song = ss.getSong();

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(LEFT_ALIGNMENT);

        // ── Track number ──────────────────────────────────────────
        JLabel numLabel = new JLabel(String.format("%02d", index));
        numLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        numLabel.setForeground(MUTED);
        numLabel.setPreferredSize(new Dimension(28, 0));

        // ── Text block ────────────────────────────────────────────
        JPanel textBlock = new JPanel();
        textBlock.setBackground(SURFACE);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(song.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(TEXT);

        titleRow.add(nameLabel);

        if (song.isExplicit()) {
            JLabel explicitBadge = new JLabel("E");
            explicitBadge.setFont(new Font("Monospaced", Font.BOLD, 10));
            explicitBadge.setForeground(new Color(250, 100, 100));
            explicitBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(250, 100, 100), 1),
                    BorderFactory.createEmptyBorder(1, 5, 1, 5)));
            titleRow.add(explicitBadge);
        }

        JLabel artistLabel = new JLabel(song.getArtist());
        artistLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        artistLabel.setForeground(MUTED);
        artistLabel.setAlignmentX(LEFT_ALIGNMENT);

        String genreText = song.getGenre().isEmpty() ? "—" : song.getGenre();
        JLabel genreLabel = new JLabel(truncate(genreText, 60));
        genreLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        genreLabel.setForeground(new Color(90, 90, 110));
        genreLabel.setAlignmentX(LEFT_ALIGNMENT);

        textBlock.add(titleRow);
        textBlock.add(Box.createVerticalStrut(2));
        textBlock.add(artistLabel);
        textBlock.add(Box.createVerticalStrut(2));
        textBlock.add(genreLabel);

        // ── Right side: score + explicit badge ───────────────────
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(SURFACE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel scoreLabel = new JLabel("Score: " + ss.getTotalScore());
        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        scoreLabel.setForeground(ACCENT);
        scoreLabel.setAlignmentX(RIGHT_ALIGNMENT);

        rightPanel.add(scoreLabel);

        JPanel feedbackButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        feedbackButtons.setBackground(SURFACE);

        JButton likeButton = feedbackButton("Like");
        JButton dislikeButton = feedbackButton("Dislike");

        likeButton.addActionListener(e -> {
            if (onFeedback != null) {
                onFeedback.accept(ss, true);
            }
            likeButton.setEnabled(false);
            likeButton.setBackground(new java.awt.Color(34, 197, 94));   // green tint
            likeButton.setForeground(java.awt.Color.WHITE);
            dislikeButton.setEnabled(false);
            dislikeButton.setOpaque(true);
        });
        
        dislikeButton.addActionListener(e -> {
            if (onFeedback != null) {
                onFeedback.accept(ss, false);
            }
            dislikeButton.setEnabled(false);
            dislikeButton.setBackground(new java.awt.Color(239, 68, 68)); // red tint
            dislikeButton.setForeground(java.awt.Color.WHITE);
            likeButton.setEnabled(false);
            likeButton.setOpaque(true);
        });

        feedbackButtons.add(likeButton);
        feedbackButtons.add(dislikeButton);

        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(feedbackButtons);

        card.add(numLabel, BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        // Hover tint
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(SURFACE2);
                textBlock.setBackground(SURFACE2);
                rightPanel.setBackground(SURFACE2);
                feedbackButtons.setBackground(SURFACE2);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(SURFACE);
                textBlock.setBackground(SURFACE);
                rightPanel.setBackground(SURFACE);
                feedbackButtons.setBackground(SURFACE);
            }
        });

        return card;
    }

    // ── Empty state ───────────────────────────────────────────────────

    private void showEmptyState() {
        songList.removeAll();

        JPanel empty = new JPanel(new FlowLayout(FlowLayout.LEFT));
        empty.setBackground(BG);
        empty.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hint = new JLabel("← Add genres or artists, then click Generate");
        hint.setForeground(new Color(60, 60, 80));
        hint.setFont(new Font("SansSerif", Font.ITALIC, 13));
        empty.add(hint);

        songList.add(empty);
        songList.revalidate();
        songList.repaint();
    }

    // ── Utilities ─────────────────────────────────────────────────────

    private static JButton feedbackButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setForeground(new Color(10, 10, 20));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(2, 7, 2, 7));

        return button;
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }
}
