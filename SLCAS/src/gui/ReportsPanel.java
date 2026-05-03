package gui;

import controller.*;
import model.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class ReportsPanel extends JPanel {
    private LibraryManager lm;
    private JLabel statusBar;
    private JPanel chartsArea;

    public ReportsPanel(JLabel statusBar) {
        this.lm = LibraryManager.getInstance();
        this.statusBar = statusBar;
        setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        header.setBackground(UITheme.BG_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        header.add(UITheme.makeLabel("\uD83D\uDCCA  Library Reports & Analytics", UITheme.TEXT_PRIMARY, UITheme.FONT_HEADER));

        StyledButton btnRefresh = new StyledButton("Refresh", StyledButton.Style.GHOST);
        btnRefresh.addActionListener(e -> refresh());
        header.add(btnRefresh);

        StyledButton btnExport = new StyledButton("Export Report", StyledButton.Style.GOLD);
        btnExport.addActionListener(e -> exportReport());
        header.add(btnExport);
        add(header, BorderLayout.NORTH);

        // Main content: two-column layout
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setBackground(UITheme.BG_PRIMARY);
        split.setBorder(null);
        split.setDividerSize(6);
        split.setResizeWeight(0.5);
        split.setDividerLocation(480);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_PRIMARY);

        // Type Distribution chart
        JPanel chartCard = new JPanel(new BorderLayout(0, 8));
        chartCard.setBackground(UITheme.BG_CARD);
        chartCard.setBorder(UITheme.cardBorder("Item Type Distribution"));
        chartCard.add(new DonutChart(), BorderLayout.CENTER);
        chartCard.setPreferredSize(new Dimension(300, 220));

        // Most borrowed table
        JPanel borrowedCard = new JPanel(new BorderLayout(0, 8));
        borrowedCard.setBackground(UITheme.BG_CARD);
        borrowedCard.setBorder(UITheme.cardBorder("Most Borrowed Items (Top 5)"));

        String[] cols = {"Title","Type","Author","Access Count"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<LibraryItem> top = lm.getMostBorrowed(5);
        for (LibraryItem item : top) {
            m.addRow(new Object[]{item.getTitle(), item.getItemType(), item.getAuthor(), item.getAccessCount()});
        }
        JTable t = buildSmallTable(m);
        borrowedCard.add(UITheme.styleScrollPane(t), BorderLayout.CENTER);

        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartCard, borrowedCard);
        vSplit.setBorder(null); vSplit.setBackground(UITheme.BG_PRIMARY);
        vSplit.setResizeWeight(0.45); vSplit.setDividerSize(6);
        p.add(vSplit, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_PRIMARY);

        // Category bar chart
        JPanel barCard = new JPanel(new BorderLayout(0, 8));
        barCard.setBackground(UITheme.BG_CARD);
        barCard.setBorder(UITheme.cardBorder("Items per Category"));
        barCard.add(new BarChart(), BorderLayout.CENTER);
        barCard.setPreferredSize(new Dimension(300, 200));

        // Overdue users table
        JPanel overdueCard = new JPanel(new BorderLayout(0, 8));
        overdueCard.setBackground(UITheme.BG_CARD);
        overdueCard.setBorder(UITheme.cardBorder("Users with Overdue Items"));

        String[] cols = {"Name","ID","Overdue Loans","Fine (₦)"};
        DefaultTableModel m2 = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (UserAccount u : lm.getUsersWithOverdue()) {
            m2.addRow(new Object[]{u.getName(), u.getId(), u.getOverdueLoans(),
                String.format("%.2f", u.getTotalFines())});
        }
        JTable t2 = buildSmallTable(m2);
        // Color overdue rows red
        t2.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setFont(UITheme.FONT_BODY);
                if (!isSel) {
                    setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ALT);
                    if (col == 2) setForeground(UITheme.DANGER);
                    else if (col == 3) setForeground(UITheme.WARNING);
                    else setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });
        overdueCard.add(UITheme.styleScrollPane(t2), BorderLayout.CENTER);

        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, barCard, overdueCard);
        vSplit.setBorder(null); vSplit.setBackground(UITheme.BG_PRIMARY);
        vSplit.setResizeWeight(0.45); vSplit.setDividerSize(6);
        p.add(vSplit, BorderLayout.CENTER);
        return p;
    }

    private JTable buildSmallTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setBackground(UITheme.BG_CARD);
        t.setForeground(UITheme.TEXT_PRIMARY);
        t.setGridColor(UITheme.BORDER);
        t.setSelectionBackground(UITheme.ACCENT_BLUE);
        t.setFont(UITheme.FONT_BODY);
        t.setRowHeight(28);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setBackground(UITheme.HEADER_BG);
        t.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);
        t.getTableHeader().setFont(UITheme.FONT_SUBHEAD);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSel) {
                    setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ALT);
                    setForeground(UITheme.TEXT_PRIMARY);
                }
                return this;
            }
        });
        return t;
    }

    public void refresh() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void exportReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SLCAS LIBRARY REPORT ===\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("SUMMARY\n-------\n");
        sb.append("Total Items: ").append(lm.getTotalItems()).append("\n");
        sb.append("Total Users: ").append(lm.getTotalUsers()).append("\n");
        sb.append("Active Loans: ").append(lm.getActiveLoans()).append("\n");
        sb.append("Overdue Items: ").append(lm.getOverdueCount()).append("\n\n");
        sb.append("TYPE DISTRIBUTION\n-----------------\n");
        lm.getTypeDistribution().forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        sb.append("\nCATEGORY DISTRIBUTION\n---------------------\n");
        lm.getCategoryDistribution().forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        sb.append("\nTOP 5 MOST BORROWED\n-------------------\n");
        for (LibraryItem item : lm.getMostBorrowed(5)) {
            sb.append(item.getTitle()).append(" (").append(item.getAccessCount()).append(" accesses)\n");
        }
        sb.append("\nUSERS WITH OVERDUE ITEMS\n------------------------\n");
        for (UserAccount u : lm.getUsersWithOverdue()) {
            sb.append(u.getName()).append(" | ").append(u.getOverdueLoans())
              .append(" overdue | Fine: ₦").append(String.format("%.2f", u.getTotalFines())).append("\n");
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("library_report_" + LocalDate.now() + ".txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            utils.FileHandler.exportReport(fc.getSelectedFile().getAbsolutePath(), sb.toString());
            JOptionPane.showMessageDialog(this, "Report exported to:\n" + fc.getSelectedFile(), "Export", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Donut Chart ───────────────────────────────────────────────────────────
    private class DonutChart extends JPanel {
        DonutChart() { setOpaque(false); setPreferredSize(new Dimension(280, 180)); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Map<String, Integer> dist = lm.getTypeDistribution();
            int total = dist.values().stream().mapToInt(v -> v).sum();
            if (total == 0) { g2.dispose(); return; }

            Color[] colors = {UITheme.ACCENT_BLUE, UITheme.ACCENT_TEAL, UITheme.ACCENT_GOLD};
            String[] keys = {"Book","Magazine","Journal"};

            int cx = 110, cy = getHeight()/2, r = 70, inner = 38;
            double startAngle = 0;
            int ki = 0;
            for (String key : keys) {
                int cnt = dist.getOrDefault(key, 0);
                double sweep = 360.0 * cnt / total;
                g2.setColor(colors[ki % colors.length]);
                g2.fill(new Arc2D.Double(cx - r, cy - r, r*2, r*2, startAngle, sweep, Arc2D.PIE));
                startAngle += sweep; ki++;
            }
            // Inner hole
            g2.setColor(UITheme.BG_CARD);
            g2.fillOval(cx - inner, cy - inner, inner*2, inner*2);
            // Center text
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.setFont(UITheme.FONT_SUBHEAD);
            String tot = String.valueOf(total);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(tot, cx - fm.stringWidth(tot)/2, cy + fm.getAscent()/2 - 2);

            // Legend
            int lx = cx + r + 20, ly = cy - 35;
            ki = 0;
            for (String key : keys) {
                int cnt = dist.getOrDefault(key, 0);
                g2.setColor(colors[ki % colors.length]);
                g2.fillRoundRect(lx, ly + ki * 24, 12, 12, 4, 4);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(key + " (" + cnt + ")", lx + 16, ly + ki * 24 + 10);
                ki++;
            }
            g2.dispose();
        }
    }

    // ─── Bar Chart ─────────────────────────────────────────────────────────────
    private class BarChart extends JPanel {
        BarChart() { setOpaque(false); setPreferredSize(new Dimension(280, 180)); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Map<String, Integer> dist = lm.getCategoryDistribution();
            if (dist.isEmpty()) { g2.dispose(); return; }

            int max = dist.values().stream().mapToInt(v -> v).max().orElse(1);
            int pw = getWidth() - 30, ph = getHeight() - 50;
            int n = dist.size();
            int barW = Math.min(40, (pw - 10) / Math.max(n, 1) - 4);
            int x = 20;
            Color[] colors = {UITheme.ACCENT_BLUE, UITheme.ACCENT_TEAL, UITheme.ACCENT_GOLD,
                UITheme.ACCENT_PURPLE, UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER};
            int ci = 0;
            for (Map.Entry<String, Integer> e : dist.entrySet()) {
                int barH = (int)(1.0 * e.getValue() / max * ph);
                int by = ph - barH + 10;
                g2.setColor(colors[ci % colors.length]);
                g2.fillRoundRect(x, by, barW, barH, 4, 4);
                // Label
                g2.setFont(UITheme.FONT_SMALL);
                g2.setColor(UITheme.TEXT_SECONDARY);
                String lbl = e.getKey().length() > 5 ? e.getKey().substring(0, 5) : e.getKey();
                g2.drawString(lbl, x, ph + 22);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.drawString(String.valueOf(e.getValue()), x + barW/2 - 3, by - 4);
                x += barW + 8; ci++;
            }
            g2.dispose();
        }
    }
}
