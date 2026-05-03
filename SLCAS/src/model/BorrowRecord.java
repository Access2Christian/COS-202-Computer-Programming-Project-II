package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private String recordId;
    private String itemId;
    private String itemTitle;
    private String itemType;
    private String userId;
    private String userName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private static final double FINE_PER_DAY = 50.0; // Naira per day

    public BorrowRecord(String recordId, String itemId, String itemTitle, String itemType,
                        String userId, String userName, LocalDate borrowDate, LocalDate dueDate) {
        this.recordId = recordId;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.itemType = itemType;
        this.userId = userId;
        this.userName = userName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = false;
    }

    // Getters
    public String getRecordId() { return recordId; }
    public String getItemId() { return itemId; }
    public String getItemTitle() { return itemTitle; }
    public String getItemType() { return itemType; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }

    public void setReturned(boolean returned) { this.returned = returned; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isOverdue() {
        if (returned && returnDate != null) return returnDate.isAfter(dueDate);
        return LocalDate.now().isAfter(dueDate);
    }

    // Recursive overdue fine computation
    public double computeFine() {
        if (!isOverdue()) return 0.0;
        LocalDate checkDate = returned && returnDate != null ? returnDate : LocalDate.now();
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, checkDate);
        return computeFineRecursive(daysOverdue, 0.0);
    }

    private double computeFineRecursive(long daysRemaining, double accumulated) {
        if (daysRemaining <= 0) return accumulated;
        // First 3 days: base rate; 4-7: 1.5x; 8+: 2x
        double rate;
        long totalDays = ChronoUnit.DAYS.between(dueDate, returned && returnDate != null ? returnDate : LocalDate.now());
        long currentDay = totalDays - daysRemaining + 1;
        if (currentDay <= 3) rate = FINE_PER_DAY;
        else if (currentDay <= 7) rate = FINE_PER_DAY * 1.5;
        else rate = FINE_PER_DAY * 2.0;
        return computeFineRecursive(daysRemaining - 1, accumulated + rate);
    }

    public String getStatus() {
        if (returned) return isOverdue() ? "Returned (Late)" : "Returned";
        return isOverdue() ? "OVERDUE" : "Active";
    }

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%b",
                recordId, itemId, itemTitle.replace(",","；"),
                itemType, userId, userName.replace(",","；"),
                borrowDate, dueDate,
                returnDate != null ? returnDate : "null", returned);
    }

    public static BorrowRecord fromCSV(String line) {
        String[] p = line.split(",", 10);
        if (p.length < 10) return null;
        try {
            String rId = p[0]; String iId = p[1];
            String iTitle = p[2].replace("；",","); String iType = p[3];
            String uId = p[4]; String uName = p[5].replace("；",",");
            LocalDate bd = LocalDate.parse(p[6]); LocalDate dd = LocalDate.parse(p[7]);
            LocalDate rd = p[8].equals("null") ? null : LocalDate.parse(p[8]);
            boolean ret = Boolean.parseBoolean(p[9]);
            BorrowRecord r = new BorrowRecord(rId, iId, iTitle, iType, uId, uName, bd, dd);
            r.setReturned(ret);
            r.setReturnDate(rd);
            return r;
        } catch (Exception e) { return null; }
    }
}
