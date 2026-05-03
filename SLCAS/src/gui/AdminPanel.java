package gui;

import controller.*;
import model.*;
import utils.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

public class AdminPanel extends JPanel {
    private LibraryManager lm;
    private JLabel statusBar;
    private JTabbedPane adminTabs;
    private Runnable onDataChanged;

    // Users table
    private JTable usersTable;
    private DefaultTableModel usersModel;

    public AdminPanel(JLabel statusBar, Runnable onDataChanged) {
        this.lm = LibraryManager.getInstance();
        this.statusBar = statusBar;
        this.onDataChanged = onDataChanged;
        setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        header.setBackground(UITheme.BG_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        header.add(UITheme.makeLabel("\u2699\uFE0F  Admin Panel", UITheme.TEXT_PRIMARY, UITheme.FONT_HEADER));

        StyledButton btnUndo = new StyledButton("Undo Last Action", StyledButton.Style.WARNING);
        btnUndo.addActionListener(e -> {
            String result = lm.undoLastAction();
            JOptionPane.showMessageDialog(this, result, "Undo", JOptionPane.INFORMATION_MESSAGE);
            refreshUsers();
            if (onDataChanged != null) onDataChanged.run();
        });
        header.add(btnUndo);

        JLabel undoHint = UITheme.makeLabel("Stack size: " + lm.getUndoStack().size(), UITheme.TEXT_MUTED, UITheme.FONT_SMALL);
        header.add(undoHint);
        add(header, BorderLayout.NORTH);

        adminTabs = new JTabbedPane();
        adminTabs.setBackground(UITheme.BG_PRIMARY);
        adminTabs.setForeground(UITheme.TEXT_SECONDARY);
        adminTabs.addTab("Add Book",     buildAddBookPanel());
        adminTabs.addTab("Add Magazine", buildAddMagPanel());
        adminTabs.addTab("Add Journal",  buildAddJournalPanel());
        adminTabs.addTab("Delete Item",  buildDeletePanel());
        adminTabs.addTab("Manage Users", buildUsersPanel());
        add(adminTabs, BorderLayout.CENTER);
    }

    // ─── Add Book ──────────────────────────────────────────────────────────────
    private JPanel buildAddBookPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_PRIMARY);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        JTextField title   = field("Title"); addRow(form, gc, 0, "Title *", title);
        JTextField author  = field("Author"); addRow(form, gc, 1, "Author *", author);
        JTextField year    = field("Year"); addRow(form, gc, 2, "Year *", year);
        JComboBox<String> cat = UITheme.makeCombo("Computer Science","Software Engineering","Mathematics",
            "Physics","Chemistry","Database","Networking","Operating Systems","Artificial Intelligence","Other");
        addRow(form, gc, 3, "Category *", cat);
        JTextField isbn    = field("ISBN"); addRow(form, gc, 4, "ISBN", isbn);
        JTextField pub     = field("Publisher"); addRow(form, gc, 5, "Publisher", pub);
        JTextField copies  = field("Copies (default 1)"); addRow(form, gc, 6, "Total Copies", copies);
        JTextField edition = field("Edition (default 1)"); addRow(form, gc, 7, "Edition", edition);
        JTextField desc    = field("Description"); addRow(form, gc, 8, "Description", desc);

        gc.gridx = 0; gc.gridy = 9; gc.gridwidth = 2;
        StyledButton btnAdd = new StyledButton("  Add Book  ", StyledButton.Style.SUCCESS);
        btnAdd.addActionListener(e -> {
            try {
                String t = title.getText().trim(), a = author.getText().trim();
                if (t.isEmpty() || a.isEmpty()) { warn("Title and Author are required."); return; }
                int y = Integer.parseInt(year.getText().trim().isEmpty() ? "2024" : year.getText().trim());
                int c = Integer.parseInt(copies.getText().trim().isEmpty() ? "1" : copies.getText().trim());
                int ed = Integer.parseInt(edition.getText().trim().isEmpty() ? "1" : edition.getText().trim());
                Book book = new Book(IDGenerator.generateBookId(), t, a, y, (String) cat.getSelectedItem(),
                    desc.getText().trim(), isbn.getText().trim(), pub.getText().trim(), c, ed);
                lm.addItem(book);
                success("Book added: " + book.getId() + " — " + t);
                clear(title, author, year, isbn, pub, copies, edition, desc);
                if (onDataChanged != null) onDataChanged.run();
            } catch (NumberFormatException ex) { warn("Year and Copies must be numbers."); }
        });
        form.add(btnAdd, gc);

