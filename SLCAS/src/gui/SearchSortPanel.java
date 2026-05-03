package gui;

import controller.*;
import model.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SearchSortPanel extends JPanel {
    private LibraryManager lm;
    private JLabel statusBar;

    private JTextField searchField;
    private JComboBox<String> fieldCombo, searchAlgoCombo;
    private JComboBox<String> sortAlgoCombo, sortFieldCombo;
    private LibraryTable resultTable;
    private JLabel resultCount;
    private JLabel algoInfoLabel;
    private boolean isSorted = false;
    private List<LibraryItem> currentList;

    public SearchSortPanel(JLabel statusBar) {
        this.lm = LibraryManager.getInstance();
        this.statusBar = statusBar;
        this.currentList = new ArrayList<>(lm.getItems());
        setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        buildUI();
        refreshTable();
    }

    private void buildUI() {
        // ── Top Controls ─────────────────────────────────────────────────
        JPanel controls = new JPanel(new GridLayout(2, 1, 0, 10));
        controls.setOpaque(false);
        controls.add(buildSearchBar());
        controls.add(buildSortBar());
        add(controls, BorderLayout.NORTH);

        // ── Algorithm Info ────────────────────────────────────────────────
        algoInfoLabel = UITheme.makeLabel("  Ready — select an algorithm and search or sort.", UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        algoInfoLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        add(algoInfoLabel, BorderLayout.AFTER_LAST_LINE);

        // ── Results Table ─────────────────────────────────────────────────
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setOpaque(false);
        tablePanel.setBorder(UITheme.cardBorder("Search / Sort Results"));

        resultTable = new LibraryTable();
        tablePanel.add(UITheme.styleScrollPane(resultTable), BorderLayout.CENTER);

        resultCount = UITheme.makeLabel("", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        foot.setOpaque(false);
        foot.add(resultCount);
        tablePanel.add(foot, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel buildSearchBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(UITheme.BG_SECONDARY);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        p.add(UITheme.makeLabel("\uD83D\uDD0D  Search", UITheme.TEXT_SECONDARY, UITheme.FONT_SUBHEAD));
        p.add(Box.createHorizontalStrut(8));

        searchField = UITheme.makeField("Enter search query...", 20);
        searchField.setPreferredSize(new Dimension(240, 32));
        p.add(searchField);

        p.add(UITheme.makeLabel("in", UITheme.TEXT_MUTED, UITheme.FONT_SMALL));
        fieldCombo = UITheme.makeCombo("Title", "Author", "Type", "Category");
        p.add(fieldCombo);

        p.add(UITheme.makeLabel("using", UITheme.TEXT_MUTED, UITheme.FONT_SMALL));
        searchAlgoCombo = UITheme.makeCombo("Linear Search", "Binary Search", "Recursive Search");
        p.add(searchAlgoCombo);

        StyledButton btnSearch = new StyledButton("Search", StyledButton.Style.PRIMARY);
        btnSearch.setMnemonic('S');
        btnSearch.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        p.add(btnSearch);

        StyledButton btnReset = new StyledButton("Reset", StyledButton.Style.GHOST);
        btnReset.addActionListener(e -> { searchField.setText(""); isSorted = false; currentList = new ArrayList<>(lm.getItems()); refreshTable(); algoInfoLabel.setText("  Reset — showing all items."); });
        p.add(btnReset);

        return p;
    }

    private JPanel buildSortBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(UITheme.BG_SECONDARY);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        p.add(UITheme.makeLabel("\u21C5  Sort", UITheme.TEXT_SECONDARY, UITheme.FONT_SUBHEAD));
        p.add(Box.createHorizontalStrut(8));

        p.add(UITheme.makeLabel("Algorithm:", UITheme.TEXT_MUTED, UITheme.FONT_SMALL));
        sortAlgoCombo = UITheme.makeCombo("Merge Sort", "Quick Sort", "Selection Sort", "Insertion Sort");
        p.add(sortAlgoCombo);

        p.add(UITheme.makeLabel("by", UITheme.TEXT_MUTED, UITheme.FONT_SMALL));
        sortFieldCombo = UITheme.makeCombo("Title", "Author", "Year", "Access");
        p.add(sortFieldCombo);

        StyledButton btnSort = new StyledButton("Sort", StyledButton.Style.GOLD);
        btnSort.setMnemonic('O');
        btnSort.addActionListener(e -> doSort());
        p.add(btnSort);

        // Complexity info
        JLabel complexLabel = UITheme.makeLabel("", UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        sortAlgoCombo.addActionListener(e -> {
            String algo = (String) sortAlgoCombo.getSelectedItem();
            switch (algo) {
                case "Merge Sort":     complexLabel.setText("O(n log n) — Stable"); break;
                case "Quick Sort":     complexLabel.setText("O(n log n) avg, O(n\u00B2) worst"); break;
                case "Selection Sort": complexLabel.setText("O(n\u00B2) — Simple"); break;
                case "Insertion Sort": complexLabel.setText("O(n\u00B2) — Best for small/sorted"); break;
            }
        });
        sortAlgoCombo.setSelectedIndex(0); // trigger label
        p.add(Box.createHorizontalStrut(10));
        p.add(complexLabel);

        return p;
    }

    private void doSearch() {
        String query = searchField.getText().trim();
        String field = (String) fieldCombo.getSelectedItem();
        String algo  = (String) searchAlgoCombo.getSelectedItem();

        long start = System.nanoTime();
        List<LibraryItem> base = isSorted ? currentList : new ArrayList<>(lm.getItems());
        List<LibraryItem> results = SearchEngine.search(base, query, field, algoShort(algo));
        long elapsed = System.nanoTime() - start;

        currentList = results;
        refreshTable();

        String info = String.format("  %s on '%s' by %s — %d result(s) in %.2f ms",
            algo, query.isEmpty() ? "ALL" : query, field, results.size(), elapsed / 1_000_000.0);
        algoInfoLabel.setText(info);
        algoInfoLabel.setForeground(UITheme.ACCENT_TEAL);
        if (statusBar != null) statusBar.setText("  Search complete: " + results.size() + " result(s)");
    }

    private void doSort() {
        String algo  = (String) sortAlgoCombo.getSelectedItem();
        String field = (String) sortFieldCombo.getSelectedItem();
        List<LibraryItem> base = currentList.isEmpty() ? new ArrayList<>(lm.getItems()) : currentList;

        long start = System.nanoTime();
        List<LibraryItem> sorted = SortEngine.sort(base, algo, field);
        long elapsed = System.nanoTime() - start;

        currentList = sorted;
        isSorted = "Title".equals(field);
        refreshTable();

        String info = String.format("  %s by %s — sorted %d items in %.2f ms",
            algo, field, sorted.size(), elapsed / 1_000_000.0);
        algoInfoLabel.setText(info);
        algoInfoLabel.setForeground(UITheme.ACCENT_GOLD);
        if (statusBar != null) statusBar.setText("  Sorted by " + field + " using " + algo);
    }

    private String algoShort(String full) {
        if (full.startsWith("Binary")) return "Binary";
        if (full.startsWith("Recursive")) return "Recursive";
        return "Linear";
    }

    private void refreshTable() {
        List<LibraryItem> list = currentList.isEmpty() && searchField != null && searchField.getText().trim().isEmpty()
            ? new ArrayList<>(lm.getItems()) : currentList;
        resultTable.populate(list);
        resultCount.setText("  Showing " + list.size() + " item(s)");
    }

    public void refresh() {
        currentList = new ArrayList<>(lm.getItems());
        isSorted = false;
        refreshTable();
    }
}
