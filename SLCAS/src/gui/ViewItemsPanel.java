package gui;

import controller.*;
import model.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class ViewItemsPanel extends JPanel {
    private LibraryManager lm;
    private LibraryTable table;
    private JLabel countLabel;
    private JComboBox<String> filterType;
    private JTextField filterField;
    private JLabel statusBar;

    public ViewItemsPanel(JLabel statusBar) {
        this.lm = LibraryManager.getInstance();
        this.statusBar = statusBar;
        setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ── Top toolbar ──────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(UITheme.BG_SECONDARY);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));

        JLabel title = UITheme.makeLabel("  \uD83D\uDCDA  Library Catalogue", UITheme.TEXT_PRIMARY, UITheme.FONT_HEADER);
        toolbar.add(title);

        toolbar.add(Box.createHorizontalStrut(20));

        JLabel fl = UITheme.makeLabel("Filter:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
        filterType = UITheme.makeCombo("All Types", "Book", "Magazine", "Journal");
        filterField = UITheme.makeField("Search title/author...", 18);
        filterField.setPreferredSize(new Dimension(200, 32));

        toolbar.add(fl);
        toolbar.add(filterType);
        toolbar.add(filterField);

        StyledButton btnFilter = new StyledButton("Filter", StyledButton.Style.GHOST);
        btnFilter.setToolTipText("Apply filter (Enter)");
        btnFilter.addActionListener(e -> applyFilter());
        toolbar.add(btnFilter);

        StyledButton btnClear = new StyledButton("Clear", StyledButton.Style.GHOST);
        btnClear.addActionListener(e -> { filterField.setText(""); filterType.setSelectedIndex(0); refresh(); });
        toolbar.add(btnClear);

        toolbar.add(Box.createHorizontalStrut(30));
        countLabel = UITheme.makeLabel("0 items", UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        toolbar.add(countLabel);

        filterField.addActionListener(e -> applyFilter());

        add(toolbar, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────────
        table = new LibraryTable();
        JScrollPane sp = UITheme.styleScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(sp, BorderLayout.CENTER);

        // ── Detail panel ─────────────────────────────────────────────────
        JPanel detailPanel = buildDetailPanel();
        add(detailPanel, BorderLayout.SOUTH);

        // Row click shows details
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails();
        });
    }

    private JTextArea detailArea;

    private JPanel buildDetailPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(UITheme.BG_SECONDARY);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        p.setPreferredSize(new Dimension(0, 80));

        JLabel lbl = UITheme.makeLabel("Details:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
        p.add(lbl, BorderLayout.WEST);

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setBackground(UITheme.BG_SECONDARY);
        detailArea.setForeground(UITheme.TEXT_PRIMARY);
        detailArea.setFont(UITheme.FONT_MONO);
        detailArea.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        p.add(detailArea, BorderLayout.CENTER);
        return p;
    }

    private void showDetails() {
        String id = table.getSelectedId();
        if (id == null) { detailArea.setText(""); return; }
        LibraryItem item = SearchEngine.linearSearchById(lm.getItems(), id);
        if (item == null) return;
        detailArea.setText(item.toString() + "\n" + item.getDetailsString());
        if (statusBar != null) statusBar.setText("  Selected: " + item.getTitle());
    }

    private void applyFilter() {
        String typeFilter = (String) filterType.getSelectedItem();
        String textFilter = filterField.getText().trim();
        List<LibraryItem> result = new ArrayList<>(lm.getItems());

        if (!"All Types".equals(typeFilter)) {
            result = SearchEngine.linearSearchByType(result, typeFilter);
        }
        if (!textFilter.isEmpty()) {
            result = SearchEngine.linearSearchByTitle(result, textFilter);
            if (result.isEmpty()) {
                result = SearchEngine.linearSearchByAuthor(lm.getItems(), textFilter);
                if (!"All Types".equals(typeFilter))
                    result = SearchEngine.linearSearchByType(result, typeFilter);
            }
        }
        table.populate(result);
        countLabel.setText(result.size() + " item(s)");
    }

    public void refresh() {
        List<LibraryItem> items = lm.getItems();
        table.populate(items);
        countLabel.setText(items.size() + " item(s)");
    }
}
