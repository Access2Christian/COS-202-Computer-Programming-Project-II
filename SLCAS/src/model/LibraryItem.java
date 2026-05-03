package model;

public abstract class LibraryItem {
    protected String id;
    protected String title;
    protected String author;
    protected int year;
    protected String category;
    protected String description;
    protected int accessCount;

    public LibraryItem(String id, String title, String author, int year, String category, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.category = category;
        this.description = description;
        this.accessCount = 0;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getAccessCount() { return accessCount; }
    public void incrementAccess() { accessCount++; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }

    public abstract String getItemType();
    public abstract String getDetailsString();
    public abstract String toCSV();

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%d) - %s", getItemType(), title, author, year, category);
    }
}
