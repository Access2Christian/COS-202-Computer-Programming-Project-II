package model;

import java.time.LocalDate;

public class Magazine extends LibraryItem implements Borrowable {
    private int issueNumber;
    private int volume;
    private String publisher;
    private String frequency; // Monthly, Weekly, etc.
    private boolean available;
    private String borrowedBy;
    private LocalDate dueDate;

    public Magazine(String id, String title, String author, int year, String category,
                    String description, int issueNumber, int volume, String publisher, String frequency) {
        super(id, title, author, year, category, description);
        this.issueNumber = issueNumber;
        this.volume = volume;
        this.publisher = publisher;
        this.frequency = frequency;
        this.available = true;
    }

    public int getIssueNumber() { return issueNumber; }
    public int getVolume() { return volume; }
    public String getPublisher() { return publisher; }
    public String getFrequency() { return frequency; }
    public void setIssueNumber(int n) { this.issueNumber = n; }
    public void setVolume(int v) { this.volume = v; }
    public void setPublisher(String p) { this.publisher = p; }
    public void setFrequency(String f) { this.frequency = f; }

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
    @Override public String getItemType() { return "Magazine"; }

    @Override
    public String getDetailsString() {
        return String.format("Vol: %d | Issue: %d | Publisher: %s | Frequency: %s",
                volume, issueNumber, publisher, frequency);
    }

    @Override
    public String toCSV() {
        return String.format("Magazine,%s,%s,%s,%d,%s,%s,%d,%d,%s,%s,%b",
                id, title.replace(",","；"), author.replace(",","；"),
                year, category, description.replace(",","；"),
                issueNumber, volume, publisher, frequency, available);
    }

    public static Magazine fromCSV(String line) {
        String[] p = line.split(",", 12);
        if (p.length < 12) return null;
        try {
            String id = p[1]; String title = p[2].replace("；",",");
            String author = p[3].replace("；",","); int year = Integer.parseInt(p[4]);
            String cat = p[5]; String desc = p[6].replace("；",",");
            int issue = Integer.parseInt(p[7]); int vol = Integer.parseInt(p[8]);
            String pub = p[9]; String freq = p[10];
            boolean avail = Boolean.parseBoolean(p[11]);
            Magazine m = new Magazine(id, title, author, year, cat, desc, issue, vol, pub, freq);
            if (!avail) m.available = false;
            return m;
        } catch (Exception e) { return null; }
    }
}
