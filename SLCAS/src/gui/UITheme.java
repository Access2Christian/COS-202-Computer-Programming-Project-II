package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

public class UITheme {

    // ─── Color Palette ─────────────────────────────────────────────────────────
    public static final Color BG_DARKEST   = new Color(6,  12,  24);
    public static final Color BG_PRIMARY   = new Color(10, 16,  32);
    public static final Color BG_SECONDARY = new Color(16, 26,  50);
    public static final Color BG_CARD      = new Color(22, 36,  64);
    public static final Color BG_HOVER     = new Color(30, 48,  82);
    public static final Color BG_ELEVATED  = new Color(26, 42,  74);

    public static final Color ACCENT_BLUE   = new Color(56,  142, 255);
    public static final Color ACCENT_GOLD   = new Color(245, 168, 30);
    public static final Color ACCENT_TEAL   = new Color(32,  200, 170);
    public static final Color ACCENT_PURPLE = new Color(150, 100, 255);

    public static final Color TEXT_PRIMARY   = new Color(225, 235, 250);
    public static final Color TEXT_SECONDARY = new Color(140, 165, 200);
    public static final Color TEXT_MUTED     = new Color(75,  95,  130);
    public static final Color TEXT_ACCENT    = new Color(100, 175, 255);

    public static final Color SUCCESS = new Color(40,  210, 100);
    public static final Color WARNING = new Color(245, 180, 30);
    public static final Color DANGER  = new Color(245, 75,  75);
    public static final Color INFO    = new Color(56,  142, 255);

    public static final Color BORDER       = new Color(35, 55, 95);
    public static final Color BORDER_LIGHT = new Color(50, 75, 120);
    public static final Color TABLE_ALT    = new Color(18, 28, 52);
    public static final Color TABLE_SEL    = new Color(56, 142, 255, 70);
    public static final Color HEADER_BG    = new Color(14, 22, 42);

    // ─── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_HUGE    = new Font("SansSerif", Font.BOLD, 32);
    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADER  = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_SUBHEAD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_BUTTON  = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_TAB     = new Font("SansSerif", Font.BOLD, 13);

    // ─── Apply Global Swing Theme ──────────────────────────────────────────────
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Panels
        UIManager.put("Panel.background",         BG_PRIMARY);
        UIManager.put("Viewport.background",       BG_PRIMARY);

        // Tabbed Pane
        UIManager.put("TabbedPane.background",              BG_PRIMARY);
        UIManager.put("TabbedPane.foreground",              TEXT_SECONDARY);
        UIManager.put("TabbedPane.selected",                BG_CARD);
        UIManager.put("TabbedPane.selectedBackground",      BG_CARD);
        UIManager.put("TabbedPane.selectedForeground",      TEXT_PRIMARY);
        UIManager.put("TabbedPane.tabAreaBackground",       BG_SECONDARY);
        UIManager.put("TabbedPane.contentAreaColor",        BG_PRIMARY);
        UIManager.put("TabbedPane.light",                   BORDER);
        UIManager.put("TabbedPane.shadow",                  BG_DARKEST);
        UIManager.put("TabbedPane.darkShadow",              BG_DARKEST);
        UIManager.put("TabbedPane.highlight",               BORDER);
        UIManager.put("TabbedPane.focus",                   ACCENT_BLUE);
        UIManager.put("TabbedPane.font",                    FONT_TAB);

        // Table
        UIManager.put("Table.background",          BG_CARD);
        UIManager.put("Table.foreground",          TEXT_PRIMARY);
        UIManager.put("Table.gridColor",           BORDER);
        UIManager.put("Table.selectionBackground", ACCENT_BLUE);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.font",                FONT_BODY);
        UIManager.put("Table.rowHeight",           Integer.valueOf(32));
        UIManager.put("TableHeader.background",    HEADER_BG);
        UIManager.put("TableHeader.foreground",    TEXT_SECONDARY);
        UIManager.put("TableHeader.font",          FONT_SUBHEAD);

