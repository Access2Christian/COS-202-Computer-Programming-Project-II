package gui;

import controller.*;
import model.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class MainWindow extends JFrame {
    private LibraryManager lm;
    private JLabel statusLabel;
    private JLabel clockLabel;
    private JLabel statsLabel;
    private JTabbedPane mainTabs;

    // Panels
    private ViewItemsPanel viewPanel;
    private BorrowPanel borrowPanel;
    private SearchSortPanel searchPanel;
    private AdminPanel adminPanel;
    private ReportsPanel reportsPanel;

    // Dashboard stat cards
    private StatCard cardTotal, cardAvail, cardLoans, cardOverdue, cardUsers;

    public MainWindow() {
        this.lm = LibraryManager.getInstance();
        UITheme.apply();
        initFrame();
        buildLayout();
        startClock();
        startOverdueTimer();
        setVisible(true);
    }

    private void initFrame() {
        setTitle("SLCAS — Smart Library Circulation & Automation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_PRIMARY);

        // Menu bar
        setJMenuBar(buildMenuBar());
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(UITheme.BG_SECONDARY);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JMenu file = styledMenu("File");
        file.add(styledMenuItem("Save All Data", e -> { lm.saveData(); setStatus("Data saved."); }));
        file.add(styledMenuItem("Exit", e -> System.exit(0)));

        JMenu view = styledMenu("View");
        view.add(styledMenuItem("Refresh All", e -> refreshAll()));

        JMenu help = styledMenu("Help");
        help.add(styledMenuItem("About SLCAS", e -> showAbout()));
        help.add(styledMenuItem("Keyboard Shortcuts", e -> showShortcuts()));

        bar.add(file); bar.add(view); bar.add(help);
        return bar;
    }

    private JMenu styledMenu(String name) {
        JMenu m = new JMenu(name);
        m.setForeground(UITheme.TEXT_SECONDARY);
        m.setFont(UITheme.FONT_BODY);
        return m;
    }

    private JMenuItem styledMenuItem(String name, ActionListener al) {
        JMenuItem mi = new JMenuItem(name);
        mi.setBackground(UITheme.BG_SECONDARY);
        mi.setForeground(UITheme.TEXT_PRIMARY);
        mi.setFont(UITheme.FONT_BODY);
        mi.addActionListener(al);
        return mi;
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_PRIMARY);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildDashboard(), BorderLayout.BEFORE_FIRST_LINE);
        root.add(buildTabs(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        // Use a wrapper with BorderLayout
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_PRIMARY);
        wrapper.add(buildHeader(), BorderLayout.NORTH);

        JPanel mid = new JPanel(new BorderLayout(0, 8));
        mid.setBackground(UITheme.BG_PRIMARY);
        mid.add(buildDashboard(), BorderLayout.NORTH);
        mid.add(buildTabs(), BorderLayout.CENTER);

        wrapper.add(mid, BorderLayout.CENTER);
        wrapper.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(wrapper);
    }

    // ─── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(8, 16, 42), getWidth(), 0, new Color(18, 36, 80));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative accent line at bottom
                g2.setColor(UITheme.ACCENT_BLUE);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Left: Logo + Title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Logo icon
        JLabel logoIcon = new JLabel("\uD83C\uDFDB") { // 🏛
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT_BLUE);
                g2.fillRoundRect(0, 0, 44, 44, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("SL", 7, 29);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoIcon.setFont(new Font("SansSerif", Font.PLAIN, 0));
        logoIcon.setPreferredSize(new Dimension(44, 44));
        logoIcon.setOpaque(false);
        left.add(logoIcon);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 0));
        titles.setOpaque(false);
        JLabel mainTitle = UITheme.makeLabel("Smart Library Circulation & Automation System", UITheme.TEXT_PRIMARY, UITheme.FONT_TITLE);
        JLabel subTitle  = UITheme.makeLabel("MIVA Open University  ·  COS 202 Project", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
        titles.add(mainTitle);
        titles.add(subTitle);
        left.add(titles);
        header.add(left, BorderLayout.WEST);

        // Right: clock + quick stats
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 2));
        right.setOpaque(false);
        clockLabel = UITheme.makeLabel("", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statsLabel = UITheme.makeLabel("", UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        right.add(clockLabel);
        right.add(statsLabel);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ─── Dashboard Stats Bar ───────────────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel dash = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.BG_SECONDARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER);
                g2.fillRect(0, getHeight()-1, getWidth(), 1);
                g2.dispose();
            }
        };
        dash.setOpaque(false);
        dash.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        cardTotal   = new StatCard("\uD83D\uDCDA", "Total Items",   String.valueOf(lm.getTotalItems()),  UITheme.ACCENT_BLUE);
        cardAvail   = new StatCard("\u2705",        "Available",     String.valueOf(lm.getAvailableItems()), UITheme.SUCCESS);
        cardLoans   = new StatCard("\uD83D\uDCD6",  "Active Loans",  String.valueOf(lm.getActiveLoans()), UITheme.ACCENT_GOLD);
        cardOverdue = new StatCard("\u26A0\uFE0F",  "Overdue",       String.valueOf(lm.getOverdueCount()), UITheme.DANGER);
        cardUsers   = new StatCard("\uD83D\uDC65",  "Users",         String.valueOf(lm.getTotalUsers()), UITheme.ACCENT_TEAL);

        // Frequent cache display
        JPanel cachePanel = buildCachePanel();

        dash.add(cardTotal);
        dash.add(cardAvail);
        dash.add(cardLoans);
        dash.add(cardOverdue);
        dash.add(cardUsers);
        dash.add(Box.createHorizontalStrut(10));
        dash.add(cachePanel);

        return dash;
    }

    private JPanel buildCachePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        p.setPreferredSize(new Dimension(280, 110));

        JLabel lbl = UITheme.makeLabel("★  TOP ACCESSED", UITheme.ACCENT_GOLD, UITheme.FONT_SMALL);
        p.add(lbl, BorderLayout.NORTH);

        JPanel items = new JPanel(new GridLayout(5, 1, 0, 0));
        items.setOpaque(false);

        LibraryItem[] cache = lm.getFrequentCache();
        for (int i = 0; i < 5; i++) {
            String txt = (i < cache.length && cache[i] != null)
                ? (i+1) + ". " + truncate(cache[i].getTitle(), 28) + " [" + cache[i].getAccessCount() + "]"
                : (i+1) + ". —";
            JLabel li = UITheme.makeLabel(txt, i == 0 ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
            items.add(li);
        }
        p.add(items, BorderLayout.CENTER);
        return p;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    // ─── Main Tabbed Pane ──────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        mainTabs = new JTabbedPane(JTabbedPane.TOP) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UITheme.BG_PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        mainTabs.setBackground(UITheme.BG_PRIMARY);
        mainTabs.setForeground(UITheme.TEXT_SECONDARY);
        mainTabs.setFont(UITheme.FONT_TAB);

        statusLabel = new JLabel("  Ready  —  SLCAS loaded successfully");
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);
        statusLabel.setFont(UITheme.FONT_SMALL);

        viewPanel   = new ViewItemsPanel(statusLabel);
        borrowPanel = new BorrowPanel(statusLabel);
        searchPanel = new SearchSortPanel(statusLabel);
        adminPanel  = new AdminPanel(statusLabel, this::refreshAll);
        reportsPanel = new ReportsPanel(statusLabel);

        mainTabs.addTab(" \uD83D\uDCDA  Catalogue  ",   viewPanel);
        mainTabs.addTab(" \uD83D\uDCD6  Borrow/Return ", borrowPanel);
        mainTabs.addTab(" \uD83D\uDD0D  Search & Sort ", searchPanel);
        mainTabs.addTab(" \u2699\uFE0F  Admin  ",        adminPanel);
        mainTabs.addTab(" \uD83D\uDCCA  Reports  ",      reportsPanel);

        // Tooltips for tabs
        mainTabs.setToolTipTextAt(0, "View all library items (Alt+1)");
        mainTabs.setToolTipTextAt(1, "Borrow and return items (Alt+2)");
        mainTabs.setToolTipTextAt(2, "Search and sort items (Alt+3)");
        mainTabs.setToolTipTextAt(3, "Admin: add/delete items and users (Alt+4)");
        mainTabs.setToolTipTextAt(4, "Analytics and reports (Alt+5)");

        // Keyboard shortcuts
        mainTabs.registerKeyboardAction(e -> mainTabs.setSelectedIndex(0),
            KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainTabs.registerKeyboardAction(e -> mainTabs.setSelectedIndex(1),
            KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainTabs.registerKeyboardAction(e -> mainTabs.setSelectedIndex(2),
            KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainTabs.registerKeyboardAction(e -> mainTabs.setSelectedIndex(3),
            KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
        mainTabs.registerKeyboardAction(e -> mainTabs.setSelectedIndex(4),
            KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Refresh data when switching to Reports tab
        mainTabs.addChangeListener(e -> {
            if (mainTabs.getSelectedIndex() == 4) reportsPanel.refresh();
            updateStats();
        });

        return mainTabs;
    }

    // ─── Status Bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UITheme.BG_DARKEST);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        bar.setPreferredSize(new Dimension(0, 26));

        if (statusLabel == null) {
            statusLabel = new JLabel("  Ready");
            statusLabel.setForeground(UITheme.TEXT_SECONDARY);
            statusLabel.setFont(UITheme.FONT_SMALL);
        }

        JLabel right = UITheme.makeLabel("SLCAS v1.0  |  COS 202  |  MIVA Open University",
            UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ─── Live Clock ────────────────────────────────────────────────────────────
    private void startClock() {
        javax.swing.Timer t = new javax.swing.Timer(1000, e -> {
            String dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy  HH:mm:ss"));
            clockLabel.setText(dt + "  ");
            updateStats();
        });
        t.start();
    }

    // ─── Overdue Reminder Timer ────────────────────────────────────────────────
    private void startOverdueTimer() {
        // Check every 30 seconds for overdue items and notify
        javax.swing.Timer t = new javax.swing.Timer(30_000, e -> {
            int overdue = lm.getOverdueCount();
            if (overdue > 0) {
                setStatus("  \u26A0 WARNING: " + overdue + " item(s) currently overdue!");
            }
        });
        t.start();
    }

    private void updateStats() {
        if (cardTotal != null) {
            cardTotal.setValue(String.valueOf(lm.getTotalItems()));
            cardAvail.setValue(String.valueOf(lm.getAvailableItems()));
            cardLoans.setValue(String.valueOf(lm.getActiveLoans()));
            cardOverdue.setValue(String.valueOf(lm.getOverdueCount()));
            cardUsers.setValue(String.valueOf(lm.getTotalUsers()));
        }
        if (statsLabel != null) {
            statsLabel.setText(lm.getTotalItems() + " items  |  " + lm.getActiveLoans() + " active loans  |  " + lm.getOverdueCount() + " overdue  ");
        }
    }

    public void refreshAll() {
        viewPanel.refresh();
        borrowPanel.refreshLoans();
        searchPanel.refresh();
        adminPanel.refreshUsers();
        updateStats();
        setStatus("  Data refreshed.");
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    // ─── About Dialog ──────────────────────────────────────────────────────────
    private void showAbout() {
        String msg = "<html><body style='width:300px; font-family:SansSerif;'>" +
            "<h2 style='color:#3890FF;'>SLCAS v1.0</h2>" +
            "<p><b>Smart Library Circulation &amp; Automation System</b></p>" +
            "<p>COS 202 Project — MIVA Open University</p><hr>" +
            "<p><b>Features:</b></p>" +
            "<ul><li>Advanced OOP with abstract classes &amp; interfaces</li>" +
            "<li>Data structures: ArrayList, Queue, Stack, Array cache</li>" +
            "<li>Sorting: Merge Sort, Quick Sort, Selection Sort, Insertion Sort</li>" +
            "<li>Searching: Linear, Binary, Recursive</li>" +
            "<li>Recursive overdue fine computation</li>" +
            "<li>Event-driven GUI with custom renderers</li>" +
            "<li>File persistence (CSV)</li>" +
            "<li>Live clock &amp; overdue timer</li>" +
            "<li>File export, undo stack, reservation queue</li></ul>" +
            "</body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(msg), "About SLCAS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showShortcuts() {
        String msg = "<html><body style='font-family:monospace; width:280px;'>" +
            "<h3>Keyboard Shortcuts</h3>" +
            "<table><tr><td><b>Alt+1</b></td><td>Catalogue tab</td></tr>" +
            "<tr><td><b>Alt+2</b></td><td>Borrow/Return tab</td></tr>" +
            "<tr><td><b>Alt+3</b></td><td>Search &amp; Sort tab</td></tr>" +
            "<tr><td><b>Alt+4</b></td><td>Admin tab</td></tr>" +
            "<tr><td><b>Alt+5</b></td><td>Reports tab</td></tr>" +
            "<tr><td><b>Enter</b></td><td>Search (in search field)</td></tr>" +
            "<tr><td><b>Alt+S</b></td><td>Search button</td></tr>" +
            "<tr><td><b>Alt+O</b></td><td>Sort button</td></tr></table>" +
            "</body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(msg), "Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }
}
