package cs152.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * PreferencesPanel
 *
 * The left-hand panel that holds all user preference inputs:
 *   - Preferred genres / artists
 *   - Excluded genres / artists
 *   - Explicit toggle
 *   - Playlist length slider
 *   - Engine selector
 *   - Generate button (pinned to bottom)
 */
public class PreferencesPanel extends JPanel {

    // ── Colour palette (matches AppWindow) ──────────────────────────
    static final Color BG         = new Color(13, 13, 18);
    static final Color SURFACE    = new Color(22, 22, 30);
    static final Color SURFACE2   = new Color(30, 30, 40);
    static final Color ACCENT     = new Color(134, 239, 172);   // mint green
    static final Color ACCENT2    = new Color(52, 211, 153);
    static final Color TEXT       = new Color(240, 240, 245);
    static final Color MUTED      = new Color(120, 120, 140);
    static final Color BORDER_COL = new Color(45, 45, 60);

    // ── Input fields ─────────────────────────────────────────────────
    private final JTextField preferredGenresField   = styledField("e.g. pop, rock, indie");
    private final JTextField preferredArtistsField  = styledField("e.g. Taylor Swift, Adele");
    private final JTextField excludedGenresField    = styledField("e.g. country, metal");
    private final JTextField excludedArtistsField   = styledField("e.g. Drake, Eminem");

    private final JCheckBox  allowExplicitBox       = new JCheckBox("Allow explicit content");
    private final JSlider    lengthSlider           = new JSlider(5, 50, 10);
    private final JLabel     lengthLabel            = new JLabel("10 songs");

    private final JRadioButton engine1Radio = new JRadioButton("Discovery  (genre-diverse)");
    private final JRadioButton engine2Radio = new JRadioButton("Top Picks  (score-ranked)");

    private final JButton generateButton = new JButton("Generate Playlist");

    // ── Callback ─────────────────────────────────────────────────────
    private Runnable onGenerate;

    // ─────────────────────────────────────────────────────────────────

    public PreferencesPanel() {
        setBackground(SURFACE);
        setLayout(new BorderLayout());
        setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COL));
        setPreferredSize(new Dimension(300, 0));

        JPanel inner = new JPanel();
        inner.setBackground(SURFACE);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(28, 24, 24, 24));

        // ── Header ────────────────────────────────────────────────
        inner.add(sectionHeader("🎵  Preferences"));
        inner.add(Box.createVerticalStrut(20));

        // ── Include section ───────────────────────────────────────
        inner.add(groupLabel("INCLUDE"));
        inner.add(Box.createVerticalStrut(6));
        inner.add(fieldLabel("Genres"));
        inner.add(Box.createVerticalStrut(4));
        inner.add(preferredGenresField);
        inner.add(Box.createVerticalStrut(10));
        inner.add(fieldLabel("Artists"));
        inner.add(Box.createVerticalStrut(4));
        inner.add(preferredArtistsField);
        inner.add(Box.createVerticalStrut(18));

        // ── Exclude section ───────────────────────────────────────
        inner.add(groupLabel("EXCLUDE"));
        inner.add(Box.createVerticalStrut(6));
        inner.add(fieldLabel("Genres"));
        inner.add(Box.createVerticalStrut(4));
        inner.add(excludedGenresField);
        inner.add(Box.createVerticalStrut(10));
        inner.add(fieldLabel("Artists"));
        inner.add(Box.createVerticalStrut(4));
        inner.add(excludedArtistsField);
        inner.add(Box.createVerticalStrut(18));

        // ── Explicit toggle ───────────────────────────────────────
        inner.add(groupLabel("CONTENT"));
        inner.add(Box.createVerticalStrut(6));
        styleCheckBox(allowExplicitBox);
        allowExplicitBox.setSelected(true);
        allowExplicitBox.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(allowExplicitBox);
        inner.add(Box.createVerticalStrut(18));

        // ── Length slider ─────────────────────────────────────────
        inner.add(groupLabel("PLAYLIST LENGTH"));
        inner.add(Box.createVerticalStrut(6));

        JPanel sliderRow = new JPanel(new BorderLayout(8, 0));
        sliderRow.setBackground(SURFACE);
        sliderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        styleSlider(lengthSlider);
        lengthSlider.addChangeListener(e -> lengthLabel.setText(lengthSlider.getValue() + " songs"));

        lengthLabel.setForeground(ACCENT);
        lengthLabel.setFont(new Font("Monospaced", Font.BOLD, 12));

        sliderRow.add(lengthSlider, BorderLayout.CENTER);
        sliderRow.add(lengthLabel, BorderLayout.EAST);
        sliderRow.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(sliderRow);
        inner.add(Box.createVerticalStrut(18));

        // ── Engine selector ───────────────────────────────────────
        inner.add(groupLabel("ENGINE"));
        inner.add(Box.createVerticalStrut(6));

        ButtonGroup engineGroup = new ButtonGroup();
        engineGroup.add(engine1Radio);
        engineGroup.add(engine2Radio);
        engine1Radio.setSelected(true);

        styleRadio(engine1Radio);
        styleRadio(engine2Radio);

        engine1Radio.setAlignmentX(LEFT_ALIGNMENT);
        engine2Radio.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(engine1Radio);
        inner.add(Box.createVerticalStrut(4));
        inner.add(engine2Radio);

        // ── Scroll pane wrapping inner ────────────────────────────
        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.setBackground(SURFACE);
        scroll.getViewport().setBackground(SURFACE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        // ── Generate button pinned to bottom ──────────────────────
        styleGenerateButton(generateButton);
        generateButton.addActionListener(e -> {
            if (onGenerate != null) onGenerate.run();
        });

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(SURFACE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                BorderFactory.createEmptyBorder(12, 24, 20, 24)));
        bottomBar.add(generateButton, BorderLayout.CENTER);

        add(scroll, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    // ── Public getters ────────────────────────────────────────────────

    public String getPreferredGenres()  { return preferredGenresField.getText(); }
    public String getPreferredArtists() { return preferredArtistsField.getText(); }
    public String getExcludedGenres()   { return excludedGenresField.getText(); }
    public String getExcludedArtists()  { return excludedArtistsField.getText(); }
    public boolean isAllowExplicit()    { return allowExplicitBox.isSelected(); }
    public int getPlaylistLength()      { return lengthSlider.getValue(); }
    public int getEngineNumber()        { return engine1Radio.isSelected() ? 1 : 2; }

    public void setOnGenerate(Runnable r) { this.onGenerate = r; }

    // ── Widget factory helpers ────────────────────────────────────────

    private static JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Georgia", Font.BOLD, 18));
        l.setForeground(TEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel groupLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, 10));
        l.setForeground(MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(new Color(190, 190, 205));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                }
            }
        };
        f.setBackground(SURFACE2);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);

        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COL, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                f.repaint();
            }
        });

        return f;
    }

    private static void styleCheckBox(JCheckBox cb) {
        cb.setBackground(SURFACE);
        cb.setForeground(new Color(190, 190, 205));
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cb.setFocusPainted(false);
    }

    private static void styleSlider(JSlider s) {
        s.setBackground(SURFACE);
        s.setForeground(ACCENT);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        s.setAlignmentX(LEFT_ALIGNMENT);
    }

    private static void styleRadio(JRadioButton rb) {
        rb.setBackground(SURFACE);
        rb.setForeground(new Color(190, 190, 205));
        rb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rb.setFocusPainted(false);
    }

    private static void styleGenerateButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(10, 10, 20));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 44));
    
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(220, 220, 220));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(Color.WHITE);
            }
        });
    }
}