        // Scroll Pane
        UIManager.put("ScrollPane.background",     BG_PRIMARY);
        UIManager.put("ScrollBar.background",      BG_SECONDARY);
        UIManager.put("ScrollBar.thumb",           new Color(50, 75, 115));
        UIManager.put("ScrollBar.thumbHighlight",  BORDER_LIGHT);
        UIManager.put("ScrollBar.track",           BG_SECONDARY);
        UIManager.put("ScrollBar.trackHighlight",  BG_SECONDARY);
        UIManager.put("ScrollBar.width",           Integer.valueOf(8));

        // Text Fields
        UIManager.put("TextField.background",       BG_CARD);
        UIManager.put("TextField.foreground",       TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",  ACCENT_BLUE);
        UIManager.put("TextField.selectionBackground", ACCENT_BLUE);
        UIManager.put("TextField.border",
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        UIManager.put("TextField.font", FONT_BODY);

        // Text Area
        UIManager.put("TextArea.background",       BG_CARD);
        UIManager.put("TextArea.foreground",       TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground",  ACCENT_BLUE);
        UIManager.put("TextArea.font",             FONT_BODY);

        // Combo Box
        UIManager.put("ComboBox.background",       BG_CARD);
        UIManager.put("ComboBox.foreground",       TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT_BLUE);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.font",             FONT_BODY);
        UIManager.put("ComboBox.buttonBackground", BG_CARD);

        // Labels
        UIManager.put("Label.foreground",          TEXT_PRIMARY);
        UIManager.put("Label.font",                FONT_BODY);

        // Buttons
        UIManager.put("Button.background",         ACCENT_BLUE);
        UIManager.put("Button.foreground",         Color.WHITE);
        UIManager.put("Button.font",               FONT_BUTTON);
        UIManager.put("Button.focus",              new Color(0,0,0,0));

        // Split Pane
        UIManager.put("SplitPane.background",      BG_PRIMARY);
        UIManager.put("SplitPane.dividerSize",     Integer.valueOf(4));

        // Option Pane
        UIManager.put("OptionPane.background",           BG_SECONDARY);
        UIManager.put("OptionPane.foreground",           TEXT_PRIMARY);
        UIManager.put("OptionPane.messageForeground",    TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont",          FONT_BODY);
        UIManager.put("OptionPane.buttonFont",           FONT_BUTTON);

        // Dialog
        UIManager.put("Dialog.background",               BG_SECONDARY);

        // List
        UIManager.put("List.background",           BG_CARD);
        UIManager.put("List.foreground",           TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",  ACCENT_BLUE);
        UIManager.put("List.selectionForeground",  Color.WHITE);
    }

    // ─── Helper: Make Field ────────────────────────────────────────────────────
    public static JTextField makeField(String placeholder, int cols) {
        JTextField f = new JTextField(cols) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_BLUE);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        if (placeholder != null && !placeholder.isEmpty()) {
            f.setToolTipText(placeholder);
        }
        return f;
    }

    // ─── Helper: Make Combo ────────────────────────────────────────────────────
    public static JComboBox<String> makeCombo(String... options) {
        JComboBox<String> c = new JComboBox<>(options);
        c.setBackground(BG_CARD);
        c.setForeground(TEXT_PRIMARY);
        c.setFont(FONT_BODY);
        c.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        c.setFocusable(false);
        return c;
    }

    // ─── Helper: Make Label ────────────────────────────────────────────────────
    public static JLabel makeLabel(String text, Color color, Font font) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(font);
        return l;
    }

    // ─── Helper: Styled Scroll Pane ────────────────────────────────────────────
    public static JScrollPane styleScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_PRIMARY);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setOpaque(true);
        return sp;
    }

    // ─── Helper: Card Border ───────────────────────────────────────────────────
    public static Border cardBorder(String title) {
        Border line = BorderFactory.createLineBorder(BORDER, 1);
        Border pad  = BorderFactory.createEmptyBorder(10, 14, 14, 14);
        if (title != null && !title.isEmpty()) {
            TitledBorder tb = BorderFactory.createTitledBorder(line, " " + title + " ");
            tb.setTitleColor(TEXT_ACCENT);
            tb.setTitleFont(FONT_SUBHEAD);
            return BorderFactory.createCompoundBorder(tb, pad);
        }
        return BorderFactory.createCompoundBorder(line, pad);
    }
}
