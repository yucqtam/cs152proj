package cs152.view;

import cs152.ScoredSong;
import cs152.controller.PlaylistController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * AppWindow
 *
 * The top-level JFrame.  Lays out the PreferencesPanel (left) and the
 * PlaylistPanel (right), and owns the PlaylistController that bridges them.
 *
 * Layout:
 *   ┌──────────────────┬────────────────────────────────────┐
 *   │  PreferencesPanel│        PlaylistPanel                │
 *   │  (300 px fixed)  │      (fills remaining width)        │
 *   └──────────────────┴────────────────────────────────────┘
 */
public class AppWindow extends JFrame {

    private final PlaylistController controller = new PlaylistController();
    private final PreferencesPanel   prefsPanel = new PreferencesPanel();
    private final PlaylistPanel      playPanel  = new PlaylistPanel();

    public AppWindow() {
        setTitle("Personalized Playlist Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 600));
        setPreferredSize(new Dimension(1060, 700));

        // Dark window decorations where supported
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Root panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PreferencesPanel.BG);

        // Top bar
        root.add(buildTopBar(), BorderLayout.NORTH);

        // Body: prefs (left) + playlist (right)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, prefsPanel, playPanel);
        split.setDividerLocation(300);
        split.setDividerSize(1);
        split.setBorder(null);
        split.setBackground(PreferencesPanel.BG);
        root.add(split, BorderLayout.CENTER);

        setContentPane(root);

        // Wire up the Generate button
        prefsPanel.setOnGenerate(this::onGenerateClicked);

        playPanel.setOnFeedback((scoredSong, liked) -> {
            controller.recordFeedback(scoredSong.getSong(), liked);
            playPanel.showFeedbackMessage("Generate again to update recommendations.");
        });

        // Show catalogue size once loaded
        if (controller.isLibraryLoaded()) {
            playPanel.setCatalogInfo(controller.getCatalogSize());
        } else {
            playPanel.showError("Could not load dataset/musicDataset.csv — make sure the file exists in the project root.");
        }

        pack();
        setLocationRelativeTo(null);  // centre on screen
    }

    // ── Top bar ───────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(18, 18, 26));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 40, 55)),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));

        JLabel appName = new JLabel("♪  Playlist Generator");
        appName.setFont(new Font("Georgia", Font.BOLD, 15));
        appName.setForeground(PreferencesPanel.TEXT);

        JLabel tagLine = new JLabel("CS 152 · SJSU");
        tagLine.setFont(new Font("Monospaced", Font.PLAIN, 11));
        tagLine.setForeground(PreferencesPanel.MUTED);

        bar.add(appName, BorderLayout.WEST);
        bar.add(tagLine, BorderLayout.EAST);

        return bar;
    }

    // ── Generate handler ──────────────────────────────────────────────

    private void onGenerateClicked() {
        if (!controller.isLibraryLoaded()) {
            playPanel.showError("Dataset not loaded. Check that dataset/musicDataset.csv is in the project root.");
            return;
        }

        playPanel.showLoading();

        // Run generation on a background thread so the UI doesn't freeze
        SwingWorker<ArrayList<ScoredSong>, Void> worker = new SwingWorker<>() {
            int engineNum;
            String engineName;

            @Override
            protected ArrayList<ScoredSong> doInBackground() {
                engineNum  = prefsPanel.getEngineNumber();
                engineName = (engineNum == 1) ? "Discovery Engine" : "Top Picks Engine";

                return controller.generatePlaylist(
                        prefsPanel.getPreferredGenres(),
                        prefsPanel.getPreferredArtists(),
                        prefsPanel.getExcludedGenres(),
                        prefsPanel.getExcludedArtists(),
                        prefsPanel.isAllowExplicit(),
                        prefsPanel.getPlaylistLength(),
                        engineNum);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<ScoredSong> result = get();
                    playPanel.showPlaylist(result, engineName);
                } catch (Exception ex) {
                    playPanel.showError("An error occurred: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }
}
