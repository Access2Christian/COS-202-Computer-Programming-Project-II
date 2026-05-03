package model;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role; // Student, Staff, Faculty
    private String department;
    private List<BorrowRecord> borrowHistory;
    private boolean active;

    public UserAccount(String id, String name, String email, String phone, String role, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.department = department;
        this.borrowHistory = new ArrayList<>();
        this.active = true;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public List<BorrowRecord> getBorrowHistory() { return borrowHistory; }
    public boolean isActive() { return active; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
    public void setDepartment(String department) { this.department = department; }
    public void setActive(boolean active) { this.active = active; }

    public void addBorrowRecord(BorrowRecord record) {
        borrowHistory.add(record);
    }

    public int getActiveLoans() {
        return (int) borrowHistory.stream().filter(r -> !r.isReturned()).count();
    }

    public int getOverdueLoans() {
        return (int) borrowHistory.stream().filter(r -> !r.isReturned() && r.isOverdue()).count();
    }

    public double getTotalFines() {
        return borrowHistory.stream()
                .filter(r -> r.isOverdue())
                .mapToDouble(BorrowRecord::computeFine)
                .sum();
    }

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s,%b",
                id, name.replace(",","；"), email,
                phone, role, department.replace(",","；"), active);
    }

    public static UserAccount fromCSV(String line) {
        String[] p = line.split(",", 7);
        if (p.length < 7) return null;
        try {
            String id = p[0]; String name = p[1].replace("；",",");
            String email = p[2]; String phone = p[3];
            String role = p[4]; String dept = p[5].replace("；",",");
            boolean active = Boolean.parseBoolean(p[6]);
            UserAccount u = new UserAccount(id, name, email, phone, role, dept);
            u.setActive(active);
            return u;
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s | %s", name, id, role, department);
    }
}