        JScrollPane sp = UITheme.styleScrollPane(form);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ─── Add Magazine ──────────────────────────────────────────────────────────
    private JPanel buildAddMagPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_PRIMARY);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6); gc.fill = GridBagConstraints.HORIZONTAL; gc.anchor = GridBagConstraints.WEST;

        JTextField title  = field("Title"); addRow(form, gc, 0, "Title *", title);
        JTextField editor = field("Editor/Author"); addRow(form, gc, 1, "Editor", editor);
        JTextField year   = field("Year"); addRow(form, gc, 2, "Year *", year);
        JTextField issue  = field("Issue Number"); addRow(form, gc, 3, "Issue Number", issue);
        JTextField vol    = field("Volume"); addRow(form, gc, 4, "Volume", vol);
        JTextField pub    = field("Publisher"); addRow(form, gc, 5, "Publisher", pub);
        JComboBox<String> freq = UITheme.makeCombo("Monthly","Weekly","Bimonthly","Quarterly","Annual");
        addRow(form, gc, 6, "Frequency", freq);
        JTextField desc = field("Description"); addRow(form, gc, 7, "Description", desc);

        gc.gridx = 0; gc.gridy = 8; gc.gridwidth = 2;
        StyledButton btnAdd = new StyledButton("  Add Magazine  ", StyledButton.Style.SUCCESS);
        btnAdd.addActionListener(e -> {
            try {
                String t = title.getText().trim();
                if (t.isEmpty()) { warn("Title is required."); return; }
                int y = Integer.parseInt(year.getText().trim().isEmpty() ? "2024" : year.getText().trim());
                int issueN = Integer.parseInt(issue.getText().trim().isEmpty() ? "1" : issue.getText().trim());
                int volN   = Integer.parseInt(vol.getText().trim().isEmpty() ? "1" : vol.getText().trim());
                Magazine mag = new Magazine(IDGenerator.generateMagazineId(), t, editor.getText().trim(),
                    y, "Magazine", desc.getText().trim(), issueN, volN,
                    pub.getText().trim(), (String) freq.getSelectedItem());
                lm.addItem(mag);
                success("Magazine added: " + mag.getId());
                clear(title, editor, year, issue, vol, pub, desc);
                if (onDataChanged != null) onDataChanged.run();
            } catch (NumberFormatException ex) { warn("Year, Issue, Volume must be numbers."); }
        });
        form.add(btnAdd, gc);
        JScrollPane sp = UITheme.styleScrollPane(form); sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ─── Add Journal ───────────────────────────────────────────────────────────
    private JPanel buildAddJournalPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_PRIMARY);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6); gc.fill = GridBagConstraints.HORIZONTAL; gc.anchor = GridBagConstraints.WEST;

        JTextField title = field("Title"); addRow(form, gc, 0, "Title *", title);
        JTextField auth  = field("Author(s)"); addRow(form, gc, 1, "Author(s)", auth);
        JTextField year  = field("Year"); addRow(form, gc, 2, "Year *", year);
        JTextField vol   = field("Volume"); addRow(form, gc, 3, "Volume", vol);
        JTextField issue = field("Issue"); addRow(form, gc, 4, "Issue", issue);
        JTextField doi   = field("DOI e.g. 10.1234/xyz"); addRow(form, gc, 5, "DOI", doi);
        JTextField inst  = field("Institution/Publisher"); addRow(form, gc, 6, "Institution", inst);
        JTextField fld   = field("Research Field"); addRow(form, gc, 7, "Field", fld);
        JTextField desc  = field("Abstract/Description"); addRow(form, gc, 8, "Description", desc);

        gc.gridx = 0; gc.gridy = 9; gc.gridwidth = 2;
        StyledButton btnAdd = new StyledButton("  Add Journal  ", StyledButton.Style.SUCCESS);
        btnAdd.addActionListener(e -> {
            try {
                String t = title.getText().trim();
                if (t.isEmpty()) { warn("Title is required."); return; }
                int y = Integer.parseInt(year.getText().trim().isEmpty() ? "2024" : year.getText().trim());
                int volN = Integer.parseInt(vol.getText().trim().isEmpty() ? "1" : vol.getText().trim());
                int issueN = Integer.parseInt(issue.getText().trim().isEmpty() ? "1" : issue.getText().trim());
                Journal j = new Journal(IDGenerator.generateJournalId(), t, auth.getText().trim(),
                    y, "Academic Journal", desc.getText().trim(), volN, issueN,
                    doi.getText().trim(), inst.getText().trim(), fld.getText().trim());
                lm.addItem(j);
                success("Journal added: " + j.getId());
                clear(title, auth, year, vol, issue, doi, inst, fld, desc);
                if (onDataChanged != null) onDataChanged.run();
            } catch (NumberFormatException ex) { warn("Year, Volume, Issue must be numbers."); }
        });
        form.add(btnAdd, gc);
        JScrollPane sp = UITheme.styleScrollPane(form); sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ─── Delete ────────────────────────────────────────────────────────────────
    private JPanel buildDeletePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10,10,10,10); gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0;
        p.add(UITheme.makeLabel("Item ID to delete:", UITheme.TEXT_SECONDARY, UITheme.FONT_BODY), gc);
        gc.gridx = 1;
        JTextField delId = field("e.g. BK1001");
        delId.setPreferredSize(new Dimension(200, 32));
        p.add(delId, gc);

        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 2;
        JLabel warn = UITheme.makeLabel("", UITheme.DANGER, UITheme.FONT_SMALL);
        p.add(warn, gc);

        gc.gridy = 2;
        StyledButton btnDel = new StyledButton("Delete Item", StyledButton.Style.DANGER);
        btnDel.addActionListener(e -> {
            String id = delId.getText().trim();
            if (id.isEmpty()) { warn.setText("Please enter an Item ID."); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete item " + id + "? This cannot be undone unless you use Undo.", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (lm.removeItem(id)) {
                    warn.setForeground(UITheme.SUCCESS);
                    warn.setText("Item " + id + " deleted. (Use 'Undo Last Action' to restore)");
                    delId.setText("");
                    if (onDataChanged != null) onDataChanged.run();
                } else {
                    warn.setForeground(UITheme.DANGER);
                    warn.setText("Item not found: " + id);
                }
            }
        });
        p.add(btnDel, gc);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UITheme.BG_PRIMARY);
        wrap.add(p, BorderLayout.CENTER);
        return wrap;
    }

    // ─── Users Panel ───────────────────────────────────────────────────────────
    private JPanel buildUsersPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_PRIMARY);

        // Add user form
        JPanel addForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        addForm.setBackground(UITheme.BG_SECONDARY);
        addForm.setBorder(UITheme.cardBorder("Add User"));

        JTextField nameF = field("Full Name"); nameF.setPreferredSize(new Dimension(130, 30));
        JTextField emailF = field("Email"); emailF.setPreferredSize(new Dimension(160, 30));
        JTextField phoneF = field("Phone"); phoneF.setPreferredSize(new Dimension(120, 30));
        JComboBox<String> roleC = UITheme.makeCombo("Student","Staff","Faculty");
        JTextField deptF = field("Department"); deptF.setPreferredSize(new Dimension(130, 30));

        addForm.add(UITheme.makeLabel("Name:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL));  addForm.add(nameF);
        addForm.add(UITheme.makeLabel("Email:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL)); addForm.add(emailF);
        addForm.add(UITheme.makeLabel("Phone:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL)); addForm.add(phoneF);
        addForm.add(UITheme.makeLabel("Role:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL));  addForm.add(roleC);
        addForm.add(UITheme.makeLabel("Dept:", UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL));  addForm.add(deptF);

        StyledButton btnAdd = new StyledButton("Add User", StyledButton.Style.SUCCESS);
        btnAdd.addActionListener(e -> {
            String n = nameF.getText().trim(), em = emailF.getText().trim();
            if (n.isEmpty()) { warn("Name is required."); return; }
            UserAccount u = new UserAccount(IDGenerator.generateUserId(), n, em,
                phoneF.getText().trim(), (String) roleC.getSelectedItem(), deptF.getText().trim());
            lm.addUser(u);
            refreshUsers();
            success("User added: " + u.getId() + " — " + n);
            clear(nameF, emailF, phoneF, deptF);
        });
        addForm.add(btnAdd);

        p.add(addForm, BorderLayout.NORTH);

        // Users table
        String[] cols = {"ID","Name","Email","Phone","Role","Department","Active Loans","Status"};
        usersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersModel);
        usersTable.setBackground(UITheme.BG_CARD);
        usersTable.setForeground(UITheme.TEXT_PRIMARY);
        usersTable.setGridColor(UITheme.BORDER);
        usersTable.setSelectionBackground(UITheme.ACCENT_BLUE);
        usersTable.setFont(UITheme.FONT_BODY);
        usersTable.setRowHeight(30);
        usersTable.setFillsViewportHeight(true);
        usersTable.getTableHeader().setBackground(UITheme.HEADER_BG);
        usersTable.getTableHeader().setForeground(UITheme.TEXT_SECONDARY);
        usersTable.getTableHeader().setFont(UITheme.FONT_SUBHEAD);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(UITheme.cardBorder("Registered Users"));
        tablePanel.add(UITheme.styleScrollPane(usersTable), BorderLayout.CENTER);
        p.add(tablePanel, BorderLayout.CENTER);

        refreshUsers();
        return p;
    }

    public void refreshUsers() {
        if (usersModel == null) return;
        usersModel.setRowCount(0);
        for (UserAccount u : lm.getUsers()) {
            String status = u.getOverdueLoans() > 0 ? "OVERDUE" : u.getActiveLoans() > 0 ? "Active" : "Clear";
            usersModel.addRow(new Object[]{
                u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                u.getRole(), u.getDepartment(), u.getActiveLoans(), status
            });
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────
    private JTextField field(String placeholder) {
        JTextField f = UITheme.makeField(placeholder, 14);
        f.setToolTipText(placeholder);
        return f;
    }

    private void addRow(JPanel p, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 0;
        p.add(UITheme.makeLabel(label, UITheme.TEXT_SECONDARY, UITheme.FONT_SMALL), gc);
        gc.gridx = 1; gc.weightx = 1;
        field.setPreferredSize(new Dimension(260, 30));
        p.add(field, gc);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void success(String msg) {
        if (statusBar != null) statusBar.setText("  \u2713 " + msg);
    }

    private void clear(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
}
