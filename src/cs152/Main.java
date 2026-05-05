package cs152;

import cs152.view.AppWindow;

import javax.swing.SwingUtilities;

/**
 * Entry point — launches the GUI application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppWindow window = new AppWindow();
            window.setVisible(true);
        });
    }
}
