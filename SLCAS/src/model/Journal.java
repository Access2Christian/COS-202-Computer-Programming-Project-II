package model;

import java.time.LocalDate;

public class Journal extends LibraryItem implements Borrowable {
    private int volume;
    private int issue;
    private String doi;
    private String institution;
    private String field;
    private boolean available;
    private String borrowedBy;
    private LocalDate dueDate;

    public Journal(String id, String title, String author, int year, String category,
                   String description, int volume, int issue, String doi, String institution, String field) {
        super(id, title, author, year, category, description);
        this.volume = volume;
        this.issue = issue;
        this.doi = doi;
        this.institution = institution;
        this.field = field;
        this.available = true;
    }

    public int getVolume() { return volume; }
    public int getIssue() { return issue; }
    public String getDoi() { return doi; }
    public String getInstitution() { return institution; }
    public String getField() { return field; }
    public void setVolume(int v) { this.volume = v; }
    public void setIssue(int i) { this.issue = i; }
    public void setDoi(String d) { this.doi = d; }
    public void setInstitution(String i) { this.institution = i; }
    public void setField(String f) { this.field = f; }

    @Override
    public boolean borrow(String userId, LocalDate due) {
        if (available) {
            available = false;
            this.borrowedBy = userId;
            this.dueDate = due;
            incrementAccess();
            return true;
        }
        return false;
    }

    @Override
    public boolean returnItem() {
        if (!available) {
            available = true;
            this.borrowedBy = null;
            this.dueDate = null;
            return true;
        }
        return false;
    }

    @Override public boolean isAvailable() { return available; }
    @Override public String getBorrowedBy() { return borrowedBy; }
    @Override public LocalDate getDueDate() { return dueDate; }
    @Override public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }
    @Override public String getItemType() { return "Journal"; }

    @Override
    public String getDetailsString() {
        return String.format("Vol: %d | Issue: %d | DOI: %s | Institution: %s | Field: %s",
                volume, issue, doi, institution, field);
    }

    @Override
    public String toCSV() {
        return String.format("Journal,%s,%s,%s,%d,%s,%s,%d,%d,%s,%s,%s,%b",
                id, title.replace(",","；"), author.replace(",","；"),
                year, category, description.replace(",","；"),
                volume, issue, doi, institution, field, available);
    }

    public static Journal fromCSV(String line) {
        String[] p = line.split(",", 13);
        if (p.length < 13) return null;
        try {
            String id = p[1]; String title = p[2].replace("；",",");
            String author = p[3].replace("；",","); int year = Integer.parseInt(p[4]);
            String cat = p[5]; String desc = p[6].replace("；",",");
            int vol = Integer.parseInt(p[7]); int issue = Integer.parseInt(p[8]);
            String doi = p[9]; String inst = p[10]; String field = p[11];
            boolean avail = Boolean.parseBoolean(p[12]);
            Journal j = new Journal(id, title, author, year, cat, desc, vol, issue, doi, inst, field);
            if (!avail) j.available = false;
            return j;
        } catch (Exception e) { return null; }
    }
}
