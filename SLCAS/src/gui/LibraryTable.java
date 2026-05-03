package gui;

import model.LibraryItem;
import model.Borrowable;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class LibraryTable extends JTable {
    private DefaultTableModel model;

    private static final String[] COLUMNS = {
        "ID", "Type", "Title", "Author", "Year", "Category", "Status", "Access"
    };

    public LibraryTable() {
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        setModel(model);
        styleTable();
    }

    private void styleTable() {
        setBackground(UITheme.BG_CARD);
        setForeground(UITheme.TEXT_PRIMARY);
        setGridColor(UITheme.BORDER);
        setSelectionBackground(UITheme.ACCENT_BLUE);
        setSelectionForeground(Color.WHITE);
        setFont(UITheme.FONT_BODY);
        setRowHeight(34);
        setShowVerticalLines(true);
        setShowHorizontalLines(true);
        setIntercellSpacing(new Dimension(0, 1));
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header
        JTableHeader header = getTableHeader();
        header.setBackground(UITheme.HEADER_BG);
        header.setForeground(UITheme.TEXT_SECONDARY);
        header.setFont(UITheme.FONT_SUBHEAD);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UITheme.BORDER_LIGHT));

        // Column widths
        int[] widths = {70, 75, 240, 160, 60, 130, 95, 60};
        for (int i = 0; i < widths.length && i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Custom renderers
        setDefaultRenderer(Object.class, new LibraryItemRenderer());
    }

    public void populate(List<LibraryItem> items) {
        model.setRowCount(0);
        for (LibraryItem item : items) {
            String status = "N/A";
            if (item instanceof Borrowable) {
                Borrowable b = (Borrowable) item;
                if (b.isAvailable()) status = "Available";
                else if (b.isOverdue()) status = "Overdue";
                else status = "Borrowed";
            }
            model.addRow(new Object[]{
                item.getId(), item.getItemType(), item.getTitle(),
                item.getAuthor(), item.getYear(), item.getCategory(),
                status, item.getAccessCount()
            });
        }
    }

    public String getSelectedId() {
        int row = getSelectedRow();
        if (row < 0) return null;
        return (String) model.getValueAt(row, 0);
    }

    // ─── Custom Row Renderer ───────────────────────────────────────────────────
    private static class LibraryItemRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setFont(UITheme.FONT_BODY);

            if (isSelected) {
                setBackground(UITheme.ACCENT_BLUE);
                setForeground(Color.WHITE);
            } else {
                setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ALT);
                setForeground(UITheme.TEXT_PRIMARY);
            }

            // Type badge styling
            if (col == 1) {
                String type = value != null ? value.toString() : "";
                setHorizontalAlignment(CENTER);
                setText(type);
                switch (type) {
                    case "Book":     setForeground(isSelected ? Color.WHITE : UITheme.ACCENT_BLUE);   break;
                    case "Magazine": setForeground(isSelected ? Color.WHITE : UITheme.ACCENT_TEAL);   break;
                    case "Journal":  setForeground(isSelected ? Color.WHITE : UITheme.ACCENT_GOLD);   break;
                }
            } else if (col == 6) {
                // Status column
                String status = value != null ? value.toString() : "";
                setHorizontalAlignment(CENTER);
                if (!isSelected) {
                    switch (status) {
                        case "Available": setForeground(UITheme.SUCCESS); break;
                        case "Overdue":   setForeground(UITheme.DANGER);  break;
                        case "Borrowed":  setForeground(UITheme.WARNING);  break;
                        default:          setForeground(UITheme.TEXT_MUTED);
                    }
                }
            } else if (col == 7) {
                setHorizontalAlignment(CENTER);
                setForeground(isSelected ? Color.WHITE : UITheme.TEXT_SECONDARY);
            } else if (col == 4) {
                setHorizontalAlignment(CENTER);
            } else {
                setHorizontalAlignment(LEFT);
            }

            return this;
        }
    }
}
