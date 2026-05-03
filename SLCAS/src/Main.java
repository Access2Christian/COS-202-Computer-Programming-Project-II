import gui.MainWindow;
import gui.UITheme;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Apply UI theme before any Swing component is created
        UITheme.apply();

        SwingUtilities.invokeLater(() -> {
            try {
                // Try to use system look and feel as base, then override with our theme
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UITheme.apply(); // Reapply after LookAndFeel change
            } catch (Exception e) {
                // Fallback is fine
            }
            new MainWindow();
        });
    }
}
