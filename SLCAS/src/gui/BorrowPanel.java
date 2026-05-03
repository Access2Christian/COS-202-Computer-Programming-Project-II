package gui;

import controller.*;
import model.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class BorrowPanel extends JPanel {
    private LibraryManager lm;
    private JLabel statusBar;

    // Borrow section
    private JTextField itemIdField, userIdField;
    private JComboBox<String> loanDaysCombo;
    private JTextArea borrowResultArea;

    // Active loans table
    private JTable loansTable;
    private DefaultTableModel loansModel;

    // Return section
    private JTextField returnItemField, returnUserField;

    // Queue table
    private JTable queueTable;
    private DefaultTableModel queueModel;

    public BorrowPanel(JLabel statusBar) {
        this.lm = LibraryManager.getInstance();
        this.statusBar = statusBar;
        setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        buildUI();
        refreshLoans();
    }

    private void buildUI() {
        // Top: split between Borrow form and Return form
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);
        topRow.add(buildBorrowCard());
        topRow.add(buildReturnCard());
        add(topRow, BorderLayout.NORTH);

        // Center: active loans + reservation queue
        JPanel center = new JPanel(new GridLayout(1, 2, 14, 0));
        center.setOpaque(false);
        center.add(buildLoansTable());
        center.add(buildQueuePanel());
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildBorrowCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.cardBorder("Borrow Item"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(card, gc, 0, "Item ID:", itemIdField = UITheme.makeField("e.g. BK1001", 14));
        addFormRow(card, gc, 1, "User ID:", userIdField = UITheme.makeField("e.g. USR100", 14));

        loanDaysCombo = UITheme.makeCombo("7 days", "14 days", "21 days", "30 days");
        addFormRow(card, gc, 2, "Loan Period:", loanDaysCombo);

        StyledButton btnBorrow = new StyledButton("Borrow Item", StyledButton.Style.SUCCESS);
        btnBorrow.setPreferredSize(new Dimension(140, 34));

        StyledButton btnReserve = new StyledButton("Reserve", StyledButton.Style.GHOST);
        btnReserve.setToolTipText("Add to reservation queue if item is unavailable");

        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnBorrow);
        btnRow.add(btnReserve);
        card.add(btnRow, gc);

        gc.gridy = 4; gc.weightx = 1; gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        borrowResultArea = new JTextArea(3, 20);
        borrowResultArea.setEditable(false);
        borrowResultArea.setBackground(UITheme.BG_SECONDARY);
        borrowResultArea.setForeground(UITheme.SUCCESS);
        borrowResultArea.setFont(UITheme.FONT_MONO);
        borrowResultArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        card.add(UITheme.styleScrollPane(borrowResultArea), gc);

        btnBorrow.addActionListener(e -> doBorrow());
        btnReserve.addActionListener(e -> doReserve());

        return card;
    }

    private JPanel buildReturnCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(UITheme.cardBorder("Return Item"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(card, gc, 0, "Item ID:", returnItemField = UITheme.makeField("e.g. BK1001", 14));
        addFormRow(card, gc, 1, "User ID:", returnUserField = UITheme.makeField("e.g. USR100", 14));

        StyledButton btnReturn = new StyledButton("Return Item", StyledButton.Style.WARNING);

        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnReturn);
        card.add(btnRow, gc);

        // Quick lookup
        gc.gridy = 3; gc.weighty = 0;
        JLabel tip = UITheme.makeLabel("Active loans are shown in the table below.", UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        card.add(tip, gc);

        // Active loans info text area
        gc.gridy = 4; gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setBackground(UITheme.BG_SECONDARY);
        info.setForeground(UITheme.TEXT_SECONDARY);
        info.setFont(UITheme.FONT_MONO);
        info.setText("Click a row in the Active Loans table\nto autofill Item ID and User ID.");
        info.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        card.add(UITheme.styleScrollPane(info), gc);

        btnReturn.addActionListener(e -> doReturn());

        return card;
    }

    private JPanel buildLoansTable() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setBorder(UITheme.cardBorder("Active Loans"));

        String[] cols = {"Record ID", "Item", "Type", "User", "Due Date", "Status"};
        loansModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        loansTable = buildStyledTable(loansModel);
        loansTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        loansTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        loansTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        loansTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Click to autofill return fields
        loansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = loansTable.getSelectedRow();
                if (row >= 0) {
                    // Extract item+user from record ID
                    String recId = (String) loansModel.getValueAt(row, 0);
                    for (BorrowRecord r : lm.getAllRecords()) {
                        if (r.getRecordId().equals(recId)) {
                            returnItemField.setText(r.getItemId());
                            returnUserField.setText(r.getUserId());
                            break;
                        }
                    }
                }
            }
        });

        p.add(UITheme.styleScrollPane(loansTable), BorderLayout.CENTER);

        StyledButton btnRefresh = new StyledButton("Refresh", StyledButton.Style.GHOST);
        btnRefresh.addActionListener(e -> refreshLoans());
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        foot.setOpaque(false);
        foot.add(btnRefresh);
        p.add(foot, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildQueuePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setBorder(UITheme.cardBorder("Reservation Queue"));

        String[] cols = {"Item ID", "User ID", "Date Added"};
        queueModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        queueTable = buildStyledTable(queueModel);
        p.add(UITheme.styleScrollPane(queueTable), BorderLayout.CENTER);

        refreshQueue();
        return p;
    }

    private JTable buildStyledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setBackground(UITheme.BG_CARD);
        t.setForeground(UITheme.TEXT_PRIMARY);
        t.setGridColor(UITheme.BORDER);
        t.setSelectionBackground(UITheme.ACCENT_BLUE);
        t.setSelectionForeground(Color.WHITE);
        t.setFont(UITheme.FONT_BODY);
        t.setRowHeight(30);
        t.setShowVerticalLines(true);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setBackground(UITheme.HEADER_BG);
        t.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);
        t.getTableHeader().setFont(UITheme.FONT_SUBHEAD);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSel) {
                    setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ALT);
                    setForeground(UITheme.TEXT_PRIMARY);
                    // Status column coloring
                    String s = val != null ? val.toString() : "";
                    if (s.equals("OVERDUE")) { setForeground(UITheme.DANGER); setFont(UITheme.FONT_SUBHEAD); }
                    else if (s.equals("Active")) setForeground(UITheme.SUCCESS);
                    else if (s.contains("Late")) setForeground(UITheme.WARNING);
                }
                return this;
            }
        });
        return t;
    }

    private void addFormRow(JPanel p, GridBagConstraints gc, int row, String labelText, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 0; gc.weighty = 0;
        gc.fill = GridBagConstraints.NONE;
        JLabel lbl = UITheme.makeLabel(labelText, UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL);
        p.add(lbl, gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        if (field instanceof JTextField) ((JTextField) field).setPreferredSize(new Dimension(160, 32));
        p.add(field, gc);
    }

    private void doBorrow() {
        String itemId = itemIdField.getText().trim();
        String userId = userIdField.getText().trim();
        if (itemId.isEmpty() || userId.isEmpty()) {
            borrowResultArea.setForeground(UITheme.DANGER);
            borrowResultArea.setText("Please fill Item ID and User ID.");
            return;
        }
        int days;
        try { days = Integer.parseInt(((String)loanDaysCombo.getSelectedItem()).split(" ")[0]); }
        catch (Exception ex) { days = 14; }

        BorrowRecord rec = lm.borrowItem(itemId, userId, days);
        if (rec != null) {
            borrowResultArea.setForeground(UITheme.SUCCESS);
            borrowResultArea.setText("SUCCESS! Item borrowed.\nRecord: " + rec.getRecordId() +
                "\nDue: " + rec.getDueDate());
            if (statusBar != null) statusBar.setText("  Borrowed: " + rec.getItemTitle() + " — Due: " + rec.getDueDate());
            refreshLoans();
            itemIdField.setText(""); userIdField.setText("");
        } else {
            borrowResultArea.setForeground(UITheme.DANGER);
            LibraryItem item = SearchEngine.linearSearchById(lm.getItems(), itemId);
            if (item == null) borrowResultArea.setText("Item not found: " + itemId);
            else if (lm.getUserById(userId) == null) borrowResultArea.setText("User not found: " + userId);
            else borrowResultArea.setText("Item is unavailable.\nTip: Use Reserve to join waitlist.");
        }
    }

    private void doReserve() {
        String itemId = itemIdField.getText().trim();
        String userId = userIdField.getText().trim();
        if (itemId.isEmpty() || userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill Item ID and User ID first.", "Reserve", JOptionPane.WARNING_MESSAGE);
            return;
        }
        lm.addReservation(itemId, userId);
        refreshQueue();
        JOptionPane.showMessageDialog(this, "Added to reservation queue!", "Reserved", JOptionPane.INFORMATION_MESSAGE);
        if (statusBar != null) statusBar.setText("  Reservation added for item " + itemId);
    }

    private void doReturn() {
        String itemId = returnItemField.getText().trim();
        String userId = returnUserField.getText().trim();
        if (itemId.isEmpty() || userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill Item ID and User ID.", "Return", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = lm.returnItem(itemId, userId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Item returned successfully!", "Return Successful", JOptionPane.INFORMATION_MESSAGE);
            if (statusBar != null) statusBar.setText("  Item returned: " + itemId);
            refreshLoans();
            returnItemField.setText(""); returnUserField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Return failed. Check Item ID / User ID.", "Return Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshLoans() {
        loansModel.setRowCount(0);
        for (BorrowRecord r : lm.getAllRecords()) {
            if (!r.isReturned()) {
                loansModel.addRow(new Object[]{
                    r.getRecordId(), r.getItemTitle(), r.getItemType(),
                    r.getUserName(), r.getDueDate(), r.getStatus()
                });
            }
        }
        refreshQueue();
    }

    private void refreshQueue() {
        queueModel.setRowCount(0);
        for (String[] q : lm.getAllReservations()) {
            queueModel.addRow(new Object[]{ q[0], q[1], q[2] });
        }
    }
}
