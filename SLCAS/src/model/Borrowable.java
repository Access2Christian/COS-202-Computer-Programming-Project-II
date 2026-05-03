package model;

import java.time.LocalDate;

public interface Borrowable {
    boolean borrow(String userId, LocalDate dueDate);
    boolean returnItem();
    boolean isAvailable();
    String getBorrowedBy();
    LocalDate getDueDate();
    boolean isOverdue();
}
