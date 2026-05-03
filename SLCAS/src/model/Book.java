package model;

import java.time.LocalDate;

public class Book extends LibraryItem implements Borrowable {
    private String isbn;
    private String publisher;
    private int totalCopies;
    private int availableCopies;
    private String borrowedBy;
    private LocalDate dueDate;
    private int edition;

    public Book(String id, String title, String author, int year, String category,
                String description, String isbn, String publisher, int totalCopies, int edition) {
        super(id, title, author, year, category, description);
        this.isbn = isbn;
        this.publisher = publisher;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.edition = edition;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getPublisher() { return publisher; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public int getEdition() { return edition; }

    // Setters
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public void setEdition(int edition) { this.edition = edition; }

    @Override
    public boolean borrow(String userId, LocalDate due) {
        if (availableCopies > 0) {
            availableCopies--;
            this.borrowedBy = userId;
            this.dueDate = due;
            incrementAccess();
            return true;
        }
        return false;
    }

    @Override
    public boolean returnItem() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            this.borrowedBy = null;
            this.dueDate = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean isAvailable() { return availableCopies > 0; }

    @Override
    public String getBorrowedBy() { return borrowedBy; }

    @Override
    public LocalDate getDueDate() { return dueDate; }

    @Override
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String getItemType() { return "Book"; }

    @Override
    public String getDetailsString() {
        return String.format("ISBN: %s | Publisher: %s | Edition: %d | Copies: %d/%d",
                isbn, publisher, edition, availableCopies, totalCopies);
    }

    @Override
    public String toCSV() {
        return String.format("Book,%s,%s,%s,%d,%s,%s,%s,%s,%d,%d,%d",
                id, title.replace(",","；"), author.replace(",","；"),
                year, category, description.replace(",","；"),
                isbn, publisher, totalCopies, availableCopies, edition);
    }

    public static Book fromCSV(String line) {
        String[] p = line.split(",", 12);
        if (p.length < 12) return null;
        try {
            String id = p[1]; String title = p[2].replace("；",",");
            String author = p[3].replace("；",","); int year = Integer.parseInt(p[4]);
            String cat = p[5]; String desc = p[6].replace("；",",");
            String isbn = p[7]; String pub = p[8];
            int total = Integer.parseInt(p[9]); int avail = Integer.parseInt(p[10]);
            int ed = Integer.parseInt(p[11]);
            Book b = new Book(id, title, author, year, cat, desc, isbn, pub, total, ed);
            b.setAvailableCopies(avail);
            return b;
        } catch (Exception e) { return null; }
    }
}
